package com.qianfeng.openapi.gateway;

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


import com.qianfeng.openapi.gateway.mq.SubMessageInput;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * Created by jackiechan on 19-12-28 上午9:51
 *
 * @Author jackiechan
 */
@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients//开启feign
@ServletComponentScan("com.qianfeng.openapi.gateway.listener")//扫描监听器
@EnableBinding(SubMessageInput.class)
public class GateWayStartApp {

    public static void main (String[] args){
        SpringApplication.run(GateWayStartApp.class,args);
        //获取垃圾回收器的名字
//        List<GarbageCollectorMXBean> list = ManagementFactory.getGarbageCollectorMXBeans();
//        for (GarbageCollectorMXBean bean : list) {
//            String name = bean.getName();
//            System.err.println(name);
//        }
    }
}
