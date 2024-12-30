package com.dev.englishapp.security;

import com.dev.englishapp.exception.AppAPIException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${app-jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication){

        String username = authentication.getName();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();

        return token;
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

    public String getUsername(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){

        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);

            return true;
        }catch (MalformedJwtException malformedJwtException){
            throw new AppAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        }catch (ExpiredJwtException expiredJwtException){
            throw new AppAPIException(HttpStatus.BAD_REQUEST, "Expired JWT Token");
        }catch (UnsupportedJwtException unsupportedJwtException){
            throw new AppAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT Token");
        }catch (IllegalArgumentException illegalArgumentException){
            throw new AppAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is null or empty");
        }


    }

}
