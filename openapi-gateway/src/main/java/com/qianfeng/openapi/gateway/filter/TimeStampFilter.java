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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jackiechan on 19-12-30 上午9:10
 * 此过滤器的作用是判断用户当前的请求发起时间和服务器收到的时间差是否在允许的范围内
 * 因为我们可能会遇到网络故障,或者是被人拦截请求后修改再次发送,为了减少这种情况,我们需要对用户发起的请求时间进行判断
 * @Author jackiechan
 */
@Component
public class TimeStampFilter  extends ZuulFilter {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
       // return currentContext.sendZuulResponse();//根据前面是否拦截来决定是否启用
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //判断用户传递的时间和服务器时间的时间差
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String timestamp = request.getParameter("timestamp");
        try {
            Date  date = simpleDateFormat.parse(timestamp);
            long time = date.getTime();//用户传递的时间
            long currentTimeMillis = System.currentTimeMillis();//系统时间
            if (currentTimeMillis - time < 0 || currentTimeMillis - time > 60000) {
                context.setSendZuulResponse(false);//拦截请求
                BaseJsonBean baseJsonBean = new BaseJsonBean();
                baseJsonBean.setCode(SystemParams.TIME_STAMP_INVALIDATE);
                baseJsonBean.setMsg("时间戳不符合要求");
                context.getResponse().setContentType("application/json;charset=utf-8");
                context.setResponseBody(objectMapper.writeValueAsString(baseJsonBean));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            //用户传递的时间参数不符合要求
            context.setSendZuulResponse(false);//拦截请求
            BaseJsonBean baseJsonBean = new BaseJsonBean();
            baseJsonBean.setCode(SystemParams.TIME_STAMP_INVALIDATE);
            baseJsonBean.setMsg("时间戳不符合要求");
            context.getResponse().setContentType("application/json;charset=utf-8");
            try {
                context.setResponseBody(objectMapper.writeValueAsString(baseJsonBean));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return null;
    }
}
