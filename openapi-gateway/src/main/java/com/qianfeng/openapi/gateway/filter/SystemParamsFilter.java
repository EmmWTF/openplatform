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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.qianfeng.openapi.gateway.bean.BaseJsonBean;
import com.qianfeng.openapi.gateway.constans.SystemParams;
import com.qianfeng.openapi.gateway.feign.CacheService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-28 上午10:10
 * 我们的项目中要求所以的请求都必须传递特定的公共参数,这个过滤器的主要目的是对公共参数进行校验
 * 问题1:公共参数我们要如何判断有没有传递,很简单,获取一下这个参数有没有值就可以了,那就是通过request获取参数
 * 问题2:到底要判断哪些参数有没有传递,需求文档已经告诉我们清清楚楚,但是这个数据其实可能会变化,如果我们在代码中声明死了所有的属性,就一点都无法改变
 * 需要改变的时候需要重新修改代码,按照设计模式来说,这是不行的,我们要对修改关闭,对扩展开放
 * 按照我们上面的分析,我们将数据写在代码中,其实保存在代码中和保存在数据库中是没有区别的,我们可以考虑,通过查询数据库来获取到我们需要过滤的参数名
 * 这样子我们需要改变的时候只需要改变数据库数据就行了,但是这个访问量可能会很大,所以我们可以保存在缓存中,做一个数据异构(异构就是将数据存放到不同的存储结构中,比如数据库和redis两种方式)
 *
 * @Author jackiechan
 */
@Component
public class SystemParamsFilter extends ZuulFilter {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ObjectMapper objectMapper;//生成josn 的工具对象


    //  private String[] all =new String[]{"name","password"};//定义所有需要判断的参数名
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        //获取到需要判断的参数名

        //获取到请求对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        //遍历我们需要判断的参数名,挨个从request中获取一下就行
        //参数名如何获取,从缓存或者是数据库中获取,比如我们此处要求从缓存中,我们这里应该是通过redis查询数据
        //redis的key是什么 ,key必须是唯一的,第二个我们要区分这个key是和用户相关还是和具体操作相关还是说是一个针对全局的
        //按照我们的分析,这个参数是针对所有的请求的.不区分用户,不区分操作,所以这个key可以写一个固定值
        Set<String> public_parmas = SystemParams.ALL_PARAMS;//修改为做本地缓存了,通过监听器来实现启动加载的
        if (public_parmas == null || public_parmas.size() == 0) {
        //防止在监听器中出现了问题导致没有加载数据,重新加载一份
            public_parmas = cacheService.sMembers(SystemParams.PUBLIC_PARMAS);//从redis中获取公共参数
            SystemParams.ALL_PARAMS.addAll(public_parmas);

        }
        if (public_parmas != null) {
            for (String parma : public_parmas) {
                String parameter = request.getParameter(parma);//获取当前遍历到的参数名对应的值
                if (parameter == null || "".equals(parameter.trim())) {
                    //空的
                    //如果没有传递,拦截请求,返回我们指定的错误信息
                    context.setSendZuulResponse(false);//拦截请求
                    BaseJsonBean baseJsonBean = new BaseJsonBean();
                    baseJsonBean.setCode(SystemParams.PARAM_NULL);
                    baseJsonBean.setMsg("参数:" + parma + " 的值不能为空");
                    //上面是个对象,如何返回
                    String json = objectMapper.writeValueAsString(baseJsonBean);
                    context.setResponseBody(json);
                    context.getResponse().setContentType("application/json;charset=utf-8");
                    return null;//只要有一个参数不符合就直接返回,没必要继续向下遍历
                }

            }
        }


        return null;
    }


}
