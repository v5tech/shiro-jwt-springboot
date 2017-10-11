package net.ameizi.utils;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SubjectUtil {

    @Autowired
    private TokenUtil tokenUtil;

    public Integer getSubjectId() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("该用户未登录");
        }
        return 1;
    }


    /**
     * 直接从token获取用户id，不抛异常。
     * @param token token
     * @return 用户id
     */
//    public Long getSubjectIdFromToken(String token) {
//        User user = this.getSubjectUserFromToken(token);
//        return user.getId();
//    }


    /**
     * 直接从token获取用户id，不抛异常。
     * @param token token
     * @return 用户id
     */
//    public User getSubjectUserFromToken(String token) {
//        if(StringUtils.isEmpty(token)){
//            throw new TokenParserException("令牌为空！");
//        }
//
//        String username = tokenUtil.getUsernameFromToken(token);
//
//        if (StringUtils.isEmpty(username)) {
//            throw new TokenParserException("从令牌中解析用户名为空！");
//        }
//        User user = this.userService.selectOne(User.builder().username(username).build());
//        if (user == null) {
//            throw new LoginException("用户不存在！");
//        }
//        return user;
//    }

}
