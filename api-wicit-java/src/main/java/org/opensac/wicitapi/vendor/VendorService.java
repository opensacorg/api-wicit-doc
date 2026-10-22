package org.opensac.wicitapi.vendor;

import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Looks up WIC authorized vendors.
 */
@Service
@Transactional(readOnly = true)
public class VendorService {

    /**
     * The most vendors returned for one map viewport.
     */
    public static final int MAX_IN_BOUNDS = 1000;

    private final VendorRepository vendorRepository;

    /**
     * Creates the service.
     *
     * @param vendorRepository the vendor repository
     */
    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Finds vendors inside a map viewport.
     *
     * @param bounds the viewport
     * @param limit  the maximum number of vendors, capped at {@link #MAX_IN_BOUNDS}
     * @return the vendors, ordered by name
     */
    public List<VendorResponse> findInBounds(Bounds bounds, int limit) {
        return vendorRepository.findInBounds(bounds.south(), bounds.north(), bounds.west(), bounds.east(),
                Math.min(limit, MAX_IN_BOUNDS));
    }

    /**
     * Lists vendors, optionally in one county or city.
     *
     * @param county county name, or null for all
     * @param city   city name, or null for all
     * @param page   zero-based page number
     * @param size   page size
     * @return a page of vendors ordered by name
     */
    public PageResponse<VendorResponse> list(String county, String city, int page, int size) {
        var vendors = vendorRepository.find(county, city, new PageRequest(page, size));
        return PageResponse.of(vendors, page, size, vendorRepository.count(county, city));
    }

    /**
     * A latitude and longitude bounding box.
     *
     * @param west  minimum longitude
     * @param south minimum latitude
     * @param east  maximum longitude
     * @param north maximum latitude
     */
    public record Bounds(double west, double south, double east, double north) {
    }
}
