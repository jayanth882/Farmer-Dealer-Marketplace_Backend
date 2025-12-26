package agriculture2.demo.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys; // This import will resolve after Maven Update
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {
    // Note: Using a Base64 key that is long enough
    private final String jwtSecret = "M2I5YmQ4ZmM0MDMzNTg5YzI3NWMzMmUyOGQ3YzgxMzY4YmI5NGRiNGE5NTZiM2RlYjkwN2I0NGFjYzc3ODQz"; 
    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    private final long jwtExpirationMs = 86400000; // 1 day

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // Log the exception here for better debugging, e.g., token expired, malformed, etc.
            return false;
        }
    }
}