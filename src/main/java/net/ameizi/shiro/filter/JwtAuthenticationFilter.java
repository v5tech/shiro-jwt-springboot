package net.ameizi.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ameizi.shiro.jwt.JwtToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends AuthenticatingFilter {

    private static final String TOKEN = "token";

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 先从Header里面获取
        String token = httpRequest.getHeader(TOKEN);
        if(StringUtils.isEmpty(token)){
            // 获取不到再从Parameter中拿
            token = httpRequest.getParameter(TOKEN);
            // 还是获取不到再从Cookie中拿
            if(StringUtils.isEmpty(token)){
                token = Arrays.stream(httpRequest.getCookies())
                        .filter(ck -> "token".equals(ck.getName()))
                        .limit(1)
                        .findFirst()
                        .get()
                        .getValue();
            }
        }
        return JwtToken.builder()
                .token(token)
                .build();
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        return true;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        jsonObject.put("msg","登录失败，无权访问");
        jsonObject.put("timestamp", System.currentTimeMillis());
        try {
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json;charset=UTF-8");
            servletResponse.setHeader("Access-Control-Allow-Origin","*");
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(jsonObject));
        } catch (IOException e) {
        }
        return false;
    }
}
