package Seoul_Milk.sm_server.domain.taxInvoice.service;

import Seoul_Milk.sm_server.domain.taxInvoice.dto.TaxInvoiceResponseDTO;
import Seoul_Milk.sm_server.login.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TaxInvoiceService {
    CompletableFuture<Map<String, Object>> processOcrAsync(MultipartFile image, MemberEntity member);
    Page<TaxInvoiceResponseDTO.GetOne> search(MemberEntity member, String provider, String consumer, String employeeId, int page, int size);
    void delete(Long taxInvoiceId);
}
