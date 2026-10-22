package org.opensac.wicitapi.vendor;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.opensac.wicitapi.common.Conditions;
import org.opensac.wicitapi.common.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opensac.wicitapi.jooq.Tables.VENDOR;

/**
 * Queries the {@code vendor} table.
 */
@Repository
public class VendorRepository {

    private final DSLContext dsl;

    /**
     * Creates the repository.
     *
     * @param dsl the jOOQ context
     */
    public VendorRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static Condition filter(String county, String city) {
        return Conditions.equalsIgnoreCase(VENDOR.COUNTY, county).and(Conditions.equalsIgnoreCase(VENDOR.CITY, city));
    }

    /**
     * Finds vendors inside a bounding box, ordered by name.
     *
     * @param south minimum latitude
     * @param north maximum latitude
     * @param west  minimum longitude
     * @param east  maximum longitude
     * @param limit the maximum number of vendors to return
     * @return the vendors
     */
    public List<VendorResponse> findInBounds(double south, double north, double west, double east, int limit) {
        return dsl.selectFrom(VENDOR)
                .where(VENDOR.LATITUDE.between(south, north))
                .and(VENDOR.LONGITUDE.between(west, east))
                .orderBy(VENDOR.NAME, VENDOR.ID)
                .limit(limit)
                .fetch(VendorResponse::from);
    }

    /**
     * Finds vendors, optionally in one county or city, ordered by name.
     *
     * @param county  county name, or null for all
     * @param city    city name, or null for all
     * @param request the page to return
     * @return the vendors on the page
     */
    public List<VendorResponse> find(String county, String city, PageRequest request) {
        return dsl.selectFrom(VENDOR)
                .where(filter(county, city))
                .orderBy(VENDOR.NAME, VENDOR.ID)
                .limit(request.size())
                .offset(request.offset())
                .fetch(VendorResponse::from);
    }

    /**
     * Counts vendors, optionally in one county or city.
     *
     * @param county county name, or null for all
     * @param city   city name, or null for all
     * @return the number of vendors
     */
    public int count(String county, String city) {
        return dsl.fetchCount(VENDOR, filter(county, city));
    }
}
