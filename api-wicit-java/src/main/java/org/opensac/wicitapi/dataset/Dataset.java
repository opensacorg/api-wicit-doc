package org.opensac.wicitapi.dataset;

import java.util.Arrays;
import java.util.Optional;

/**
 * The published CDPH WIC datasets bundled in src/main/resources. Each id is the dataset's name on
 * data.chhs.ca.gov and the classpath folder that holds its downloaded files.
 */
public enum Dataset {

    /**
     * WIC Participants by County.
     */
    PARTICIPANTS("cdph-wic-participants-by-county", "WIC Participants by County"),

    /**
     * California Women, Infants and Children Program Redemption by County.
     */
    REDEMPTIONS("california-women-infants-and-children-program-redemption-by-county",
            "California Women, Infants and Children Program Redemption by County"),

    /**
     * California WIC Authorized Product List.
     */
    AUTHORIZED_PRODUCTS("wic-authorized-product-list", "California WIC Authorized Product List"),

    /**
     * WIC Enabled Farmers and Authorized Farmers Markets.
     */
    FARMERS_MARKETS("wic-farmers-markets", "WIC Enabled Farmers and Authorized Farmers Markets"),

    /**
     * Women, Infants and Children (WIC) Authorized Vendors.
     */
    AUTHORIZED_VENDORS("women-infants-and-children-wic-authorized-vendors",
            "Women, Infants and Children (WIC) Authorized Vendors");

    private final String id;
    private final String title;

    Dataset(String id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Finds a dataset by id.
     *
     * @param id the dataset id
     * @return the dataset, or empty when the id is unknown
     */
    public static Optional<Dataset> fromId(String id) {
        return Arrays.stream(values()).filter(dataset -> dataset.id.equals(id)).findFirst();
    }

    /**
     * The dataset name on data.chhs.ca.gov, also the classpath folder of its files.
     *
     * @return the id
     */
    public String id() {
        return id;
    }

    /**
     * The dataset title on data.chhs.ca.gov.
     *
     * @return the title
     */
    public String title() {
        return title;
    }

    /**
     * The dataset's page on data.chhs.ca.gov.
     *
     * @return the URL
     */
    public String sourceUrl() {
        return "https://data.chhs.ca.gov/dataset/" + id;
    }
}
