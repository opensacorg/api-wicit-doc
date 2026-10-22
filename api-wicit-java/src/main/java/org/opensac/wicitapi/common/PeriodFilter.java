package org.opensac.wicitapi.common;

import org.jooq.Condition;
import org.jooq.Field;

/**
 * Filters shared by the county statistics tables, which store one row per county and period.
 * A period is a whole year (month is null) or a single month.
 *
 * @param county county name, such as {@code Sacramento} or {@code Statewide}; matched without regard to case
 * @param year   calendar year
 * @param month  month of the year, 1 to 12
 * @param annual {@code true} for annual rows only, {@code false} for monthly rows only, {@code null} for both
 */
public record PeriodFilter(String county, Integer year, Integer month, Boolean annual) {

    /**
     * Builds the condition that applies this filter to a table.
     *
     * @param countyField the column that holds the county name
     * @param yearField   the year column
     * @param monthField  the month column, null on annual rows
     * @return the condition
     */
    public Condition toCondition(Field<String> countyField, Field<Short> yearField, Field<Short> monthField) {
        Condition condition = Conditions.equalsIgnoreCase(countyField, county);
        if (year != null) {
            condition = condition.and(yearField.eq(year.shortValue()));
        }
        if (month != null) {
            condition = condition.and(monthField.eq(month.shortValue()));
        }
        if (annual != null) {
            condition = condition.and(annual ? monthField.isNull() : monthField.isNotNull());
        }
        return condition;
    }
}
