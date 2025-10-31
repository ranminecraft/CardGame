package cc.ranmc.game.card.server.util;

import cc.ranmc.game.card.common.constant.JsonKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cc.ranmc.game.card.common.constant.GameInfo.AUTHOR;
import static cc.ranmc.game.card.server.util.ConfigUtil.CONFIG;

public class JwtTokenUtil {

    private final static Long EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7L;
    public static final SecretKey SECRET = Keys.hmacShaKeyFor(CONFIG.getString(JsonKey.SECRET).getBytes());

    private static Map<String,Object> initClaims(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", AUTHOR);
        claims.put("sub", email);
        claims.put("exp", new Date(System.currentTimeMillis() + EXPIRE_TIME));
        claims.put("aud", email);
        claims.put("iat", new Date());
        claims.put("jti", UUID.randomUUID().toString());
        return claims;
    }

    public static String generatorToken(String email) {
        Map<String, Object> claims = initClaims(email);
        return Jwts.builder()
                .claims(claims)
                .signWith(SECRET, Jwts.SIG.HS256)
                .compact();
    }

    public static String getEmail(String token) throws Exception {
        String email;
        try {
            email = getPayload(token).getSubject();
        } catch (Exception e){
            throw new Exception();
        }
        return email;
    }

    private static Claims getPayload(String token) throws Exception {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(SECRET)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new Exception();
        }
        return claims;
    }

    public static boolean validate(String token) {
        try {
            return getPayload(token).getIssuer().equals(AUTHOR) && !isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isExpired(String token) throws Exception {
        return getPayload(token).getExpiration().before(new Date());
    }
}
