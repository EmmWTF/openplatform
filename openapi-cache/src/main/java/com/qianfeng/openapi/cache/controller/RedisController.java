package com.qianfeng.openapi.cache.controller;

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


import com.qianfeng.openapi.cache.service.CacheServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-27 下午3:48
 *
 * @Author jackiechan
 */
@RestController
@RequestMapping("/cache")
public class RedisController {
    @Autowired
    private CacheServiceApi cacheServiceApi;

    @RequestMapping("/get/{key}")
    public String getFromRedis(@PathVariable("key") String key) {
        return cacheServiceApi.getFromRedis(key);
    }

    @PostMapping("/set/{key}/{value}/{expireTime}")
    boolean save2redis(@PathVariable String key, @PathVariable String value, @PathVariable long expireTime) {
        //此处应该判断key是不是空
        return cacheServiceApi.save2redis(key, value, expireTime);
    }

    @PostMapping("/delete/{key}")
    boolean deleteKey(@PathVariable String key) {
        //此处应该判断key是不是空
        return cacheServiceApi.deleteKey(key);
    }

    @PostMapping("/expire/{key}/{expireTime}")
    boolean expire(@PathVariable String key, @PathVariable long expireTime) {
        //此处应该判断key是不是空
        return cacheServiceApi.expire(key, expireTime);
    }

    @RequestMapping("/increment/{key}/{count}")
    Long getAutoIncrementId(@PathVariable String key,@PathVariable int count) {
        //此处应该判断key是不是空
        return cacheServiceApi.getAutoIncrementId(key,count);
    }


    @RequestMapping("/smembers/{key}")
    public Set<String> sMembers(@PathVariable String key) {
        //此处需要判断key是不是空的
        return cacheServiceApi.sMembers(key);
    }

    @PostMapping("/sadd/{key}/{value}/{expireTime}")
    Long sadd(@PathVariable String key,@PathVariable String value,@PathVariable long expireTime){
        return cacheServiceApi.sadd(key, value, expireTime);
    }

    @PostMapping("/sadd")
    Long sadd(String key, String[]values,long expireTime){

        return cacheServiceApi.sadd(key, values, expireTime);

    }


    @RequestMapping("/hget/{key}/{field}")
    public String hGet(@PathVariable String key, @PathVariable String field) {
        return cacheServiceApi.hGet(key, field);
    }

    @RequestMapping("/hgetall/{key}")
    public Map<Object, Object> hGetAll(@PathVariable String key) {

        return cacheServiceApi.hGetAll(key);
    }

    @RequestMapping("/keys/{partten}")
    public Set<String> keys(@PathVariable String partten) {
        return  cacheServiceApi.keys(partten);
    }
}
