package com.qianfeng.openapi.gateway.constans;
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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by jackiechan on 19-12-28 上午11:09
 *
 * @Author jackiechan
 */
public interface SystemParams {
    String PUBLIC_PARMAS = "public_params";//公共参数在redis中的参数名
    String PARAM_NULL ="1-00001";//请求参数为空了,我们约定所有和参数相关的错误吗都是1开头的
    String TIME_STAMP_INVALIDATE = "1-00002";//时间戳参数不符合要求
    String SIGN_INVALIDATE = "1-00003";//签名校验失败
    String SIGN_ALLREADY_USED = "1-00004";//签名重复了,已经被使用了,为了防止非幂等操作
    String METHOD_REDIS_PRE = "apiinfo:";//这个就是我们的method和服务之间的映射关系在redis中key的前缀
    String LIMIT_ERROR = "2-00001";//代表请求次数已经不足
    Set<String> ALL_PARAMS = new HashSet<>();

    Map<String, Map<Object, Object>> ALL_APIINFO = new HashMap<>();//保存有所有接口信息的map.key就是method名字加上前缀
}
