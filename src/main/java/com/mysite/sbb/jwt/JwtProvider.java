package com.mysite.sbb.jwt;

import com.mysite.sbb.user.SiteUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JwtProvider {
    private final String SECRET_KEY;

    public JwtProvider(@Value("${SECRET_KEY}")String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }


    public String generateToken(SiteUser user) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        Date expire = calendar.getTime();

        return Jwts
                .builder()
                .setSubject(user.getName())
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims decode(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
