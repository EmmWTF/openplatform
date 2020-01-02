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
import com.qianfeng.openapi.gateway.mq.SubMessageInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-31 下午3:52
 * 消费者按照理论来说必须在程序启动的时候就绑定到交换机上,以防止在启动后到绑定前期间有消息过来无法接受导致消息丢失
 * @Author jackiechan
 */
@WebListener
public class MQListener  implements ServletContextListener {
    @Autowired
    private SubMessageInput subMessageInput;
    @Autowired
    private CacheService cacheService;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //程序启动,我们立刻绑定到mq上
        SubscribableChannel channel = subMessageInput.getMessgae();
        channel.subscribe(new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
               byte[] bs = (byte[]) message.getPayload();//发送过来的数据是一个字节数组
                if ("ABC".equalsIgnoreCase(new String(bs))){//转成字符串
                    //我们收到了一个更新全局参数的消息
                   // 更新全局数据
                    Set<String> members = cacheService.sMembers(SystemParams.PUBLIC_PARMAS);//获取到最新的参数列表
                    SystemParams.ALL_PARAMS.clear();
                    SystemParams.ALL_PARAMS.addAll(members);//重新保存最新的数据
                }
            }
        });

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
