package com.qianfeng.openapi.web.event;

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
import com.qianfeng.openapi.web.mq.RabbitMQOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by jackiechan on 19-12-31 下午3:28
 *
 * @Author jackiechan
 */
@Component
public class RefreshRedisEvent {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private PublicParmasMapper mapper;
    @Autowired
    private RabbitMQOutput output;

    @EventListener//当前方法会接受发送EventType类型时间的通知
    public void onEvent(EventType eventType) {
        if (eventType == EventType.PUBLICPARMAS) {
            //收到了一个更新全局参数的通知
            //从数据库查询新的全局参数,更新到redis中
            Set<String> publicParams = mapper.getAllPublicParams();
            cacheService.deleteKey(SystemParams.PUBLIC_PARMAS);//删除原先的全局参数数据,因为可能会出现数据已经删掉,结果没有删除的睇情况
            cacheService.sadd(SystemParams.PUBLIC_PARMAS, publicParams.toArray(new String[]{}), -1);//将查询到最新的数据重新添加到redis
            //通知网关更新数据
            MessageChannel channel = output.sendMessage();//获取到发送消息的通道
            channel.send(new GenericMessage<String>("abc"));//发送消息,内容是abc,abc本身没有任何含义,但是我们因为是在更新参数中发送的,所以它可以作为更新全局参数的标记
        } else if (eventType == EventType.APIINFO) {
            //更新接口信息

        }
    }
}
