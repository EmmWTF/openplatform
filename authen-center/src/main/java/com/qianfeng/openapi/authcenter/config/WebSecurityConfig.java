package com.qianfeng.openapi.authcenter.config;

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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.qianfeng.openapi.authcenter.filter.JWTUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by jackiechan on 20-1-2 上午9:32
 *
 * @Author jackiechan
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取数据库中的用户名和密码
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())//设置密码的编码格式
        .withUser("admin").password(new BCryptPasswordEncoder().encode("admin"))//设置一个用户名和密码
        .roles("ADMIN","USER")//设置角色,当用户登录成功后就知道用户有什么角色了
        .and().withUser("user").password(new BCryptPasswordEncoder().encode("user")).roles("USER");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//禁用csrf防攻击
                .formLogin().disable()//项目本身会有一个登录框,要求我们输入账号和密码,我不想用它,禁用掉
                .logout().disable()//禁用掉原始的退出方式
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//
                .and().anonymous().and().exceptionHandling()//当用户输入的用户名和密码是错误的时候
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setContentType("text/html;charset=utf-8");
                    res.getWriter().write("刘金虎你的账号和密码是什么心里没点索引吗");
                }).and().addFilterAfter(new JWTUsernameAndPasswordAuthenticationFilter(authenticationManager(), objectMapper, jwtConfig), UsernamePasswordAuthenticationFilter.class)//
                .authorizeRequests().mvcMatchers(jwtConfig.getLoginUrl()).permitAll()//除了登录地址意外的地址都需要验证
                .anyRequest().authenticated();

    }
}
