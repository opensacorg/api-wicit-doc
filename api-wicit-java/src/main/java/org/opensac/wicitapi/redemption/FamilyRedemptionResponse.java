package org.opensac.wicitapi.redemption;

import org.opensac.wicitapi.jooq.tables.records.RedemptionByFamilyRecord;

import java.math.BigDecimal;

/**
 * Redemptions in one vendor county and period, with the number of families redeeming. Values are null
 * when suppressed or when the delivery system did not apply (no EBT before June 2019, no food
 * instruments after March 2020).
 *
 * @param part                         the source file: {@code A} (2010-2018), {@code B} (2019-2020) or {@code C} (2021 onward)
 * @param vendorCountyCode             the county code; 0 is Statewide
 * @param vendorCounty                 the county where benefits were redeemed, or {@code Statewide}
 * @param year                         the calendar year (obligation year)
 * @param month                        the month, or null when the row covers the whole year
 * @param familiesRedeemed             unique families redeeming benefits
 * @param foodInstrumentsRedeemed      paper food instruments redeemed
 * @param foodInstrumentDollars        dollar amount of food instruments redeemed
 * @param ebtTransactions              EBT transactions
 * @param ebtDollars                   dollar amount of EBT transactions
 * @param averageCostPerFamily         average cost per family
 * @param statewideInfantFormulaRebate infant formula rebate (Statewide rows only)
 * @param totalCostAdjusted            total cost after the rebate (Statewide rows only)
 * @param averageCostAdjusted          average cost per family after the rebate (Statewide rows only)
 */
public record FamilyRedemptionResponse(String part, int vendorCountyCode, String vendorCounty, int year, Integer month,
                                       Integer familiesRedeemed, Integer foodInstrumentsRedeemed,
                                       BigDecimal foodInstrumentDollars,
                                       Integer ebtTransactions, BigDecimal ebtDollars, BigDecimal averageCostPerFamily,
                                       BigDecimal statewideInfantFormulaRebate, BigDecimal totalCostAdjusted,
                                       BigDecimal averageCostAdjusted) {

    static FamilyRedemptionResponse from(RedemptionByFamilyRecord row) {
        return new FamilyRedemptionResponse(row.getPart(), row.getVendorCountyCode(), row.getVendorCounty(),
                row.getYear(), row.getMonth() == null ? null : row.getMonth().intValue(), row.getFamiliesRedeemed(),
                row.getFoodInstrumentsRedeemed(), row.getFoodInstrumentDollars(), row.getEbtTransactions(),
                row.getEbtDollars(), row.getAverageCostPerFamily(), row.getStatewideInfantFormulaRebate(),
                row.getTotalCostAdjusted(), row.getAverageCostAdjusted());
    }
}
