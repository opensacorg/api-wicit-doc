package org.opensac.wicitapi.farmersmarket;

import org.opensac.wicitapi.jooq.tables.records.FarmersMarketRecord;

/**
 * One market day of a WIC authorized farmers' market.
 *
 * @param county     the county
 * @param marketName the market name
 * @param location   the street location
 * @param city       the city
 * @param dayOfWeek  the day the market is open, such as {@code Saturday}
 * @param hours      opening hours, such as {@code 09:00 AM - 01:00 PM}
 * @param season     when the market runs, such as {@code Year Round} or {@code May-Oct}
 */
public record FarmersMarketResponse(String county, String marketName, String location, String city,
                                    String dayOfWeek, String hours, String season) {

    static FarmersMarketResponse from(FarmersMarketRecord market) {
        return new FarmersMarketResponse(market.getCounty(), market.getMarketName(), market.getLocation(),
                market.getCity(), market.getDayOfWeek(), market.getHours(), market.getSeason());
    }
}
