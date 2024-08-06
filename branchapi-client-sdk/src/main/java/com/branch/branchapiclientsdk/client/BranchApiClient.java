package com.branch.branchapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.branch.branchapiclientsdk.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.branch.branchapiclientsdk.utils.SignUtils.getSign;

/**
 * 调用第三方接口请求的客户端
 * @author Eelex
 */
public class BranchApiClient {
    private String accessKey;
    private String secretKey;
    public BranchApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        System.out.println("accessKey:" + this.accessKey);
        System.out.println("secretKey:" + this.secretKey);
    }
        public String getNameByGet(String name){
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("name", name);
            String result = HttpUtil.get("http://localhost:8123/api/name/", paramMap);
            System.out.println(result);
            return result;
        }


        public String getNameByPost(String name){
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("name", name);
            String result = HttpUtil.get("http://localhost:8123/api/name/", paramMap);
            System.out.println(result);
            return result;
        }


        private Map<String, String> getHeaderMap(String body){
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("accessKey", accessKey);
            //一定不能直接发送
//            headerMap.put("secretKey", secretKey);
            headerMap.put("body", body);
            headerMap.put("nonce", RandomUtil.randomNumbers(4));
            headerMap.put("sign", getSign(body, secretKey));
            headerMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
            return headerMap;
        }


        public String getUsernameByPost(User user){
            String json = JSONUtil.toJsonStr(user);
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8123/api/name/user")
                    .addHeaders(getHeaderMap(json))
                    .body(json)
                    .execute();
            System.out.println(httpResponse.getStatus());
            String result = httpResponse.body();
            System.out.println(result);
            return result;
        }

}

