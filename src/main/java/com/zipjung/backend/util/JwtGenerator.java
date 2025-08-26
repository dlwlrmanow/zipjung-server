//package com.zipjung.backend.util;
//
//import com.zipjung.backend.entity.Member;
//import io.jsonwebtoken.Jwts;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//
//@Component
//public class JwtGenerator {
//    public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_EXPIRATION, Member member) {
//        Long now = System.currentTimeMillis();
//
//        return Jwts.builder()
//                .setHeader(createHeader())
//                .setClaims(createClaims(member))
//                .setSubject(String.valueOf(member.getId()))
//                .
//    }
//}
