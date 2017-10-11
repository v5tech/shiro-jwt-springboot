package net.ameizi.shiro.jwt;

import com.google.common.collect.Sets;
import net.ameizi.domain.SysUser;
import net.ameizi.utils.TokenUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;

public class JwtRealm extends AuthorizingRealm {

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 根据用户名查找角色，请根据需求实现
        String username = (String)principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 根据username查询角色
        authorizationInfo.setRoles(Sets.newHashSet("admin","superadmin"));

        // 根据username查询权限
        authorizationInfo.setStringPermissions(Sets.newHashSet("system:*"));

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;

        // 获取token
        String token = jwtToken.getToken();

        // 从token中获取用户名
        String username = tokenUtil.getUsernameFromToken(token);

        // 根据用户名查询数据库

        SysUser user = new SysUser();
        user.setUserName(username);
        user.setStatus(1);
        user.setPassword("000000");

        // 用户不存在
        if (user == null) {
            throw new UnknownAccountException();
        }

        // 用户被禁用
        if(user.getStatus()==0){
            throw new LockedAccountException();
        }

        try {
            return new SimpleAuthenticationInfo(
                    username,
                    token,
                    getName()
            );
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
