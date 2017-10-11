package net.ameizi.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.common.collect.ImmutableMap;
import net.ameizi.shiro.jwt.JwtToken;
import net.ameizi.utils.TokenUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mobile.device.Device;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class LoginController {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private DefaultKaptcha captchaProducer;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping(value="/captcha")
    public Map<String,String> captcha(){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String capText = captchaProducer.createText();
            String uuid = UUID.randomUUID().toString();
            redisTemplate.boundValueOps(uuid).set(capText,60, TimeUnit.SECONDS);
            BufferedImage bi = captchaProducer.createImage(capText);
            ImageIO.write(bi, "png", baos);
            String imgBase64 = Base64.encodeBase64String(baos.toByteArray());
            return ImmutableMap.of(uuid,"data:image/jpeg;base64,"+imgBase64);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @PostMapping("/login")
    public Object login(HttpServletRequest request, HttpServletResponse response, Device device) throws IOException {
        JSONObject jsonObject = new JSONObject();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 验证用户名密码成功后生成token
        String token = tokenUtil.generateToken(username, device);
        // 构建JwtToken
        JwtToken jwtToken = JwtToken.builder().token(token).principal(username).build();

        Subject subject = SecurityUtils.getSubject();
        try {
            // 该方法会调用JwtRealm中的doGetAuthenticationInfo方法
            subject.login(jwtToken);
        } catch (UnknownAccountException exception) {
            exception.printStackTrace();
            System.out.println("账号不存在");
        } catch (IncorrectCredentialsException exception) {
            exception.printStackTrace();
            System.out.println("错误的凭证，用户名或密码不正确");
        } catch (LockedAccountException exception) {
            exception.printStackTrace();
            System.out.println("账户已锁定");
        } catch (ExcessiveAttemptsException exception) {
            exception.printStackTrace();
            System.out.println("错误次数过多");
        } catch (AuthenticationException exception) {
            exception.printStackTrace();
            System.out.println("认证失败");
        }

        // 认证通过
        if(subject.isAuthenticated()){

            // 将token写出到cookie
            Cookie cookie =new Cookie("token",token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(3600 * 5);
            cookie.setPath("/");
            response.addCookie(cookie);
            response.flushBuffer();

            jsonObject.put("code",200);
            jsonObject.put("msg","认证成功!");
            jsonObject.put("token",token);
            jsonObject.put("timestamp", Calendar.getInstance().getTimeInMillis());
            return jsonObject;
        }else{
            jsonObject.put("code",403);
            jsonObject.put("msg","认证失败");
            jsonObject.put("token",null);
            jsonObject.put("timestamp", Calendar.getInstance().getTimeInMillis());
            return jsonObject;
        }
    }


    /**
     * 检查是否登录
     * @param token
     * @return
     */
    @GetMapping(value = "/checkLogin")
    public Object checkLogin(@CookieValue("token") String token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",200);
        if(StringUtils.isEmpty(token)){
            jsonObject.put("msg","令牌为空");
        }

        // 根据token获取用户信息

        // 检查token合法性及用户身份

        return jsonObject;
    }


    /**
     * 登出
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/logout")
    public Object logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                .filter(ck -> "token".equals(ck.getName()))
                .limit(1)
                .map(ck -> {
                    ck.setMaxAge(0);
                    ck.setHttpOnly(true);
                    ck.setPath("/");
                    return ck;
                })
                .findFirst();
        response.addCookie(cookie.get());
        response.flushBuffer();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",200);
        jsonObject.put("msg","success");
        return jsonObject;
    }


    /**
     * 更新token
     * @param token
     * @return
     */
    @RequestMapping("/token/refresh")
    public Object refreshToken(@CookieValue(value = "token") String token) {
        JSONObject jsonObject = new JSONObject();
        String newToken = tokenUtil.refreshToken(token);
        jsonObject.put("code",200);
        jsonObject.put("msg","success");
        jsonObject.put("token",newToken);
        jsonObject.put("timestamp", Calendar.getInstance().getTimeInMillis());
        return jsonObject;
    }
}
