package com.branch.branchapiinterface.controller;

import com.branch.branchapiclientsdk.model.User;
import com.branch.branchapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 名称API
 * @author Ee1ex
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name, HttpServletRequest request){
        return "GET 你的名字是"+name;
    }
    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return "POST 你的名字是"+name;
    }
    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");

        //todo 实际情况：从数据库中查询
        if (!accessKey.equals("yupi")){
            throw new RuntimeException("accessKey 不正确");
        }
        if (Long.parseLong(nonce) > 10000){
            throw new RuntimeException("nonce 不正确");
        }
        //时间和当前时间不能超过五分钟
        //假定 timestamp 是一个 long 类型的值，代表秒数
        long timestamplong;
        try {
            timestamplong = Long.parseLong(timestamp);
            if (timestamplong - (System.currentTimeMillis() / 1000) > 60 * 5) {
                throw new RuntimeException("timestamp 不正确");
            }
        } catch (NumberFormatException e) {
            // 处理 timestampStr 无法解析为 long 的情况
            System.err.println("无效的时间戳格式");
        }

        //todo 实际情况：从数据库中查询
        String serverSign = SignUtils.getSign(body, "abcdefgh");
        if (!sign.equals(serverSign)){
            throw new RuntimeException("sign 不正确");
        }
        return "POST 你的名字是" + user.getUsername();
    }

    public void throwAccessKeyException() {
        throw new RuntimeException("accessKey 不正确");
    }

    public void throwNonceException() {
        throw new RuntimeException("nonce 不正确");
    }

    public void throwSignException() {
        throw new RuntimeException("sign 不正确");
    }
}
