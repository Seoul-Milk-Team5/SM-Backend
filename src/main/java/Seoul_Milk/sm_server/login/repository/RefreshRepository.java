package Seoul_Milk.sm_server.login.repository;

import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository {
    Boolean existsByRefreshToken(String refreshToken);

    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
