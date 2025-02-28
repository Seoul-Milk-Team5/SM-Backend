package Seoul_Milk.sm_server.login.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "비밀번호 변경 요청 DTO")
public record UpdatePwDTO(@Schema(description = "비밀번호 입력", example = "1234") String password1,
                          @Schema(description = "비밀번호 확인", example = "1234") String password2) {

    @Builder
    public UpdatePwDTO(
            @JsonProperty("password1") String password1,
            @JsonProperty("password2") String password2
    ) {
        this.password1 = password1;
        this.password2 = password2;
    }
}
