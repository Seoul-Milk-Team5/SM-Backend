package Seoul_Milk.sm_server.login.controller;

import Seoul_Milk.sm_server.global.dto.response.SuccessResponse;
import Seoul_Milk.sm_server.login.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final ReissueService reissueService;
    @PostMapping("/reissue")
    public SuccessResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return SuccessResponse.ok(reissueService.reissue(request, response));
    }
}
