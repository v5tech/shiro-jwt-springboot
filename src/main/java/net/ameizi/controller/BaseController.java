//package net.ameizi.controller;
//
//import com.google.common.collect.Maps;
//import net.ameizi.utils.HttpStatus;
//import org.apache.shiro.authz.AuthorizationException;
//import org.apache.shiro.authz.UnauthorizedException;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BaseController {
//
//    /** 设置成功响应 */
//    protected ResponseEntity<Object> success(Object data) {
//        return responseEntity(HttpStatus.OK,data,HttpStatus.OK.msg());
//    }
//
//    protected ResponseEntity<Object> success(String msg) {
//        return responseEntity(HttpStatus.OK,null,msg);
//    }
//
//    protected ResponseEntity<Object> success() {
//        return responseEntity(HttpStatus.OK,null,HttpStatus.OK.msg());
//    }
//
//    protected ResponseEntity<Object> success(Object data,String msg) {
//        return responseEntity(HttpStatus.OK,data,msg);
//    }
//
//    /** 设置失败响应 */
//    protected ResponseEntity<Object> error(Object data) {
//        return responseEntity(HttpStatus.BAD_REQUEST,data,HttpStatus.BAD_REQUEST.msg());
//    }
//
//    protected ResponseEntity<Object> error(String msg) {
//        return responseEntity(HttpStatus.BAD_REQUEST,null,msg);
//    }
//
//    protected ResponseEntity<Object> error() {
//        return responseEntity(HttpStatus.BAD_REQUEST,null,HttpStatus.BAD_REQUEST.msg());
//    }
//
//    protected ResponseEntity<Object> error(Object data,String msg) {
//        return responseEntity(HttpStatus.BAD_REQUEST,data,msg);
//    }
//
//    /** 设置响应代码 */
//    protected ResponseEntity<Object> responseEntity(HttpStatus code, Object data, String msg) {
//        Map<String,Object> map = new HashMap<>();
//        if (data != null) {
//            if (data instanceof PageInfo) {
//                PageInfo<?> page = (PageInfo<?>) data;
//                map.put("data", page.getList());
//                map.put("current", page.getPageNum());
//                map.put("size", page.getSize());
//                map.put("pages", page.getPages());
//                map.put("total", page.getTotal());
//            } else if (data instanceof List<?>) {
//                map.put("data", data);
//            } else {
//                map.put("data", data);
//            }
//        }
//        map.put("code", code.value());
//        map.put("msg", msg);
//        map.put("timestamp", System.currentTimeMillis());
//        return ResponseEntity.ok()
//                .header("Access-Control-Allow-Origin","*")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(map);
//    }
//
//    /** 异常处理 */
//    @ExceptionHandler(Exception.class)
//    public void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex)
//            throws Exception {
//        Map<String,Object> map = Maps.newHashMap();
//        int status;
//        // 方法级别shiro权限校验失败时异常信息处理
//        if (ex instanceof AuthorizationException){
//            status = HttpStatus.FORBIDDEN.value();
//            map.put("code", HttpStatus.FORBIDDEN.value());
//            map.put("msg", HttpStatus.FORBIDDEN.msg());
//        } else if (ex instanceof UnauthorizedException) {
//            status = HttpStatus.FORBIDDEN.value();
//            map.put("code", HttpStatus.FORBIDDEN.value());
//            map.put("msg", HttpStatus.FORBIDDEN.msg());
//        } else if(ex instanceof LoginException){
//            status = HttpStatus.FORBIDDEN.value();
//            map.put("code", HttpStatus.LOGIN_FAIL.value());
//            map.put("msg", ex.getMessage());
//        } else if(ex instanceof UploadException){
//            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            map.put("code", HttpStatus.JP_UPLOAD_FAIL.value());
//            map.put("msg", ex.getMessage());
//        } else if(ex instanceof TokenParserException){
//            status = HttpStatus.FORBIDDEN.value();
//            map.put("code", HttpStatus.TOKEN_PARSER_FAIL.value());
//            map.put("msg",ex.getMessage());
//        } else {
//            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
//            map.put("msg", HttpStatus.INTERNAL_SERVER_ERROR.msg());
//        }
//        ex.printStackTrace();
//        response.setContentType("application/json;charset=UTF-8");
//        response.setHeader("Access-Control-Allow-Origin","*");
//        response.setStatus(status);
//        map.put("timestamp", System.currentTimeMillis());
//        response.getOutputStream().write(JSONUtils.toJSONString(map).getBytes());
//    }
//
//}
