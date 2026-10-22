package org.opensac.wicitapi.participants;

import org.opensac.wicitapi.jooq.tables.records.ParticipantsByCategoryRecord;
import org.opensac.wicitapi.jooq.tables.records.ParticipantsByRaceEthnicityRecord;

/**
 * Participant counts for one county, period and group.
 *
 * @param countyCode            the county code; 0 is Statewide
 * @param county                the county of residence, or {@code Statewide}
 * @param year                  the calendar year
 * @param month                 the month, or null when the row covers the whole year
 * @param group                 the WIC category or race/ethnicity
 * @param certifiedParticipants certified participants, or null when suppressed
 * @param issuedParticipants    participants issued benefits, or null when suppressed
 * @param redeemedParticipants  participants redeeming benefits; null when suppressed, and always null from
 *                              2019 because EBT redemptions are recorded per family
 */
public record ParticipantCountResponse(int countyCode, String county, int year, Integer month, String group,
                                       Integer certifiedParticipants, Integer issuedParticipants,
                                       Integer redeemedParticipants) {

    static ParticipantCountResponse from(ParticipantsByCategoryRecord row) {
        return new ParticipantCountResponse(row.getCountyCode(), row.getCounty(), row.getYear(),
                toInteger(row.getMonth()), row.getCategory(), row.getCertifiedParticipants(),
                row.getIssuedParticipants(), row.getRedeemedParticipants());
    }

    static ParticipantCountResponse from(ParticipantsByRaceEthnicityRecord row) {
        return new ParticipantCountResponse(row.getCountyCode(), row.getCounty(), row.getYear(),
                toInteger(row.getMonth()), row.getRaceEthnicity(), row.getCertifiedParticipants(),
                row.getIssuedParticipants(), row.getRedeemedParticipants());
    }

    private static Integer toInteger(Short value) {
        return value == null ? null : value.intValue();
    }
}
