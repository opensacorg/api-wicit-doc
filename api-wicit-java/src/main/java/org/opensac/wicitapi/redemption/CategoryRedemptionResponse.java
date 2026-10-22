package org.opensac.wicitapi.redemption;

import org.opensac.wicitapi.jooq.tables.records.RedemptionByParticipantCategoryRecord;

import java.math.BigDecimal;

/**
 * Food instrument redemptions in one vendor county, period and participant category (2010-2018).
 * Values are null when suppressed.
 *
 * @param vendorCounty                 the county where benefits were redeemed, or {@code Statewide}
 * @param participantCategory          the participant category, or {@code All} for the statewide annual total
 * @param year                         the calendar year
 * @param month                        the month, or null when the row covers the whole year
 * @param participantsRedeemed         unique participants redeeming food instruments
 * @param vouchersRedeemed             food instruments (vouchers) redeemed
 * @param totalCost                    dollar amount of food instruments redeemed
 * @param averageCost                  average cost per participant
 * @param statewideInfantFormulaRebate infant formula rebate (Statewide rows only)
 * @param totalCostAdjusted            total cost after the rebate (Statewide rows only)
 * @param averageCostAdjusted          average cost after the rebate (Statewide rows only)
 * @param stateAverageCostAdjusted     statewide average cost after the rebate
 */
public record CategoryRedemptionResponse(String vendorCounty, String participantCategory, int year, Integer month,
                                         Integer participantsRedeemed, Integer vouchersRedeemed, BigDecimal totalCost,
                                         BigDecimal averageCost,
                                         BigDecimal statewideInfantFormulaRebate, BigDecimal totalCostAdjusted,
                                         BigDecimal averageCostAdjusted,
                                         BigDecimal stateAverageCostAdjusted) {

    static CategoryRedemptionResponse from(RedemptionByParticipantCategoryRecord row) {
        return new CategoryRedemptionResponse(row.getVendorCounty(), row.getParticipantCategory(), row.getYear(),
                row.getMonth() == null ? null : row.getMonth().intValue(), row.getParticipantsRedeemed(),
                row.getVouchersRedeemed(), row.getTotalCost(), row.getAverageCost(),
                row.getStatewideInfantFormulaRebate(), row.getTotalCostAdjusted(), row.getAverageCostAdjusted(),
                row.getStateAverageCostAdjusted());
    }
}
