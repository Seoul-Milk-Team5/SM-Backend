package Seoul_Milk.sm_server.login.repository;

import Seoul_Milk.sm_server.login.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByEmployeeId(String employeeId);
    Boolean existsByEmployeeId(String employeeId);
}
