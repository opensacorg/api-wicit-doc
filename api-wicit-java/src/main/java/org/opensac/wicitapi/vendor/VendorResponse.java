package org.opensac.wicitapi.vendor;

import org.opensac.wicitapi.jooq.tables.records.VendorRecord;

/**
 * A WIC authorized vendor as returned by the API.
 *
 * @param id            the vendor id in this API
 * @param name          the store name
 * @param address       the street address
 * @param secondAddress the second address line, if any
 * @param city          the city
 * @param zip           the ZIP code
 * @param county        the county
 * @param latitude      the latitude, or null when the dataset has no location
 * @param longitude     the longitude, or null when the dataset has no location
 */
public record VendorResponse(Long id, String name, String address, String secondAddress, String city, String zip,
                             String county, Double latitude, Double longitude) {

    static VendorResponse from(VendorRecord vendor) {
        return new VendorResponse(vendor.getId(), vendor.getName(), vendor.getAddress(), vendor.getSecondAddress(),
                vendor.getCity(), vendor.getZip(), vendor.getCounty(), vendor.getLatitude(), vendor.getLongitude());
    }
}
