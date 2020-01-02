package com.qianfeng.openapi.gateway.feign;
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


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-27 下午4:46
 *
 * @Author jackiechan
 */
@FeignClient("OPENAPI-CACHE")
public interface CacheService {

    @RequestMapping("/cache/get/{key}")
    String getFromRedis(@PathVariable String key);

    @PostMapping("/cache/expire/{key}/{expireTime}")
    boolean expire(@PathVariable String key, @PathVariable long expireTime);

    @RequestMapping("/cache/smembers/{key}")
    Set<String> sMembers(@PathVariable  String key);


    @RequestMapping("/cache/hget/{key}/{field}")
    public String hGet(@PathVariable String key, @PathVariable String field);

    @RequestMapping("/cache/hgetall/{key}")
    public Map<Object, Object> hGetAll(@PathVariable String key);


    @PostMapping("/cache/set/{key}/{value}/{expireTime}")
    boolean save2redis(@PathVariable String key, @PathVariable String value, @PathVariable long expireTime);

    @RequestMapping("/cache/increment/{key}/{count}")
    Long getDecrementNum(@PathVariable String key,@PathVariable int count);

    @RequestMapping("/cache/keys/{partten}")
    Set<String> keys(@PathVariable String partten);
}
