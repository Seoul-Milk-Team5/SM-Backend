package Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.enums;

/**
 * RE_01 조회 api에서 최대로 조회할 수 있는 개수
 */
public enum MaxSearchLimit {
    MAX_SEARCH_LIMIT(100);

    private final int maxLimit;

    MaxSearchLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    public Integer getNum(){
        return this.maxLimit;
    }
}
