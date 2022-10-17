package com.huyeon.superspace.global.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {

    @DisplayName("Jasypt DB 정보 암호화")
    @Test
    void test_1(){
        //DB 정보 변경시마다 암호화해서 사용
        String url = "";
        String username = "";
        String password = "";

        System.out.println(jasyptEncoding(url));
        System.out.println(jasyptEncoding(username));
        System.out.println(jasyptEncoding(password));
    }

    private String jasyptEncoding(String value) {
        String key = "huyeon/super-space";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
