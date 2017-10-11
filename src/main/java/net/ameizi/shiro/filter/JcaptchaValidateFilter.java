package net.ameizi.shiro.filter;

import net.ameizi.utils.ApplicationContextUtil;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class JcaptchaValidateFilter extends AccessControlFilter {

    private boolean jcaptchaEbabled = true;     //是否开启验证码支持

    private String jcaptchaParam = "jcaptchaCode";  //前台提交的验证码参数名

    public static String failureKeyAttribute = "jcaptcha.message";   //验证失败后存储到的属性名

    private String redisCaptchaKey = "vcodeKey";

    public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
        this.jcaptchaEbabled = jcaptchaEbabled;
    }

    public void setJcaptchaParam(String jcaptchaParam) {
        this.jcaptchaParam = jcaptchaParam;
    }

    public void setFailureKeyAttribute(String failureKeyAttribute) {
        JcaptchaValidateFilter.failureKeyAttribute = failureKeyAttribute;
    }

    public void setRedisCaptchaKey(String redisCaptchaKey) {
        this.redisCaptchaKey = redisCaptchaKey;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse servletResponse, Object o) throws Exception {
        //1、设置验证码是否开启属性，页面可以根据该属性来决定是否显示验证码
        request.setAttribute("jcaptchaEbabled",jcaptchaEbabled);
        HttpServletRequest httpRequest = WebUtils.toHttp(request);

        //2、判断验证码是否禁用或不是表单提交（允许访问）
        if(!jcaptchaEbabled || !"post".equalsIgnoreCase(httpRequest.getMethod())){
            return true;
        }
        String captchaKey = httpRequest.getParameter(redisCaptchaKey);
        if(StringUtils.isEmpty(captchaKey)){
            request.setAttribute(failureKeyAttribute,"验证码Key为空！");
            return false;
        }

        RedisTemplate<String,Object> redisTemplate = (RedisTemplate) ApplicationContextUtil.getBeanObj("redisTemplate");
        String captchaValue = (String) redisTemplate.boundValueOps(captchaKey).get();

        if(StringUtils.isEmpty(captchaValue)){
            request.setAttribute(failureKeyAttribute,"验证码已过期！");
            return false;
        }

        if((httpRequest.getParameter(jcaptchaParam)).equalsIgnoreCase(captchaValue)){
            return true;
        }

        request.setAttribute(failureKeyAttribute,"验证码输入错误！");

        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse servletResponse) throws Exception {
        return true;
    }
}
