package net.ameizi.config;

import net.ameizi.shiro.StatelessDefaultSubjectFactory;
import net.ameizi.shiro.filter.JcaptchaValidateFilter;
import net.ameizi.shiro.filter.JwtAuthenticationFilter;
import net.ameizi.shiro.jwt.JwtRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    /**
     * 自定义Realm
     * @return
     */
    @Bean(name = "jwtRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public JwtRealm jwtRealm() {
        JwtRealm jwtRealm = new JwtRealm();
        // jwtRealm.setCredentialsMatcher(credentialsMatcher());
        jwtRealm.setCachingEnabled(false);
        return jwtRealm;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    @Bean("shiroFilter")
    @DependsOn("securityManager")
    public ShiroFilterFactoryBean shiroFilter(DefaultSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilter  = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 拦截器
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();

        // 允许用户匿名访问/login(登录接口)
        filterChainDefinitionMap.put("/login", "anon");
        // 验证码允许匿名访问
        filterChainDefinitionMap.put("/captcha*","anon");

        filterChainDefinitionMap.put("/**", "jwt");

        // filterChainDefinitionMap.put("/**","anon");

        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt",new JwtAuthenticationFilter());
        filters.put("jcaptchaValidate",new JcaptchaValidateFilter());

        shiroFilter.setFilters(filters);

        return shiroFilter;
    }

    /**
     * Subject工厂管理器
     * @return
     */
    @Bean
    public DefaultWebSubjectFactory subjectFactory(){
        DefaultWebSubjectFactory subjectFactory = new StatelessDefaultSubjectFactory();
        return subjectFactory;
    }

    /**
     * 安全管理器
     * @return
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();

        securityManager.setRealm(jwtRealm());

        // 替换默认的DefaultSubjectFactory，用于关闭session功能
        securityManager.setSubjectFactory(subjectFactory());
        securityManager.setSessionManager(sessionManager());

        // 关闭session存储，禁用Session作为存储策略的实现，但它没有完全地禁用Session所以需要配合SubjectFactory中的context.setSessionCreationEnabled(false)
        ((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);

        // 用户授权/认证信息Cache, 后期可采用EhCache缓存
        // securityManager.setCacheManager(cacheManager());

        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * 会话管理器
     * @return
     */
    public DefaultSessionManager sessionManager(){
        DefaultSessionManager sessionManager =new DefaultSessionManager();
        // 关闭session定时检查，通过setSessionValidationSchedulerEnabled禁用掉会话调度器
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return  sessionManager;
    }

    /**
     * 用户授权信息缓存
     * @return
     */
    @Bean
    public CacheManager cacheManager() {
        // EhCacheManager cacheManager = new EhCacheManager();
        // cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return new MemoryConstrainedCacheManager();
    }

    /**
     * 凭证匹配器
     * @return
     */
    @Bean
    public CredentialsMatcher credentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher =new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(1024);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /**
     * Shiro生命周期处理器
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro注解(如@RequiresRoles,@RequiresPermissions)
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }
}
