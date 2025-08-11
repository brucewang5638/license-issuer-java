package com.example.licenseissuer.util;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

public class KeyStoreManager {
    private Map<String, KeyPair> keyPairs = new HashMap<>();

    // 生成新密钥对，保存到Map，返回keyId
    public String generateNewKeyPair() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyUtil.generateRSAKeyPair();
        String keyId = "key-" + System.currentTimeMillis();
        keyPairs.put(keyId, keyPair);
        return keyId;
    }

    // 获取所有keyId
    public Set<String> getAllKeyIds() {
        return keyPairs.keySet();
    }

    // 根据keyId获取私钥
    public PrivateKey getPrivateKey(String keyId) {
        KeyPair pair = keyPairs.get(keyId);
        return pair == null ? null : pair.getPrivate();
    }

    // 根据keyId获取公钥
    public PublicKey getPublicKey(String keyId) {
        KeyPair pair = keyPairs.get(keyId);
        return pair == null ? null : pair.getPublic();
    }
}
