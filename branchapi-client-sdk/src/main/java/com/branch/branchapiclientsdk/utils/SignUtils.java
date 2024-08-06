package com.branch.branchapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class SignUtils {

    /**
     *签名工具
     * @param body
     * @param secretKey
     * @return
     */
    public static String getSign (String body, String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body.toString() + "." + secretKey;
        String digestHex = md5.digestHex(content);
        return digestHex;
    }
}
