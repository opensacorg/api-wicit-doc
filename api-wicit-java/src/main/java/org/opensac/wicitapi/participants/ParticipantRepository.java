package org.opensac.wicitapi.participants;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.opensac.wicitapi.common.Conditions;
import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PeriodFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opensac.wicitapi.jooq.Tables.PARTICIPANTS_BY_CATEGORY;
import static org.opensac.wicitapi.jooq.Tables.PARTICIPANTS_BY_RACE_ETHNICITY;

/**
 * Queries the {@code participants_by_category} and {@code participants_by_race_ethnicity} tables.
 */
@Repository
public class ParticipantRepository {

    private final DSLContext dsl;

    /**
     * Creates the repository.
     *
     * @param dsl the jOOQ context
     */
    public ParticipantRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static Condition byCategory(PeriodFilter filter, String category) {
        var t = PARTICIPANTS_BY_CATEGORY;
        return filter.toCondition(t.COUNTY, t.YEAR, t.MONTH).and(Conditions.equalsIgnoreCase(t.CATEGORY, category));
    }

    private static Condition byRaceEthnicity(PeriodFilter filter, String raceEthnicity) {
        var t = PARTICIPANTS_BY_RACE_ETHNICITY;
        return filter.toCondition(t.COUNTY, t.YEAR, t.MONTH)
                .and(Conditions.equalsIgnoreCase(t.RACE_ETHNICITY, raceEthnicity));
    }

    /**
     * Finds report 1 rows, ordered by period, county code and category.
     *
     * @param filter   county and period filter
     * @param category WIC category, or null for all
     * @param request  the page to return
     * @return the rows on the page
     */
    public List<ParticipantCountResponse> findByCategory(PeriodFilter filter, String category, PageRequest request) {
        var t = PARTICIPANTS_BY_CATEGORY;
        return dsl.selectFrom(t)
                .where(byCategory(filter, category))
                .orderBy(t.YEAR, t.MONTH.asc().nullsFirst(), t.COUNTY_CODE, t.CATEGORY)
                .limit(request.size())
                .offset(request.offset())
                .fetch(ParticipantCountResponse::from);
    }

    /**
     * Counts report 1 rows.
     *
     * @param filter   county and period filter
     * @param category WIC category, or null for all
     * @return the number of rows
     */
    public int countByCategory(PeriodFilter filter, String category) {
        return dsl.fetchCount(PARTICIPANTS_BY_CATEGORY, byCategory(filter, category));
    }

    /**
     * Finds report 2 rows, ordered by period, county code and race/ethnicity.
     *
     * @param filter        county and period filter
     * @param raceEthnicity race/ethnicity, or null for all
     * @param request       the page to return
     * @return the rows on the page
     */
    public List<ParticipantCountResponse> findByRaceEthnicity(PeriodFilter filter, String raceEthnicity,
                                                              PageRequest request) {
        var t = PARTICIPANTS_BY_RACE_ETHNICITY;
        return dsl.selectFrom(t)
                .where(byRaceEthnicity(filter, raceEthnicity))
                .orderBy(t.YEAR, t.MONTH.asc().nullsFirst(), t.COUNTY_CODE, t.RACE_ETHNICITY)
                .limit(request.size())
                .offset(request.offset())
                .fetch(ParticipantCountResponse::from);
    }

    /**
     * Counts report 2 rows.
     *
     * @param filter        county and period filter
     * @param raceEthnicity race/ethnicity, or null for all
     * @return the number of rows
     */
    public int countByRaceEthnicity(PeriodFilter filter, String raceEthnicity) {
        return dsl.fetchCount(PARTICIPANTS_BY_RACE_ETHNICITY, byRaceEthnicity(filter, raceEthnicity));
    }
}
