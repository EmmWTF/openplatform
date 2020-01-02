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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-31 上午10:52
 * 当前过滤器的作用是用于同步所有需要校验的公共参数,因为我们的公共参数基本上不变,没有必要每次查询redis,所以提前做一份jvm缓存(多级缓存)
 *
 * @Author jackiechan
 */
@WebListener
public class PublicParmasListener implements ServletContextListener {
    Logger logger = LoggerFactory.getLogger(PublicParmasListener.class);
    @Autowired
    private CacheService cacheService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            //查询缓存获取数据
            Set<String> public_parmas = cacheService.sMembers(SystemParams.PUBLIC_PARMAS);
            SystemParams.ALL_PARAMS.clear();//清空
            SystemParams.ALL_PARAMS.addAll(public_parmas);//重新保存最新数据
            logger.error("加载了全局的过滤参数{}",public_parmas);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
