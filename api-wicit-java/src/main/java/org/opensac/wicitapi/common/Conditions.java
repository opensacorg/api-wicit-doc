package org.opensac.wicitapi.common;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

/**
 * jOOQ conditions for optional request filters. A filter that was not given adds no condition.
 */
public final class Conditions {

    private Conditions() {
    }

    /**
     * Matches a text column to a value, ignoring case.
     *
     * @param field the column
     * @param value the value to match, or null or blank for no condition
     * @return the condition
     */
    public static Condition equalsIgnoreCase(Field<String> field, String value) {
        return value == null || value.isBlank() ? DSL.noCondition() : field.equalIgnoreCase(value.strip());
    }
}
