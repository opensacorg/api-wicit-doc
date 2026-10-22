package org.opensac.wicitapi.vendor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.opensac.wicitapi.common.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * WIC authorized vendors: the stores shown on the WICit map.
 */
@RestController
@RequestMapping("/api/vendors")
@Tag(name = "Vendors", description = "Stores authorized to accept WIC benefits")
public class VendorController {

    private final VendorService vendorService;

    /**
     * Creates the controller.
     *
     * @param vendorService the vendor service
     */
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Finds vendors inside a map viewport.
     *
     * @param west  minimum longitude
     * @param south minimum latitude
     * @param east  maximum longitude
     * @param north maximum latitude
     * @param limit the maximum number of vendors
     * @return the vendors
     */
    @GetMapping("/in-bounds")
    @Operation(summary = "Vendors inside a map viewport",
            description = "Returns vendors whose location is inside the bounding box, ordered by name. "
                    + "Vendors without a location are not included.")
    public List<VendorResponse> inBounds(
            @RequestParam @DecimalMin("-180") @DecimalMax("180") double west,
            @RequestParam @DecimalMin("-90") @DecimalMax("90") double south,
            @RequestParam @DecimalMin("-180") @DecimalMax("180") double east,
            @RequestParam @DecimalMin("-90") @DecimalMax("90") double north,
            @Parameter(description = "Maximum vendors to return (at most 1000)")
            @RequestParam(defaultValue = "500") @Min(1) @Max(VendorService.MAX_IN_BOUNDS) int limit) {
        return vendorService.findInBounds(new VendorService.Bounds(west, south, east, north), limit);
    }

    /**
     * Lists vendors.
     *
     * @param county county name
     * @param city   city name
     * @param page   zero-based page number
     * @param size   page size
     * @return a page of vendors
     */
    @GetMapping
    @Operation(summary = "List vendors", description = "Lists vendors by name, optionally in one county or city.")
    public PageResponse<VendorResponse> list(
            @Parameter(description = "County name, such as Sacramento") @RequestParam(required = false) String county,
            @Parameter(description = "City name, such as Elk Grove") @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(1000) int size) {
        return vendorService.list(county, city, page, size);
    }
}
