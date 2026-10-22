package org.opensac.wicitapi.participants;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensac.wicitapi.DatabaseTest;
import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PeriodFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensac.wicitapi.jooq.Tables.PARTICIPANTS_BY_CATEGORY;

@DatabaseTest
@Import(ParticipantRepository.class)
class ParticipantRepositoryTest {

    private static final PageRequest FIRST_PAGE = new PageRequest(0, 100);

    @Autowired
    private DSLContext dsl;

    @Autowired
    private ParticipantRepository repository;

    @BeforeEach
    void setUp() {
        insert(34, "Sacramento", 2018, 2, "Infant", 4000);
        insert(34, "Sacramento", 2018, null, "Infant", 9000);
        insert(34, "Sacramento", 2018, 1, "Infant", null);
        insert(0, "Statewide", 2018, 1, "Infant", 200000);
    }

    @Test
    void ordersAnnualRowsBeforeMonths() {
        var rows = repository.findByCategory(new PeriodFilter("sacramento", 2018, null, null), null, FIRST_PAGE);

        assertThat(rows).extracting(ParticipantCountResponse::month).containsExactly(null, 1, 2);
    }

    @Test
    void filtersAnnualAndMonthlyRows() {
        assertThat(repository.countByCategory(new PeriodFilter(null, null, null, true), null)).isEqualTo(1);
        assertThat(repository.countByCategory(new PeriodFilter(null, null, null, false), null)).isEqualTo(3);
        assertThat(repository.countByCategory(new PeriodFilter(null, 2018, 1, null), "INFANT")).isEqualTo(2);
    }

    @Test
    void keepsSuppressedCountsAsNull() {
        var rows = repository.findByCategory(new PeriodFilter("Sacramento", 2018, 1, null), null, FIRST_PAGE);

        assertThat(rows).singleElement().satisfies(row -> {
            assertThat(row.certifiedParticipants()).isNull();
            assertThat(row.group()).isEqualTo("Infant");
        });
    }

    private void insert(int countyCode, String county, int year, Integer month, String category, Integer certified) {
        dsl.insertInto(PARTICIPANTS_BY_CATEGORY)
                .set(PARTICIPANTS_BY_CATEGORY.COUNTY_CODE, (short) countyCode)
                .set(PARTICIPANTS_BY_CATEGORY.COUNTY, county)
                .set(PARTICIPANTS_BY_CATEGORY.YEAR, (short) year)
                .set(PARTICIPANTS_BY_CATEGORY.MONTH, month == null ? null : month.shortValue())
                .set(PARTICIPANTS_BY_CATEGORY.CATEGORY, category)
                .set(PARTICIPANTS_BY_CATEGORY.CERTIFIED_PARTICIPANTS, certified)
                .execute();
    }
}
