package org.opensac.wicitapi.redemption;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.opensac.wicitapi.common.Conditions;
import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PeriodFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opensac.wicitapi.jooq.Tables.REDEMPTION_BY_FAMILY;
import static org.opensac.wicitapi.jooq.Tables.REDEMPTION_BY_PARTICIPANT_CATEGORY;

/**
 * Queries the {@code redemption_by_family} and {@code redemption_by_participant_category} tables.
 */
@Repository
public class RedemptionRepository {

    private final DSLContext dsl;

    /**
     * Creates the repository.
     *
     * @param dsl the jOOQ context
     */
    public RedemptionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static Condition byFamily(PeriodFilter filter) {
        var t = REDEMPTION_BY_FAMILY;
        return filter.toCondition(t.VENDOR_COUNTY, t.YEAR, t.MONTH);
    }

    private static Condition byParticipantCategory(PeriodFilter filter, String participantCategory) {
        var t = REDEMPTION_BY_PARTICIPANT_CATEGORY;
        return filter.toCondition(t.VENDOR_COUNTY, t.YEAR, t.MONTH)
                .and(Conditions.equalsIgnoreCase(t.PARTICIPANT_CATEGORY, participantCategory));
    }

    /**
     * Finds redemptions with family counts, ordered by period and county code.
     *
     * @param filter  vendor county and period filter
     * @param request the page to return
     * @return the rows on the page
     */
    public List<FamilyRedemptionResponse> findByFamily(PeriodFilter filter, PageRequest request) {
        var t = REDEMPTION_BY_FAMILY;
        return dsl.selectFrom(t)
                .where(byFamily(filter))
                .orderBy(t.YEAR, t.MONTH.asc().nullsFirst(), t.VENDOR_COUNTY_CODE)
                .limit(request.size())
                .offset(request.offset())
                .fetch(FamilyRedemptionResponse::from);
    }

    /**
     * Counts redemptions with family counts.
     *
     * @param filter vendor county and period filter
     * @return the number of rows
     */
    public int countByFamily(PeriodFilter filter) {
        return dsl.fetchCount(REDEMPTION_BY_FAMILY, byFamily(filter));
    }

    /**
     * Finds redemptions by participant category, ordered by period, county and category.
     *
     * @param filter              vendor county and period filter
     * @param participantCategory participant category, or null for all
     * @param request             the page to return
     * @return the rows on the page
     */
    public List<CategoryRedemptionResponse> findByParticipantCategory(PeriodFilter filter, String participantCategory,
                                                                      PageRequest request) {
        var t = REDEMPTION_BY_PARTICIPANT_CATEGORY;
        return dsl.selectFrom(t)
                .where(byParticipantCategory(filter, participantCategory))
                .orderBy(t.YEAR, t.MONTH.asc().nullsFirst(), t.VENDOR_COUNTY, t.PARTICIPANT_CATEGORY)
                .limit(request.size())
                .offset(request.offset())
                .fetch(CategoryRedemptionResponse::from);
    }

    /**
     * Counts redemptions by participant category.
     *
     * @param filter              vendor county and period filter
     * @param participantCategory participant category, or null for all
     * @return the number of rows
     */
    public int countByParticipantCategory(PeriodFilter filter, String participantCategory) {
        return dsl.fetchCount(REDEMPTION_BY_PARTICIPANT_CATEGORY, byParticipantCategory(filter, participantCategory));
    }
}
