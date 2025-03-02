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
        Digester com.branch = new Digester(DigestAlgorithm.SHA256);
        String content = body.toString() + "." + secretKey;
        String digestHex = com.branch.digestHex(content);
        return digestHex;
    }
}
