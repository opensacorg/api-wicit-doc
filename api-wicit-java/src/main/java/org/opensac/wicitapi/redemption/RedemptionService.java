package org.opensac.wicitapi.redemption;

import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PageResponse;
import org.opensac.wicitapi.common.PeriodFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Queries the WIC Program Redemption by County dataset.
 */
@Service
@Transactional(readOnly = true)
public class RedemptionService {

    private final RedemptionRepository repository;

    /**
     * Creates the service.
     *
     * @param repository the redemption repository
     */
    public RedemptionService(RedemptionRepository repository) {
        this.repository = repository;
    }

    /**
     * Redemptions with family counts, 2010 onward.
     *
     * @param filter vendor county and period filter
     * @param page   zero-based page number
     * @param size   page size
     * @return a page of redemptions ordered by period and county code
     */
    public PageResponse<FamilyRedemptionResponse> byFamily(PeriodFilter filter, int page, int size) {
        var rows = repository.findByFamily(filter, new PageRequest(page, size));
        return PageResponse.of(rows, page, size, repository.countByFamily(filter));
    }

    /**
     * Redemptions by participant category, 2010-2018.
     *
     * @param filter              vendor county and period filter
     * @param participantCategory participant category, such as {@code Infant}, or null for all
     * @param page                zero-based page number
     * @param size                page size
     * @return a page of redemptions ordered by period, county and category
     */
    public PageResponse<CategoryRedemptionResponse> byParticipantCategory(PeriodFilter filter,
                                                                          String participantCategory, int page,
                                                                          int size) {
        var rows = repository.findByParticipantCategory(filter, participantCategory, new PageRequest(page, size));
        return PageResponse.of(rows, page, size, repository.countByParticipantCategory(filter, participantCategory));
    }
}
