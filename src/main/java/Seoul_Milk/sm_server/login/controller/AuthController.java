package Seoul_Milk.sm_server.login.controller;

import Seoul_Milk.sm_server.global.dto.response.SuccessResponse;
import Seoul_Milk.sm_server.login.dto.request.LoginRequestDTO;
import Seoul_Milk.sm_server.login.dto.response.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Operation(summary = "로그인", description = "사번과 비밀번호를 입력해 로그인합니다. 성공 시 cookie에 refreshToken(refresh=~~ 형태로 저장), header에 accessToken을 응답으로 줍니다.")
    @PostMapping("/login")
    public SuccessResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return SuccessResponse.ok(LoginResponseDTO.of("이름", "권한"));
    }
}
