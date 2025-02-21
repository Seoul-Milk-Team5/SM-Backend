package Seoul_Milk.sm_server.global.refresh;

import Seoul_Milk.sm_server.login.entity.RefreshEntity;
import Seoul_Milk.sm_server.login.repository.MemberRepository;
import Seoul_Milk.sm_server.login.repository.RefreshRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshToken {
    private final MemberRepository memberRepository;
    private final RefreshRepository refreshRepository;

    public void addRefreshEntity(String employeeId, String refreshToken, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setMemberEntity(memberRepository.findByEmployeeId(employeeId));
        refreshEntity.setRefreshToken(refreshToken);
        refreshEntity.setExpiration(date);

        refreshRepository.save(refreshEntity);
    }
}
