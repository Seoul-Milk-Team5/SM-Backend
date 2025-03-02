package Seoul_Milk.sm_server.domain.taxValidation.service;

import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.APPROVAL_NO;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.CONTRACTOR_REG_NUMBER;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.ID;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.IDENTITY;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.LOGIN_TYPE;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.LOGIN_TYPE_LEVEL;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.ORGANIZATION;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.PHONE_NO;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.REPORTING_DATE;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.SUPPLIER_REG_NUMBER;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.SUPPLY_VALUE;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.TELECOM;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.TWO_WAY_INFO;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.CodefParameters.USER_NAME;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.TwoWayInfo.JOB_INDEX;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.TwoWayInfo.JTI;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.TwoWayInfo.THREAD_INDEX;
import static Seoul_Milk.sm_server.domain.taxValidation.enums.TwoWayInfo.TWO_WAY_TIMESTAMP;

import Seoul_Milk.sm_server.domain.taxInvoice.constant.ProcessStatus;
import Seoul_Milk.sm_server.domain.taxInvoice.entity.TaxInvoice;
import Seoul_Milk.sm_server.domain.taxInvoice.repository.TaxInvoiceRepository;
import Seoul_Milk.sm_server.domain.taxValidation.dto.NonVerifiedTaxValidationRequestDTO;
import Seoul_Milk.sm_server.domain.taxValidation.dto.NonVerifiedTaxValidationResponseDTO;
import Seoul_Milk.sm_server.domain.taxValidation.dto.TaxInvoiceInfo;
import Seoul_Milk.sm_server.domain.taxValidation.thread.RequestThread;
import Seoul_Milk.sm_server.global.redis.RedisUtils;
import Seoul_Milk.sm_server.login.entity.MemberEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaxValidationServiceImpl implements TaxValidationService {
    private final String PUBLIC_KEY;
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String PRODUCT_URL;
    private final RedisUtils redisUtils;
    private final TaxInvoiceRepository taxInvoiceRepository;
    public TaxValidationServiceImpl(
            @Value("${codef.public.key}") String PUBLIC_KEY,
            @Value("${codef.client.id}") String CLIENT_ID,
            @Value("${codef.client.secret}") String CLIENT_SECRET,
            @Value("${codef.valid.url}") String productUrl, RedisUtils redisUtils,
            TaxInvoiceRepository taxInvoiceRepository
    ) {
        this.PUBLIC_KEY = PUBLIC_KEY;
        this.CLIENT_ID = CLIENT_ID;
        this.CLIENT_SECRET = CLIENT_SECRET;
        this.PRODUCT_URL = productUrl;
        this.redisUtils = redisUtils;
        this.taxInvoiceRepository = taxInvoiceRepository;
    }
    private EasyCodef settingCodef(){
        EasyCodef easyCodef = new EasyCodef();
        easyCodef.setClientInfoForDemo(CLIENT_ID, CLIENT_SECRET);
        easyCodef.setPublicKey(PUBLIC_KEY);
        return easyCodef;
    }
    @Override
    public NonVerifiedTaxValidationResponseDTO nonVerifiedTaxValidation(
            NonVerifiedTaxValidationRequestDTO nonVerifiedTaxValidationRequestDTO,
            MemberEntity memberEntity)
            throws InterruptedException {
        EasyCodef easyCodef = settingCodef();
        List<TaxInvoiceInfo> taxInvoiceInfoList = nonVerifiedTaxValidationRequestDTO.getTaxInvoiceInfoList();
        String id = memberEntity.makeUniqueId();
        int iter = taxInvoiceInfoList.size();
        for(int i=0; i<iter; i++) {
            TaxInvoiceInfo taxInvoiceInfo = taxInvoiceInfoList.get(i);
            // 공통 파라미터 설정
            HashMap<String, Object> requestData = populateParameters(id, Map.of(
                    LOGIN_TYPE_LEVEL.getKey(), nonVerifiedTaxValidationRequestDTO.getLoginTypeLevel(),
                    USER_NAME.getKey(), nonVerifiedTaxValidationRequestDTO.getUserName(),
                    PHONE_NO.getKey(), nonVerifiedTaxValidationRequestDTO.getPhoneNo(),
                    IDENTITY.getKey(), nonVerifiedTaxValidationRequestDTO.getIdentity(),
                    TELECOM.getKey(), nonVerifiedTaxValidationRequestDTO.getTelecom()
            ), Map.of(
                    SUPPLIER_REG_NUMBER.getKey(), taxInvoiceInfo.getSupplierRegNumber(),
                    CONTRACTOR_REG_NUMBER.getKey(), taxInvoiceInfo.getContractorRegNumber(),
                    APPROVAL_NO.getKey(), taxInvoiceInfo.getApprovalNo(),
                    REPORTING_DATE.getKey(), taxInvoiceInfo.getReportingDate(),
                    SUPPLY_VALUE.getKey(), taxInvoiceInfo.getSupplyValue()
            ));

            Thread t = new RequestThread(id, easyCodef, requestData, i, PRODUCT_URL, redisUtils, taxInvoiceRepository);

            t.start();

            // API 요청A와 요청B 다건 요청을 위해서는 요청A 처리 후 요청B를 처리할 수 있도록
            // 요청A 송신 후 약 0.5초 ~ 1초 이내 요청B 송신 필요
            Thread.sleep(1000);
        }
        return new NonVerifiedTaxValidationResponseDTO(id);
    }

    @Override
    public String verifiedTaxValidation(String key)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        EasyCodef easyCodef = settingCodef();

        Map<String, Object> addAuthResponse = redisUtils.getCodefApiResponse(key);
        Map<String, Object> commonResponse = redisUtils.getCodefApiResponse(key+"common");
        Map<String, Object> firstResponse = redisUtils.getCodefApiResponse(key+"first");

        HashMap<String, Object> parameterMap = populateParameters(key, commonResponse, firstResponse);

        //간편인증 추가인증 입력부
        parameterMap.put("simpleAuth", "1");
        parameterMap.put("is2Way", true);

        /** #3.twoWayInfo 파라미터 설정*/
        HashMap<String, Object> twoWayInfo = twoWayInfoParameters(addAuthResponse);

        parameterMap.put(TWO_WAY_INFO.getKey(), twoWayInfo);

        // 요청 Endpoint는 동일함
        String result;

        // 추가인증 요청 시에는 이지코드에프.requestCertification 으로 호출
        result = easyCodef.requestCertification(PRODUCT_URL, EasyCodefServiceType.DEMO, parameterMap);

        TaxInvoice taxInvoice = taxInvoiceRepository.findByIssueId(firstResponse.get(APPROVAL_NO.getKey()).toString());
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String resAuthenticity = rootNode.path("data").path("resAuthenticity").asText();
        if(Objects.equals(resAuthenticity, "1")){
            taxInvoice.changeStatus(ProcessStatus.APPROVED);
        }else{
            taxInvoice.changeStatus(ProcessStatus.REJECTED);
        }
        taxInvoiceRepository.save(taxInvoice);

        return "성공";
    }

    /**
     * verifiedTaxValidation과
     * nonVerifiedTaxValidation이 공통으로 필요한 파라미터 넣는 메서드
     */
    private HashMap<String, Object> populateParameters(String id, Map<String, Object> commonResponse, Map<String, Object> firstResponse){
        HashMap<String, Object> parameterMap = new HashMap<>();

        // 필수 입력값 설정
        parameterMap.put(ORGANIZATION.getKey(), "0004");
        parameterMap.put(ID.getKey(), id);
        parameterMap.put(LOGIN_TYPE.getKey(), "5");

        // 공통 응답에서 가져오기

        parameterMap.put(LOGIN_TYPE_LEVEL.getKey(), commonResponse.get(LOGIN_TYPE_LEVEL.getKey()));
        parameterMap.put(USER_NAME.getKey(), commonResponse.get(USER_NAME.getKey()));
        parameterMap.put(PHONE_NO.getKey(), commonResponse.get(PHONE_NO.getKey()));
        parameterMap.put(IDENTITY.getKey(), commonResponse.get(IDENTITY.getKey()));
        parameterMap.put(TELECOM.getKey(), commonResponse.get(TELECOM.getKey()));


        // 첫 번째 응답에서 가져오기
        parameterMap.put(SUPPLIER_REG_NUMBER.getKey(), firstResponse.get(SUPPLIER_REG_NUMBER.getKey()));
        parameterMap.put(CONTRACTOR_REG_NUMBER.getKey(), firstResponse.get(CONTRACTOR_REG_NUMBER.getKey()));
        parameterMap.put(APPROVAL_NO.getKey(), firstResponse.get(APPROVAL_NO.getKey()));
        parameterMap.put(REPORTING_DATE.getKey(), firstResponse.get(REPORTING_DATE.getKey()));
        parameterMap.put(SUPPLY_VALUE.getKey(), firstResponse.get(SUPPLY_VALUE.getKey()));

        return parameterMap;
    }

    private HashMap<String, Object> twoWayInfoParameters(Map<String, Object> addAuthResponse){
        HashMap<String, Object> twoWayInfo = new HashMap<String, Object>();

        twoWayInfo.put(JOB_INDEX.setCarmelCase(), Integer.parseInt((String) addAuthResponse.get(JOB_INDEX.name())));
        twoWayInfo.put(THREAD_INDEX.setCarmelCase(), Integer.parseInt((String) addAuthResponse.get(THREAD_INDEX.name())));
        twoWayInfo.put(JTI.setCarmelCase(),  addAuthResponse.get(JTI.name()));
        twoWayInfo.put(TWO_WAY_TIMESTAMP.setCarmelCase(), Long.parseLong((String) addAuthResponse.get(TWO_WAY_TIMESTAMP.name())));
        return twoWayInfo;
    }
}
