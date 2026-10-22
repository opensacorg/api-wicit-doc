package org.opensac.wicitapi.farmersmarket;

import org.jooq.DSLContext;
import org.opensac.wicitapi.common.Conditions;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opensac.wicitapi.jooq.Tables.FARMERS_MARKET;
import static org.opensac.wicitapi.jooq.Tables.WIC_FARMER;

/**
 * Queries the {@code farmers_market} and {@code wic_farmer} tables.
 */
@Repository
public class FarmersMarketRepository {

    private final DSLContext dsl;

    /**
     * Creates the repository.
     *
     * @param dsl the jOOQ context
     */
    public FarmersMarketRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Finds farmers' market days.
     *
     * @param county county name, or null for all
     * @param city   city name, or null for all
     * @return the market days ordered by county, market and day
     */
    public List<FarmersMarketResponse> findMarkets(String county, String city) {
        return dsl.selectFrom(FARMERS_MARKET)
                .where(Conditions.equalsIgnoreCase(FARMERS_MARKET.COUNTY, county))
                .and(Conditions.equalsIgnoreCase(FARMERS_MARKET.CITY, city))
                .orderBy(FARMERS_MARKET.COUNTY, FARMERS_MARKET.MARKET_NAME, FARMERS_MARKET.DAY_OF_WEEK,
                        FARMERS_MARKET.ID)
                .fetch(FarmersMarketResponse::from);
    }

    /**
     * Finds WIC enabled farmers.
     *
     * @param county county name, or null for all
     * @return the farmers ordered by county and farm name
     */
    public List<WicFarmerResponse> findFarmers(String county) {
        return dsl.selectFrom(WIC_FARMER)
                .where(Conditions.equalsIgnoreCase(WIC_FARMER.COUNTY, county))
                .orderBy(WIC_FARMER.COUNTY, WIC_FARMER.FARM_NAME)
                .fetch(WicFarmerResponse::from);
    }
}
