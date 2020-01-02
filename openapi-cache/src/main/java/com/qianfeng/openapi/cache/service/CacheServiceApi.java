package com.qianfeng.openapi.cache.service;
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


import java.util.Map;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-27 下午3:49
 *
 * @Author jackiechan
 */
public interface CacheServiceApi {

    String getFromRedis(String key); //从redis获取数据

    boolean save2redis(String key, String value, long expireTime);//保存到redis


    boolean deleteKey(String key);//删除key

    boolean expire(String key,long expireTime);//设置有效期

    Long getAutoIncrementId(String key,int count);//自增
    //从redis中获取指定key对应的set集合
    Set<String> sMembers(String key);

    Long sadd(String key,String value,long expireTime);

    Long sadd(String key, String[] values,long expireTime);


    String hGet(String key, String field);

    Map<Object, Object> hGetAll(String key);

    Set<String> keys(String partten);

}
