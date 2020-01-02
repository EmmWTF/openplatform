package com.qianfeng.openapi.cache.service.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by jackiechan on 19-12-27 下午3:50
 *
 * @Author jackiechan
 */
@Service
public class CacheService implements CacheServiceApi {
    Logger logger = LoggerFactory.getLogger(CacheService.class);
    @Autowired
    private StringRedisTemplate template;

    @Override
    public String getFromRedis(String key) {
        logger.error("从redis中获取字符串数据,key为{}", key);
        return template.opsForValue().get(key);
    }

    @Override
    public boolean save2redis(String key, String value, long expireTime) {
        logger.error("save2redis执行了,key为{},value为{},过期时间为{}", key, value, expireTime);

        try {
            template.opsForValue().set(key, value);

            //我们做一个约定.如果传递的是-1 代表永久有效
            if (expireTime != -1) {
                //代表需要设置有效期
                expireTime = Math.abs(expireTime);
                template.expire(key, expireTime, TimeUnit.MILLISECONDS);
            }else{
                //持久化
                template.persist(key);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("save2redis执行失败,key为{},value为{},过期时间为{}", key, value, expireTime);
        }
        return false;
    }

    @Override
    public boolean deleteKey(String key) {

        try {
            template.delete(key);
            logger.error("deleteKey执行了,key为{}", key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("deleteKey执行失败,key为{}", key);
        }
        return false;
    }

    @Override
    public boolean expire(String key, long expireTime) {

        try {
            if (expireTime != -1) {
                //代表需要设置有效期
                expireTime = Math.abs(expireTime);
                template.expire(key, expireTime, TimeUnit.MILLISECONDS);
            }else{
                //持久化
                template.persist(key);
            }
            logger.error("expire执行了,key为{},,过期时间为{}", key, expireTime);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("expire执行失败了,key为{},过期时间为{}", key, expireTime);
        }

        return false;
    }



    @Override
    public Long getAutoIncrementId(String key,int count) {
        //key如果不存在 会返回1
        //如果key对应的数据不是数字或者不是string格式的数据  则会抛出异常

        try {
            Long increment = template.opsForValue().increment(key, count);
            logger.error("获取自增数据执行了,key为{},count为{}", key,count);
            return increment;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取自增数据失败,key为{},count为{}", key,count);
        }
        return null;
    }

    @Override
    public Set<String> sMembers(String key) {

        try {
            Set<String> members = template.opsForSet().members(key);
            logger.error("获取set集合数据执行了,key为{},", key);
            return members;
        } catch (Exception e) {
            logger.error("获取set集合数据失败,key为{},", key);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sadd(String key, String value, long expireTime) {

        try {
            Long result = template.opsForSet().add(key, value);
            if (expireTime != -1) {
                //代表需要设置有效期
                expireTime = Math.abs(expireTime);
                template.expire(key, expireTime, TimeUnit.MILLISECONDS);
            }else{
                //持久化
                template.persist(key);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sadd(String key, String[] values, long expireTime) {

        try {

            Long result = template.opsForSet().add(key, values);
            if (expireTime != -1) {
                //代表需要设置有效期
                expireTime = Math.abs(expireTime);
                template.expire(key, expireTime, TimeUnit.MILLISECONDS);
            }else{
                //持久化
                template.persist(key);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String hGet(String key, String field) {
        Object o = template.opsForHash().get(key, field);
        return o==null?null:o.toString();
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        Map<Object, Object> map = template.opsForHash().entries(key);
        return map;
    }

    @Override
    public Set<String> keys(String partten) {
        return  template.keys(partten);
    }

}
