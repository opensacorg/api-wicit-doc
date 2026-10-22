package org.opensac.wicitapi.vendor;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensac.wicitapi.DatabaseTest;
import org.opensac.wicitapi.common.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensac.wicitapi.jooq.Tables.VENDOR;

@DatabaseTest
@Import(VendorRepository.class)
class VendorRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private VendorRepository repository;

    @BeforeEach
    void setUp() {
        insert("FOODMAXX #474", "Oroville", "Butte", 39.50231, -121.5615);
        insert("BEL AIR #501", "Sacramento", "Sacramento", 38.52554, -121.432);
        insert("ARDEN FAIR MARKET", "Sacramento", "Sacramento", 38.60, -121.42);
        insert("NO LOCATION MARKET", "Sacramento", "Sacramento", null, null);
    }

    @Test
    void findsOnlyVendorsInsideTheBoxOrderedByName() {
        var vendors = repository.findInBounds(38.4, 38.7, -121.6, -121.3, 10);

        assertThat(vendors).extracting(VendorResponse::name).containsExactly("ARDEN FAIR MARKET", "BEL AIR #501");
    }

    @Test
    void appliesTheLimit() {
        assertThat(repository.findInBounds(30, 45, -125, -115, 1)).hasSize(1);
    }

    @Test
    void filtersAndPagesByCountyIgnoringCase() {
        assertThat(repository.count("sacramento", null)).isEqualTo(3);
        assertThat(repository.find("SACRAMENTO", null, new PageRequest(1, 2)))
                .extracting(VendorResponse::name).containsExactly("NO LOCATION MARKET");
    }

    private void insert(String name, String city, String county, Double latitude, Double longitude) {
        dsl.insertInto(VENDOR)
                .set(VENDOR.NAME, name)
                .set(VENDOR.ADDRESS, "1 MAIN ST")
                .set(VENDOR.CITY, city)
                .set(VENDOR.COUNTY, county)
                .set(VENDOR.LATITUDE, latitude)
                .set(VENDOR.LONGITUDE, longitude)
                .execute();
    }
}
