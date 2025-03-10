package Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.controller;

import Seoul_Milk.sm_server.domain.taxInvoice.enums.ProcessStatus;
import Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.dto.TaxInvoiceValidationHistoryResponseDTO;
import Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.dto.TaxInvoiceValidationHistoryRequestDTO.ChangeTaxInvoiceRequest;
import Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.dto.TaxInvoiceValidationHistoryRequestDTO.TaxInvoiceRequest;
import Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.service.TaxInvoiceValidationService;
import Seoul_Milk.sm_server.global.annotation.CurrentMember;
import Seoul_Milk.sm_server.global.dto.response.SuccessResponse;
import Seoul_Milk.sm_server.login.entity.MemberEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validation/history")
@RequiredArgsConstructor
@Tag(name = "검증내역 화면 부분<RE_01>")
public class TaxInvoiceValidationHistoryController {
    private final TaxInvoiceValidationService taxInvoiceValidationService;

    @Operation(summary = "<RE_01> 공급자 또는 공급받는자 검색 기능 + 승인, 반려, 검증실패별 조회 api")
    @GetMapping("/search")
    public SuccessResponse<TaxInvoiceValidationHistoryResponseDTO.TaxInvoiceSearchResult> searchAndShowByFilter(
            @CurrentMember MemberEntity memberEntity,
            @RequestParam(value = "poc") String poc,
            @RequestParam(value = "status") @Nullable ProcessStatus processStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return SuccessResponse.ok(taxInvoiceValidationService.searchByProviderOrConsumer(memberEntity,processStatus, poc, page-1, size));
    }

    @Operation(summary = "<RE_01> 검증 내역 삭제 api")
    @DeleteMapping
    public SuccessResponse<Void> deleteValidationTaxInvoice(
            @CurrentMember MemberEntity memberEntity,
            @RequestBody TaxInvoiceRequest taxInvoiceRequest
    ){
        return SuccessResponse.ok(taxInvoiceValidationService.deleteValidationTaxInvoice(memberEntity,
                taxInvoiceRequest));
    }

    @Operation(summary = "<RE_01> 임시 저장 api")
    @PostMapping("/temp")
    public SuccessResponse<Void> tempSave(
            @CurrentMember MemberEntity memberEntity,
            @RequestBody TaxInvoiceRequest taxInvoiceRequest
    ){
        return SuccessResponse.ok(taxInvoiceValidationService.tempSave(memberEntity, taxInvoiceRequest));
    }

    @Operation(summary = "<RE_02> 승인/반려/수정된 결과 조회 모달 띄우기 api")
    @GetMapping("/modal/{taxInvoiceId}")
    public SuccessResponse<TaxInvoiceValidationHistoryResponseDTO.GetModalResponse> showModal(
            @PathVariable(name = "taxInvoiceId") Long taxInvoiceId
    ){
        return SuccessResponse.ok(taxInvoiceValidationService.showModal(taxInvoiceId));
    }

    @Operation(summary = "<RE_03> 필수컬럼 수정 api")
    @PostMapping("/change")
    public SuccessResponse<Void> changeColunm(
            @CurrentMember MemberEntity memberEntity,
            @RequestBody ChangeTaxInvoiceRequest changeTaxInvoiceRequest
    ){
        return SuccessResponse.ok(taxInvoiceValidationService.changeColunm(memberEntity, changeTaxInvoiceRequest));
    }

}
