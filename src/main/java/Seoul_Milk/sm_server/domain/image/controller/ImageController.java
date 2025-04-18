package Seoul_Milk.sm_server.domain.image.controller;

import Seoul_Milk.sm_server.domain.image.dto.ImageResponseDTO;
import Seoul_Milk.sm_server.domain.image.service.ImageService;
import Seoul_Milk.sm_server.global.common.annotation.CurrentMember;
import Seoul_Milk.sm_server.global.common.response.SuccessResponse;
import Seoul_Milk.sm_server.domain.member.entity.MemberEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Builder
@RequestMapping("/api/image/tmp")
@RequiredArgsConstructor
@Tag(name = "이미지 임시 저장 API")
public class ImageController {

    private final ImageService imageService;

    /**
     * 특정 멤버의 임시 저장된 이미지 전체 조회
     * @param member 로그인 유저
     * @return 임시 저장된 이미지 리스트
     */
    @Operation(summary = "임시 저장된 이미지 전체 조회 (페이지네이션)")
    @GetMapping
    public SuccessResponse<Page<ImageResponseDTO.GetOne>> getAllTemporary(
            @CurrentMember MemberEntity member,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<ImageResponseDTO.GetOne> result = imageService.getAll(member, page-1, size);
        return SuccessResponse.ok(result);
    }

    /**
     * 특정 ID 리스트를 찾아 임시 저장 등록
     * @param files 임시 저장할 이미지 리스트
     * @param member 로그인 유저
     * @return 성공 메시지
     */
    @Operation(summary = "여러 개의 이미지를 임시 저장 상태로 등록")
    @PostMapping(value = "/mark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<String> markAsTemporary(
            @RequestPart(value = "requests", required = false) String requestsJson,
            @RequestPart(value = "files") List<MultipartFile> files,
            @CurrentMember MemberEntity member) throws JsonProcessingException {

        imageService.markAsTemporary(requestsJson, files, member);
        return SuccessResponse.ok("선택한 이미지들이 임시 저장으로 설정되었습니다.");
    }

    /**
     * 해당 유저의 모든 임시 저장 이미지를 해제
     * @param member 로그인 유저
     * @return 성공 메시지
     */
    @Operation(summary = "리스트(ID)로 받은 임시 저장 이미지를 해제")
    @PatchMapping("/unmark")
    public SuccessResponse<String> removeFromTemporary(
            @CurrentMember MemberEntity member,
            @RequestParam(value = "imageIds") List<Long> imageIds
    ) {
        imageService.removeFromTemporary(member, imageIds);
        return SuccessResponse.ok("모든 임시 저장 이미지가 해제되었습니다.");
    }
}
