package com.qianfeng.openapi.gateway.listener;

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


import com.qianfeng.openapi.gateway.constans.SystemParams;
import com.qianfeng.openapi.gateway.feign.CacheService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Map;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-31 上午11:07
 * 这个过滤器的作用是在程序启动的时候将所有aoi的信息加载到本地做jvm缓存,这样子后续的路由的时候就不需要每次都查询redis缓存了
 *
 * @Author jackiechan
 */
@WebListener
public class ApiinfoListener implements ServletContextListener {
    @Autowired
    private CacheService cacheService;


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //需要加载的是所有的接口信息
        //怎么知道所有接口 keys命令

        try {
            SystemParams.ALL_APIINFO.clear();
            Set<String> keys = cacheService.keys(SystemParams.METHOD_REDIS_PRE+"*");
            for (String key : keys) {
                Map<Object, Object> apiinfoMap = cacheService.hGetAll(key);//获取每个接口的apiinfo
                SystemParams.ALL_APIINFO.put(key, apiinfoMap);//保存每一个接口信息
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
