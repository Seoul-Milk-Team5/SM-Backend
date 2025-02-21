package Seoul_Milk.sm_server.global.cookie;

import jakarta.servlet.http.Cookie;

public class CookieClass {
    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키 생명주기
        //cookie.setSecure(true); //https 통신 시
        //cookie.setPath("/"); // 쿠키가 적용될 범위
        cookie.setHttpOnly(true); // 클라이언트 단에서 JS로 해당 쿠키 접근 못하도록 막아야함
        return cookie;
    }
}
