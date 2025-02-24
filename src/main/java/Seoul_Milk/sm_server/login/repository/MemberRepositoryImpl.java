package Seoul_Milk.sm_server.login.repository;

import Seoul_Milk.sm_server.login.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public MemberEntity findByEmployeeId(String employeeId) {
        return memberJpaRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Boolean existsByEmployeeId(String employeeId) {
        return memberJpaRepository.existsByEmployeeId(employeeId);
    }
}
