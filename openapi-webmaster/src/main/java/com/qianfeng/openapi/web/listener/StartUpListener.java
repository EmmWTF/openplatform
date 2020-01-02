package com.qianfeng.openapi.web.listener;

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


import com.qianfeng.openapi.web.constans.SystemParams;
import com.qianfeng.openapi.web.feign.CacheService;
import com.qianfeng.openapi.web.mapper.PublicParmasMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-28 下午2:44
 * 这是一个监听器,用于监听我们程序的启动,在程序启动的时候同步数据到redis
 *
 * @Author jackiechan
 */
@WebListener//声明当前类是一个web的监听器
public class StartUpListener implements ServletContextListener {
    @Autowired
    private PublicParmasMapper publicParmasMapper;
    @Autowired
    private CacheService cacheService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //程序启动了
        //调用mapper中的数据,然后同步到redis
        Set<String> params = publicParmasMapper.getAllPublicParams();
        cacheService.sadd(SystemParams.PUBLIC_PARMAS, params.toArray(new String[]{}), -1);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //程序销毁F
    }
}
