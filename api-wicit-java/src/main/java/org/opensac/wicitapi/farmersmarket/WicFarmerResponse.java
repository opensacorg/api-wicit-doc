package org.opensac.wicitapi.farmersmarket;

import org.opensac.wicitapi.jooq.tables.records.WicFarmerRecord;

/**
 * A WIC enabled farmer.
 *
 * @param county             the county
 * @param farmName           the farm name
 * @param certifiedProducer1 the first certified producer
 * @param certifiedProducer2 the second certified producer, if any
 */
public record WicFarmerResponse(String county, String farmName, String certifiedProducer1, String certifiedProducer2) {

    static WicFarmerResponse from(WicFarmerRecord farmer) {
        return new WicFarmerResponse(farmer.getCounty(), farmer.getFarmName(), farmer.getCertifiedProducer_1(),
                farmer.getCertifiedProducer_2());
    }
}
