package org.opensac.wicitapi.participants;

import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PageResponse;
import org.opensac.wicitapi.common.PeriodFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Queries the WIC Participants by County reports.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantService {

    private final ParticipantRepository repository;

    /**
     * Creates the service.
     *
     * @param repository the participant repository
     */
    public ParticipantService(ParticipantRepository repository) {
        this.repository = repository;
    }

    /**
     * Report 1: participants by county of residence and WIC category.
     *
     * @param filter   county and period filter
     * @param category WIC category, such as {@code Infant}, or null for all
     * @param page     zero-based page number
     * @param size     page size
     * @return a page of counts ordered by period and county
     */
    public PageResponse<ParticipantCountResponse> byCategory(PeriodFilter filter, String category, int page, int size) {
        var rows = repository.findByCategory(filter, category, new PageRequest(page, size));
        return PageResponse.of(rows, page, size, repository.countByCategory(filter, category));
    }

    /**
     * Report 2: participants by county of residence and race/ethnicity.
     *
     * @param filter        county and period filter
     * @param raceEthnicity race/ethnicity, such as {@code Hispanic}, or null for all
     * @param page          zero-based page number
     * @param size          page size
     * @return a page of counts ordered by period and county
     */
    public PageResponse<ParticipantCountResponse> byRaceEthnicity(PeriodFilter filter, String raceEthnicity, int page,
                                                                  int size) {
        var rows = repository.findByRaceEthnicity(filter, raceEthnicity, new PageRequest(page, size));
        return PageResponse.of(rows, page, size, repository.countByRaceEthnicity(filter, raceEthnicity));
    }
}
