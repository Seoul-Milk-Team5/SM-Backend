package Seoul_Milk.sm_server.login.service;

import static Seoul_Milk.sm_server.global.cookie.CookieClass.createCookie;
import static Seoul_Milk.sm_server.global.token.Token.ACCESS_TOKEN;
import static Seoul_Milk.sm_server.global.token.Token.REFRESH_TOKEN;

import Seoul_Milk.sm_server.global.exception.CustomException;
import Seoul_Milk.sm_server.global.exception.ErrorCode;
import Seoul_Milk.sm_server.global.jwt.JWTUtil;
import Seoul_Milk.sm_server.global.refresh.RefreshToken;
import Seoul_Milk.sm_server.login.repository.MemberRepository;
import Seoul_Milk.sm_server.login.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;
    public Object reissue(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        String refresh = Arrays.stream(cookies)
                .filter(cookie -> REFRESH_TOKEN.category().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            //response status code
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals(REFRESH_TOKEN.category())) {
            //response status code
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            //response body
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String employeeId = jwtUtil.getEmployeeId(refresh);
        String role = jwtUtil.getRole(refresh).toString();

        //make new JWT
        String newAccess = jwtUtil.createJwt(ACCESS_TOKEN.category(), employeeId, role, ACCESS_TOKEN.expireMs());
        String newRefresh = jwtUtil.createJwt(REFRESH_TOKEN.category(), employeeId, role, REFRESH_TOKEN.expireMs());

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefreshToken(refresh);
        RefreshToken refreshToken = new RefreshToken(memberRepository, refreshRepository);
        refreshToken.addRefreshEntity(employeeId, newRefresh, REFRESH_TOKEN.expireMs());

        //response
        response.setHeader(ACCESS_TOKEN.category(), newAccess);
        response.addCookie(createCookie(REFRESH_TOKEN.category(), newRefresh));
        return null;
    }
}
