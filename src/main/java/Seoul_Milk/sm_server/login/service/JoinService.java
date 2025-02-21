package Seoul_Milk.sm_server.login.service;

import Seoul_Milk.sm_server.login.constant.Role;
import Seoul_Milk.sm_server.login.dto.JoinDTO;
import Seoul_Milk.sm_server.login.entity.MemberEntity;
import Seoul_Milk.sm_server.login.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class JoinService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public void joinProcess(JoinDTO joinDTO){
        String employeeId = joinDTO.getEmployeeId();
        String password = joinDTO.getPassword();

        Boolean isExist = memberRepository.existsByEmployeeId(employeeId);

        if(isExist){
            return;
        }

        MemberEntity data = new MemberEntity();
        data.setEmployeeId(employeeId);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole(Role.ROLE_NORMAL);

        memberRepository.save(data);
    }
}
