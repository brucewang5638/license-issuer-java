package com.example.licenseissuer.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class KeyExportUtil {
    public static void exportPublicKey(PublicKey publicKey, String filePath) throws IOException {
        byte[] encoded = publicKey.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(encoded);
        }
    }
}
