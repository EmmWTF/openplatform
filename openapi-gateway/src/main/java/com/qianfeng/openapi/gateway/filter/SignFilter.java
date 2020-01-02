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
import com.qianfeng.openapi.gateway.utils.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jackiechan on 19-12-30 上午9:53
 * 此过滤器的作用是对用户请求中的参数进行合法性校验的,防止中途有人篡改数据
 * 客户端和服务的使用相同的算法来对数据进行签名,客户端生成签名数据并发送过来,服务的对客户端数据按照相同的算法生成一次然后进行校验
 *
 * @Author jackiechan
 */
@Component
public class SignFilter extends ZuulFilter {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(SignFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 30;
    }

    @Override
    public boolean shouldFilter() {
     //   return RequestContext.getCurrentContext().sendZuulResponse();
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取用户所有的请求参数
        RequestContext context = RequestContext.getCurrentContext();
        // Map<String, List<String>> params = context.getRequestQueryParams();
        //拿到除了签名之外的数据,我们约定了签名参数名叫sign
        HttpServletRequest request = context.getRequest();//获取到所有的请求参数名
        Enumeration<String> names = request.getParameterNames();
        Map<String, String> pmaps = new TreeMap<>();//保存除了签名之外的数据
        //按照我们相同的规则对这些数据进行MD5运算(需要用户当前请求发起用户的一个秘钥)

        while (names.hasMoreElements()) {
            String name = names.nextElement();//获取到一个参数

            if ("sign".equals(name)) {
                continue;
            }
            String parameter = request.getParameter(name);//获取当前的参数对应的值
            pmaps.put(name, parameter);
        }
        //获取到用户的秘钥,算出我们认为的签名
        //因为一个用户可能有多个应用,所以到底是每个用户一个秘钥还是一个应用一个秘钥
        //经过我们分析 觉得一个应用一个秘钥会更合理一些,所以我们在此处必须知道当前应用是谁
        //要求用户必须在参数中传递当前应用是谁,比如我们要求传递是app_key这个参数
        //我们约定app相关的信息在redis中保存着,也就是我们通过app_key去获取一份秘钥,从redis中获取
        //我们分析出来一个应用可能会有好多信息,所以使用hash存放在redis中,注意app_key的值理论不能直接作为reids的key,而是要拼接
        // app_key:info
        String app_key = request.getParameter("app_key");//获取当前app_key 值
        String secret = cacheService.hGet(app_key + ":info", "secret");//获取当前应用的秘钥,也就是盐值


        String md5Signature = Md5Util.md5Signature(pmaps,secret);
        logger.error("计算出的签名{}", md5Signature);
        //将我们运算后的数据和用户传递过来的签名数据进行比较
        String sign = request.getParameter("sign");//用户传递的签名
        //此处保存用户传递的签名到redis中,用于下次过滤非幂等操作的,有效期此处写了1分钟,实际上取决于我们的时间戳的有效期
        cacheService.save2redis(sign, "dasdasd", 60000);
        //如果一致代表数据没有被篡改,放行
        if (!sign.equalsIgnoreCase(md5Signature)) {
            //否则拦截
            context.setSendZuulResponse(false);
            BaseJsonBean baseJsonBean = new BaseJsonBean();
            baseJsonBean.setCode(SystemParams.SIGN_INVALIDATE);
            baseJsonBean.setMsg("签名校验失败");
            context.getResponse().setContentType("application/json;charset=utf-8");
            try {
                context.setResponseBody(objectMapper.writeValueAsString(baseJsonBean));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }


        return null;
    }
}
