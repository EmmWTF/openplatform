package com.qianfeng.openapi.gateway.filter;

//
//                            _ooOoo_  
//                           o8888888o  
//                           88" . "88  
//                           (| -_- |)  
//                            O\ = /O  
//                        ____/`---'\____  
//                      .   ' \\| |// `.  
//                       / \\||| : |||// \  
//                     / _||||| -:- |||||- \  
//                       | | \\\ - /// | |  
//                     | \_| ''\---/'' | |  
//                      \ .-\__ `-` ___/-. /  
//                   ___`. .' /--.--\ `. . __  
//                ."" '< `.___\_<|>_/___.' >'"".  
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |  
//                 \ \ `-. \_ __\ /__ _/ .-` / /  
//         ======`-.____`-.___\_____/___.-`____.-'======  
//                            `=---='  
//  
//         .............................................  
//                  佛祖镇楼                  BUG辟易  
//          佛曰:  
//                  写字楼里写字间，写字间里程序员；  
//                  程序人员写程序，又拿程序换酒钱。  
//                  酒醒只在网上坐，酒醉还来网下眠；  
//                  酒醉酒醒日复日，网上网下年复年。  
//                  但愿老死电脑间，不愿鞠躬老板前；  
//                  奔驰宝马贵者趣，公交自行程序员。  
//                  别人笑我忒疯癫，我笑自己命太贱；  
//  


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.qianfeng.openapi.gateway.bean.BaseJsonBean;
import com.qianfeng.openapi.gateway.constans.SystemParams;
import com.qianfeng.openapi.gateway.feign.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * Created by jackiechan on 19-12-30 上午11:15
 * 此过滤器用于对重复请求进行校验,相同的请求不允许发生第二次,我们判断请求是否相同的标准就是sign数据有没有出现过
 *
 * @Author jackiechan
 */
@Component
public class IdempotentsFilter extends ZuulFilter {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CacheService cacheService;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    //放在时间戳和签名校验的之间,因为时间戳是帮我们拦截一分钟以内的请求的,这个过滤器是防止一分钟内重复请求的,只有不是重复请求的才有必要继续下去进行校验
    @Override
    public int filterOrder() {
        return 25;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
       // return context.sendZuulResponse();
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        //获取到请求的签名
        String sign = context.getRequest().getParameter("sign");
        //去判断下这个签名出现过没有,凡是判断就是两个数据比较,数据到底在哪,另外一个数据肯定在某个地方存放这
        //我们约定好以签名的值作为key,随便放个数据到redis中,并设置有效期和时间戳的有效期一致,下次我们只要看看有没有这个key就知道这个数据出现过没有
        //因为前面有一个时间戳的过滤器可以帮我们把请求限制在一分钟以内,所以只要在一分钟以内的请求才会进入到这里,那么我们只要设置key的有效期为1分钟,只要能进来,就说明要么这个key不存在,要么就一定还在有效期里面
        String info = cacheService.getFromRedis(sign);//获取签名在redis中的数据,这个数据没有任何意义,只要不为空就说明存在,空就说明不存在
        //如果没有出现过  放行

        //出现过就拦截
        if (info != null) {
            //否则拦截
            context.setSendZuulResponse(false);
            BaseJsonBean baseJsonBean = new BaseJsonBean();
            baseJsonBean.setCode(SystemParams.SIGN_ALLREADY_USED);
            baseJsonBean.setMsg("请不要重复发起请求");
            context.getResponse().setContentType("application/json;charset=utf-8");
            try {
                context.setResponseBody(objectMapper.writeValueAsString(baseJsonBean));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            context.setSendZuulResponse(false);
        }
        return null;
    }
}
