package Seoul_Milk.sm_server.global.clovaOcr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateOcrField {
    private String name;
    private String inferText;
    private BoundingPoly boundingPoly;
}
