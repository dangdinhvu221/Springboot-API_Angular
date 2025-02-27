package com.project.shopapp.components;


import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component // Đánh dấu class là một Bean để Spring quản lý
@RequiredArgsConstructor // Tự động tạo constructor cho các biến final
public class JwtTokenUtil {

    @Value("${jwt.expiration}")
    private int expiration; // Thời gian hết hạn của token, lấy từ cấu hình

    @Value("${jwt.securityKey}")
    private String securityKey; // Khóa bảo mật dùng để ký token, lấy từ cấu hình

    // Hàm tạo ra JWT token
    public String generateToken(User user) throws Exception{
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey(); //gen key token
        claims.put("phoneNumber", user.getPhoneNumber()); // Thêm thông tin vào claims

        try {
            String token = Jwts.builder()
                    .setClaims(claims) // Thêm danh sách claims
                    .setSubject(user.getPhoneNumber()) // Định danh người sở hữu token
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // Thời gian hết hạn của token
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Ký token với thuật toán HS256
                    .compact(); // Hoàn thành và trả về chuỗi token
            return token;
        } catch (Exception e) {
            throw new InvalidParameterException("Cannot create jwt token, error: " + e.getMessage());
            //return null; // Nếu có lỗi, trả về null
        }
    }

    // Hàm lấy khóa ký từ securityKey
    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(securityKey); // Giải mã khóa bảo mật từ Base64
        return Keys.hmacShaKeyFor(bytes); // Tạo khóa HMAC SHA256
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String secretKey = Encoders.BASE64.encode(bytes);
        return secretKey;
    }

    // Hàm trích xuất tất cả claims từ token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Xác minh chữ ký bằng khóa bảo mật
                .build()
                .parseClaimsJws(token) // Phân tích token JWT
                .getBody(); // Lấy phần nội dung claims của token
    }

    // Hàm trích xuất một claim cụ thể từ token
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = this.extractAllClaims(token); // Lấy toàn bộ claims
        return claimResolver.apply(claims); // Lấy giá trị claim cụ thể
    }

    // Hàm kiểm tra token đã hết hạn chưa
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration); // Lấy ngày hết hạn của token
        return expirationDate.before(new Date()); // So sánh với thời gian hiện tại
    }

    public String extractPhonenumber(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String phoneNumber = extractPhonenumber(token);
        return (phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

