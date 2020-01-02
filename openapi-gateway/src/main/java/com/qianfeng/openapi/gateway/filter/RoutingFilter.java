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


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.qianfeng.openapi.gateway.constans.SystemParams;
import com.qianfeng.openapi.gateway.feign.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by jackiechan on 19-12-30 下午3:24
 * 这是一个动态路由的过滤器,规则是这样的,我们以后对外开放的服务可能会有很多,可能会增加或者减少,如何在不重启网关的情况下让用户可以直接访问这个服务
 * 我们需要定义一个规则,我们的网关可以从eukrea中动态获取到最新的服务,网关只需要知道访问哪个服务的哪个地址即可
 * 所以我们约定我们把服务和地址保存在redis中.给一个key,用户下次带着这个key过来,我们动态的去获取这个key对应的服务和地址,这以后我们只需要在redis中不断添加或者修改数据即可
 *
 * @Author jackiechan
 */
@Component
public class RoutingFilter extends ZuulFilter {

    Logger logger = LoggerFactory.getLogger(RoutingFilter.class);


    @Autowired
    private CacheService cacheService;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        //context.put(FilterConstants.SERVICE_ID_KEY, "testservice2");
        // context.put(FilterConstants.REQUEST_URI_KEY, "/test2/test2/wangwu");
        //根据用户传递的参数,找到与之对应的服务名字和地址
        //我们要求用户必须通过method这个参数来传递要请求的服务
        HttpServletRequest request = context.getRequest();
        String method = request.getParameter("method");
        //修改,先从本地取,然后再去缓存
        Map<Object, Object> apiinfo = SystemParams.ALL_APIINFO.get(SystemParams.METHOD_REDIS_PRE + method);
        if (apiinfo == null) {
            //如果本地没有,则去查询一次redis,查询到后放入到本地
            //根据获取到用户传递的method的值,去redis中获取这个值对应的服务名字和地址
            apiinfo = cacheService.hGetAll(SystemParams.METHOD_REDIS_PRE + method);
            SystemParams.ALL_APIINFO.put(SystemParams.METHOD_REDIS_PRE + method, apiinfo);
        }
        if (apiinfo != null && apiinfo.size() >= 2) {
            Object serviceId = apiinfo.get("serviceId");
            Object url = apiinfo.get("url"); //例如 /test/test/{age}
            logger.error("替换前是:{}", url);
            //通过FilterConstants.SERVICE_ID_KEY和FilterConstants.REQUEST_URI_KEY把地址设置过去,进行跳转
            context.put(FilterConstants.SERVICE_ID_KEY, serviceId.toString());
            Enumeration<String> parameterNames = request.getParameterNames();//所有的参数名 比如 name  age  password

            while (parameterNames.hasMoreElements()) {
                String pName = parameterNames.nextElement();//获取到每一个参数名
                String parameter = request.getParameter(pName);//获取到没一个参数的具体的值
                url = url.toString().replaceAll("\\{" + pName + "\\}", parameter);//将地址中与参数名字对应的那段内容替换为用户传递的内容
                logger.error("替换后是:{}", url);
            }

            context.put(FilterConstants.REQUEST_URI_KEY, url.toString());
        }
        return null;
    }
}
