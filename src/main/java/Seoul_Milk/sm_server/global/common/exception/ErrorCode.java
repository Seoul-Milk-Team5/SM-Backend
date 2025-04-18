package Seoul_Milk.sm_server.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 에러코드 규약
     * HTTP Status Code는 에러에 가장 유사한 코드를 부여한다.
     * 사용자정의 에러코드는 중복되지 않게 배정한다.
     * 사용자정의 에러코드는 각 카테고리 이름과 숫자를 조합하여 명확성을 더한다.
     */

    /**
     * 400 : 잘못된 요청
     * 401 : 인증되지 않은 요청
     * 403 : 권한의 문제가 있을때
     * 404 : 요청한 리소스가 존재하지 않음
     * 409 : 현재 데이터와 값이 충돌날 때(ex. 아이디 중복)
     * 412 : 파라미터 값이 뭔가 누락됐거나 잘못 왔을 때
     * 422 : 파라미터 문법 오류
     * 424 : 뭔가 단계가 꼬였을때, 1번안하고 2번하고 그런경우
     */

    // Common
    SERVER_UNTRACKED_ERROR("COMMON500", "미등록 서버 에러입니다. 서버 팀에 연락주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("COMMON400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("COMMON401", "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COMMON403", "권한이 부족합니다.", HttpStatus.FORBIDDEN),
    OBJECT_NOT_FOUND("COMMON404", "조회된 객체가 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PARAMETER("COMMON422", "잘못된 파라미터입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    PARAMETER_VALIDATION_ERROR("COMMON422", "파라미터 검증 에러입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    PARAMETER_GRAMMAR_ERROR("COMMON422", "파라미터 문법 에러입니다.", HttpStatus.UNPROCESSABLE_ENTITY),

    // Token
    TOKEN_INVALID("TOKEN401", "유효하지 않은 Token 입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_ROLE("TOKEN401", "JWT 토큰에 Role 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("TOKEN401", "Access Token 이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID("TOKEN401", "유효하지 않은 Access Token 입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("TOKEN404", "해당 사용자에 대한 Refresh Token 을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_MISMATCH("TOKEN401", "Refresh Token 이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("TOKEN401", "Refresh Token 이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("TOKEN401", "유효하지 않은 Refresh Token 입니다.", HttpStatus.UNAUTHORIZED),

    // User (회원)
    USER_ALREADY_EXIST("USER400", "이미 회원가입된 유저입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST("USER404", "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    USER_EMPLOYEE_ID_NOT_EXIST("USER404", "존재하지 않는 사번입니다.", HttpStatus.NOT_FOUND),
    USER_NOT_VALID("USER404", "유효한 사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_WRONG_PASSWORD("USER401", "비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED),
    USER_SAME_PASSWORD("USER400", "동일한 비밀번호로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    PASSWORDS_NOT_MATCH("PASSWORD401", "입력한 두 개의 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NO_PERMISSION("USER403", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    USER_FORBIDDEN("USER403", "유저의 권한이 부족합니다.", HttpStatus.FORBIDDEN),

    // Role
    INVALID_ROLE("ROLE400", "잘못된 Role 값입니다.", HttpStatus.BAD_REQUEST),

    // 세금계산서 (TaxInvoice)
    TAX_INVOICE_NOT_EXIST("TAX_INVOICE404", "존재하지 않는 세금계산서입니다.", HttpStatus.NOT_FOUND),
    TAX_INVOICE_ALREADY_EXIST("TAX_INVOICE400", "이미 등록된 승인번호의 세금계산서입니다.", HttpStatus.BAD_REQUEST),
    DO_NOT_ACCESS_OTHER_TAX_INVOICE("TAX_INVOICE401", "다른 사람의 세금계산서에 접근할 수 없습니다.", HttpStatus.UNAUTHORIZED),

    // UPLOAD (업로드)
    UPLOAD_FAILED("UPLOAD001", "업로드 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_FAILED("DELETE001", "삭제 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_URL("URL400", "유효하지 않은 파일 URL입니다.", HttpStatus.BAD_REQUEST),
    INVALID_URL_FORM("URL400", "URL 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // OCR
    OCR_EMPTY_JSON("OCR400", "OCR JSON 응답이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    OCR_INVALID_JSON("OCR422", "OCR JSON 응답이 잘못된 JSON 형식입니다.", HttpStatus.BAD_REQUEST),
    OCR_NO_IMAGES("OCR404", "OCR 응답 JSON에서 images 필드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OCR_NO_FIELDS("OCR404", "OCR 결과에 fields 데이터가 없습니다.", HttpStatus.NOT_FOUND),
    OCR_NO_RESULT("OCR404", "OCR API에서 반환된 결과가 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_REGISTRATION_NUMBERS("OCR422", "등록번호가 2개 미만입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    OCR_NO_BOUNDING_POLY("OCR422", "OCR 필드에서 boundingPoly를 찾을 수 없습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    OCR_MISSING_BUSINESS_FIELDS("OCR422", "등록번호를 기준으로 '상호', '성명', '사업장' 필드를 찾지 못했습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    OCR_NO_BUSINESS_NAME_CANDIDATES("OCR404", "필터링된 상호명 후보가 없습니다.", HttpStatus.NOT_FOUND),
    OCR_FIELD_CONVERSION_ERROR("OCR500", "OCR 필드 변환 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    OCR_JSON_PARSING_ERROR("OCR500", "OCR JSON 파싱 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    OCR_INVALID_FILE("OCR500", "OCR에 올바르지 않은 파일이 들어왔습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    OCR_FILE_DOWNLOAD_FAILED("OCR500", "OCR 과정에서 S3 이미지를 다운로드 받지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // PROCESS_STATUS (승인 상태)
    PROCESS_STATUS_INVALID("PROCESS_STATUS400", "존재하지 않은 승인 상태입니다. UNAPPROVED, APPROVED, REJECTED 중 하나로 입력하세요.", HttpStatus.BAD_REQUEST),

    // EMAIL
    EMAIL_VERIFICATION_EXPIRED("EMAIL_VERIFICATION404", "인증 코드가 만료되었습니다.", HttpStatus.NOT_FOUND),
    EMAIL_VERIFICATION_INVALID("EMAIL_VERIFICATION401", "유효하지 않은 인증 코드입니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_SEND_FAIL("EMAIL500", "메일 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_AUTH_FAIL("EMAIL401", "이메일 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_REQUEST_LIMIT_EXCEEDED("EMAIL429", "5분 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS),

    // JSON
    JSON_PARSING_FAILED("JSON001", "JSON 파싱에 실패했습니다.", HttpStatus.BAD_REQUEST),

    // CODEF
    CODEF_INTERANL_SERVER_ERROR("CODEF001", "codef api 연동에 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    CODEF_NEED_AUTHENTICATION("CODEF002", "간편인증이 진행되지 않았습니다 진행 후 다시 시도해주세요.", HttpStatus.UNAUTHORIZED),

    // S3
    S3_FILE_NOT_FOUND("S3404", "S3에서 이동된 파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    S3_FILE_MOVE_FAILED("S3500", "S3 파일 이동 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),

    // IMAGE (임시저장 이미지)
    TMP_IMAGE_NOT_EXIST("TMP_IMAGE404", "해당 임시저장 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("TMP_IMAGE401", "본인의 임시저장 이미지만 삭제할 수 있습니다.", HttpStatus.UNAUTHORIZED),

    //excel (엑셀파일)
    MAKE_EXCEL_FILE_ERROR("EXTRACT_EXCEL001", "엑셀파일로 추출하던중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}