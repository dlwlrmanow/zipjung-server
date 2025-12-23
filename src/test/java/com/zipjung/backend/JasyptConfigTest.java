//package com.zipjung.backend;
//
//import org.assertj.core.api.Assertions;
//import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
//import org.junit.jupiter.api.Test;
//
//public class JasyptConfigTest {
//    @Test
//    void jasypt(){
//        String jasyptEncryptorPassword = "JASYPT_ENCRYPT_JWT"; // yml에서 설정한 이름
//        String password = "YmN0W1b9rsBMtm7q8QDjXGjkTUJzo7NIR3jARuZIwnzYPgeFTdfVd75I+xszjeebiYl36wnjibW7kseDcw+syw=="; // 실제 암호화 할 비번
//
//        String encryptPassword = jasyptEncrypt(password, jasyptEncryptorPassword);
//
//        System.out.println("encryptPassword : " + encryptPassword);
//
//        Assertions.assertThat(password).isEqualTo(jasyptDecryt(encryptPassword, jasyptEncryptorPassword));
//    }
//
//    private String jasyptEncrypt(String input, String jasyptedPassword) {
//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//        encryptor.setPassword(jasyptedPassword); // 사용할 이름
//        encryptor.setAlgorithm("PBEWithMD5AndDES");
//        return encryptor.encrypt(input);
//    }
//
//    private String jasyptDecryt(String input, String jasyptedPassword) {
//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//        encryptor.setPassword(jasyptedPassword);
//        encryptor.setAlgorithm("PBEWithMD5AndDES");
//        return encryptor.decrypt(input);
//    }
//
//}
