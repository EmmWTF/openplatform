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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by jackiechan on 19-12-31 上午9:34
 * 限制每个app能访问的每个接口的次数
 * 必须知道当前app是谁,必须知道访问的接口是谁,必须知道这个用户当前接口剩余的次数是多少
 * 我们约定次数肯定是在数据库中存放的,但是为了降低数据库压力,我们在缓存中放了一份,但是这个次数是针对某个用户的某个接口的,比如我们以接口:用户为key 次数为值放入redis中
 *
 * @Author jackiechan
 */
@Component
public class LimitFilter extends ZuulFilter {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 50;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String method = request.getParameter("method");
        //根据当前请求的接口,获取到要通过的过滤器,然后判断当前过滤器在不在里面,在就执行(注意到底执行不执行还取决于其他条件)
        //  Map<Object, Object> apiinfo = cacheService.hGetAll(SystemParams.METHOD_REDIS_PRE + method);
        //  Object filters = apiinfo.get("filters");//获取所有需要经过的过滤器
        //  String filters = cacheService.hGet(SystemParams.METHOD_REDIS_PRE + method, "filters");
        String filters = null;
        Map<Object, Object> apiinfo = SystemParams.ALL_APIINFO.get(SystemParams.METHOD_REDIS_PRE + method);//获取当前借口的信息
        if (apiinfo != null) {
            filters = (String) apiinfo.get("filters");//获取接口的过滤器信息
        }else{
            filters = cacheService.hGet(SystemParams.METHOD_REDIS_PRE + method, "filters");//本地没找到,去查询一次redis
        }
        boolean contains = filters == null ? false : filters.contains(getClass().getName());//判断所有的过滤器中包含不包含当前过滤器
        return RequestContext.getCurrentContext().sendZuulResponse() && contains;
    }

    @Override
    public Object run() throws ZuulException {
        //获取到当前app是谁
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String app_key = request.getParameter("app_key");
        //获取到当前app要请求的接口,因为我们通过method来映射的接口地址,所以method可以做为地址的代替
        String method = request.getParameter("method");
        //根据上面两个条件去查询缓存,看看用户剩余多少次数,我们不需要先获取次数然后判断,然后再减去1设置回去,我们只需要通过redis的自减功能获取减掉之后的值 判断一下就可以了

        Long num = cacheService.getDecrementNum(method + ":" + app_key, -1);
        //如果不足,返回提示
        if (num < 0) {
            BaseJsonBean baseJsonBean = new BaseJsonBean();
            baseJsonBean.setCode(SystemParams.LIMIT_ERROR);
            baseJsonBean.setMsg("剩余次数不足,请充值后再试");
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
