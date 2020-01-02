package com.qianfeng.openapi.authcenter.filter;

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
import com.qianfeng.openapi.authcenter.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by jackiechan on 20-1-2 上午9:43
 *
 * @Author jackiechan
 */
public class JWTUsernameAndPasswordAuthenticationFilter  extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper;

    private JwtConfig jwtConfig;

    public JWTUsernameAndPasswordAuthenticationFilter(AuthenticationManager manager, ObjectMapper objectMapper, JwtConfig jwtConfig) {

        super(new AntPathRequestMatcher(jwtConfig.getLoginUrl(),"POST"));//设置登录的请求地址和请求方式
        this.objectMapper = objectMapper;
        this.jwtConfig = jwtConfig;
        setAuthenticationManager(manager);
    }

    /**
     * 获取用户输入的用户名和密码的
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
      //我们要求用户必须传递json字符串过来,key必须是username和password

        //request.getParameter("username");
       // request.getParameterMap()  key=values&asdasd=dasdas&dadas=dasd

      //  String json = null;

        User user = objectMapper.readValue(request.getInputStream(), User.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        return getAuthenticationManager().authenticate(token);
    }

    /**
     * 对我们输入的账号和密码认证成功之后会执行这个方法
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
       //认证成功后之后需要生成一个token给用户返回,用户以后只要带着token来就行了
//        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
//        List<String> roles = new ArrayList<>();
//        for (GrantedAuthority authority : authorities) {

//            String role = authority.getAuthority();
//            roles.add(role);
//        }
//        //lambda表达式  js闭包  xxx->asdasd
        Instant now = Instant.now();

        String jwt = Jwts.builder()//
                .setSubject(authResult.getName())//设置当前用户是谁,通过认证结果获取
              //  .claim("suibianxie",roles)//把当前用户的角色保存起来
                .claim("suibianxie",authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))//把当前用户的角色保存起来
                .setIssuedAt(Date.from(now))//设置有效期的开始时间
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpireTime())))//设置过期时间
                .signWith(SignatureAlgorithm.HS256,jwtConfig.getSecret().getBytes())//设置签名数据
                .compact();
        //将生成的token给用户返回,我们有很多方式,所以我们可以选择其中任意一个,我们怎么返回,客户端就得怎么获取
        response.addHeader(jwtConfig.getJwtHeadername(),jwt);
    }

    static  class User{
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
