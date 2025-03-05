package Seoul_Milk.sm_server.domain.taxInvoice.repository;

import static Seoul_Milk.sm_server.domain.taxInvoice.enums.ProcessStatus.APPROVED;
import static Seoul_Milk.sm_server.domain.taxInvoice.enums.ProcessStatus.REJECTED;
import static Seoul_Milk.sm_server.domain.taxInvoice.enums.ProcessStatus.UNAPPROVED;
import static Seoul_Milk.sm_server.domain.taxInvoice.enums.TempStatus.INITIAL;
import static Seoul_Milk.sm_server.domain.taxInvoice.enums.TempStatus.TEMP;
import static Seoul_Milk.sm_server.domain.taxInvoice.enums.TempStatus.UNTEMP;
import static Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.enums.MaxSearchLimit.MAX_SEARCH_LIMIT;

import Seoul_Milk.sm_server.domain.taxInvoice.entity.QTaxInvoice;
import Seoul_Milk.sm_server.domain.taxInvoice.entity.TaxInvoice;
import Seoul_Milk.sm_server.domain.taxInvoice.enums.ProcessStatus;
import Seoul_Milk.sm_server.domain.taxInvoice.enums.TempStatus;
import Seoul_Milk.sm_server.domain.taxInvoiceFile.entity.QTaxInvoiceFile;
import Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.dto.TaxInvoiceSearchResult;
import Seoul_Milk.sm_server.domain.taxInvoiceValidationHistory.enums.MaxSearchLimit;
import Seoul_Milk.sm_server.global.exception.CustomException;
import Seoul_Milk.sm_server.global.exception.ErrorCode;
import Seoul_Milk.sm_server.login.constant.Role;
import Seoul_Milk.sm_server.login.entity.MemberEntity;
import Seoul_Milk.sm_server.login.entity.QMemberEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaxInvoiceRepositoryImpl implements TaxInvoiceRepository {

    private final TaxInvoiceJpaRepository taxInvoiceJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public TaxInvoice getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TAX_INVOICE_NOT_EXIST));
    }

    @Override
    public Optional<TaxInvoice> findById(Long id) {
        return taxInvoiceJpaRepository.findById(id);
    }

    @Override
    public TaxInvoice save(TaxInvoice taxInvoice) {
        return taxInvoiceJpaRepository.save(taxInvoice);
    }

    @Override
    public void delete(Long id) {
        taxInvoiceJpaRepository.deleteById(id);
    }

    @Override
    public Page<TaxInvoice> searchWithFilters(String provider, String consumer, String employeeId, MemberEntity member,
                                              LocalDate startDate, LocalDate endDate, ProcessStatus status, Pageable pageable) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        QMemberEntity memberEntity = QMemberEntity.memberEntity;
        QTaxInvoiceFile taxInvoiceFile = QTaxInvoiceFile.taxInvoiceFile;

        BooleanBuilder whereClause = new BooleanBuilder();

        // 권한 조건
        if (member.getRole() == Role.ROLE_NORMAL) {
            whereClause.and(taxInvoice.member.employeeId.eq(member.getEmployeeId()));
        }
        if (member.getRole() == Role.ROLE_ADMIN && employeeId != null && !employeeId.isEmpty()) {
            whereClause.and(taxInvoice.member.employeeId.eq(employeeId));
        }

        // 공급자 검색 조건
        if (provider != null && !provider.isEmpty()) {
            whereClause.and(taxInvoice.ipName.eq(provider));
        }

        // 공급받는자 검색 조건
        if (consumer != null && !consumer.isEmpty()) {
            whereClause.and(taxInvoice.suName.eq(consumer));
        }

        // 날짜 검색 (특정 날짜 or 최근 N개월 내)
        if (startDate != null && endDate != null) {
            whereClause.and(taxInvoice.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }

        // 승인 상태 조건
        if (status != null) {
            whereClause.and(taxInvoice.processStatus.eq(status));
        }

        long total = Optional.ofNullable( //전체개수 파악
                queryFactory
                        .select(Wildcard.count)
                        .from(taxInvoice)
                        .where(whereClause)
                        .fetchOne()
        ).orElse(0L);

        List<TaxInvoice> results = queryFactory
                .selectFrom(taxInvoice)
                .leftJoin(taxInvoice.member, memberEntity).fetchJoin()
                .leftJoin(taxInvoice.file, taxInvoiceFile).fetchJoin()
                .where(whereClause.and(taxInvoice.file.isNotNull()))
                .where(whereClause)
                .orderBy(taxInvoice.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<TaxInvoice> findTempInvoicesByMember(MemberEntity member) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;

        return queryFactory.selectFrom(taxInvoice)
                .where(
                        taxInvoice.member.eq(member),
                        taxInvoice.isTemporary.eq(TEMP)
                )
                .fetch();
    }

    @Override
    public List<TaxInvoice> findTempInvoicesByIds(List<Long> taxInvoiceIds, MemberEntity member) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;

        return queryFactory.selectFrom(taxInvoice)
                .where(
                        taxInvoice.taxInvoiceId.in(taxInvoiceIds),
                        taxInvoice.member.eq(member)
                )
                .fetch();
    }

    @Override
    public List<TaxInvoice> saveAll(List<TaxInvoice> taxInvoices) {
        return taxInvoiceJpaRepository.saveAll(taxInvoices);
    }

    @Override
    public List<TaxInvoice> findAll() {
        return taxInvoiceJpaRepository.findAll();
    }

    @Override
    public long getProcessStatusCount(ProcessStatus processStatus, MemberEntity member) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(taxInvoice.member.id.eq(member.getId()));
        if(processStatus != null){
            whereClause.and(taxInvoice.processStatus.eq(processStatus));
        }
        //최신 100개 데이터만 고려
        List<Long> latestIds = queryFactory
                .select(taxInvoice.taxInvoiceId)
                .from(taxInvoice)
                .where(taxInvoice.member.id.eq(member.getId())) // 사용자 기준으로 필터링
                .orderBy(taxInvoice.createdAt.desc()) // 최신순 정렬
                .limit(MAX_SEARCH_LIMIT.getNum()) // 최신 100개만 선택
                .fetch();

        return Optional.ofNullable(
                queryFactory
                        .select(Wildcard.count)
                        .from(taxInvoice)
                        .where(whereClause.and(taxInvoice.taxInvoiceId.in(latestIds))) // 최신 100개 내에서 필터링
                        .fetchOne()
        ).orElse(0L);
    }

    //임시저장 상태가 INITIAL인건 모두 Untemp로 바꾸기
    @Override
    @Transactional
    public void updateInitialToUntemp(List<Long> taxInvoiceIds) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        queryFactory
                .update(taxInvoice)
                .set(taxInvoice.isTemporary, UNTEMP) // UNTEMP로 변경
                .where(
                        taxInvoice.taxInvoiceId.in(taxInvoiceIds)
                                .and(taxInvoice.isTemporary.eq(INITIAL)) // INITIAL인 것만 변경
                )
                .execute();
    }

    @Override
    public void updateIsTemporaryToTemp(List<Long> taxInvoiceIds) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        queryFactory
                .update(taxInvoice)
                .set(taxInvoice.isTemporary, TEMP) // TEMP로 변경
                .where(taxInvoice.taxInvoiceId.in(taxInvoiceIds)) // 특정 ID들만 업데이트
                .execute();
    }

    @Override
    public Page<TaxInvoice> searchConsumerOrProvider(String poc, String employeeId, ProcessStatus processStatus, MemberEntity member, Pageable pageable) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        QMemberEntity memberEntity = QMemberEntity.memberEntity;
        QTaxInvoiceFile taxInvoiceFile = QTaxInvoiceFile.taxInvoiceFile;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(taxInvoice.member.id.eq(member.getId()));

        // 공급자 또는 공급받는자 검색 조건
        if (poc != null && !poc.isEmpty()) {
            BooleanBuilder supplierCondition = new BooleanBuilder();

            supplierCondition.or(taxInvoice.ipName.eq(poc).and(taxInvoice.suName.eq(poc))); // 둘 다 같은 경우
            supplierCondition.or(taxInvoice.ipName.eq(poc));
            supplierCondition.or(taxInvoice.suName.eq(poc));

            whereClause.and(supplierCondition);
        }

        //처리여부 별 검색 조건
        if(processStatus != null){
            whereClause.and(taxInvoice.processStatus.eq(processStatus));
        }

        //임시저장 여부 조건 추가
        whereClause.and(taxInvoice.isTemporary.eq(UNTEMP).not());

        int maxLimit = MAX_SEARCH_LIMIT.getNum();
        int pageSize = Math.min(pageable.getPageSize(), maxLimit);

        long total = Optional.ofNullable(
                queryFactory
                        .select(Wildcard.count)
                        .from(taxInvoice)
                        .where(whereClause)
                        .fetchOne()
        ).orElse(0L);

        // ✅ 전체 개수가 100개 초과하면, 100개까지만 표시
        if (total > maxLimit) {
            total = maxLimit;
        }


        List<TaxInvoice> results = queryFactory
                .selectFrom(taxInvoice)
                .leftJoin(taxInvoice.member, memberEntity).fetchJoin()
                .leftJoin(taxInvoice.file, taxInvoiceFile).fetchJoin()
                .where(whereClause)
                .orderBy(taxInvoice.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }


    @Override
    public List<TaxInvoice> findAllById(List<Long> taxInvoiceIdList) {
        return taxInvoiceJpaRepository.findAllById(taxInvoiceIdList);
    }

    @Override
    public void deleteAll(List<TaxInvoice> taxInvoices) {
        taxInvoiceJpaRepository.deleteAll(taxInvoices);
    }

    @Override
    public TaxInvoice findByIssueId(String issueId) {
        return taxInvoiceJpaRepository.findByIssueId(issueId);
    }
}
