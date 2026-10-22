package org.opensac.wicitapi.redemption;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.opensac.wicitapi.common.PageResponse;
import org.opensac.wicitapi.common.PeriodFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * WIC benefit redemptions by the county of the vendor where they were redeemed.
 */
@RestController
@RequestMapping("/api/redemptions")
@Tag(name = "Redemptions", description = "WIC benefit redemptions by vendor county")
public class RedemptionController {

    private final RedemptionService redemptionService;

    /**
     * Creates the controller.
     *
     * @param redemptionService the redemption service
     */
    public RedemptionController(RedemptionService redemptionService) {
        this.redemptionService = redemptionService;
    }

    /**
     * Redemptions with family counts.
     *
     * @param county vendor county
     * @param year   calendar year
     * @param month  month
     * @param annual annual or monthly rows only
     * @param page   zero-based page number
     * @param size   page size
     * @return a page of redemptions
     */
    @GetMapping("/by-family")
    @Operation(summary = "Redemptions with family counts, 2010 onward",
            description = "Combines parts A, B and C of the dataset. Food instrument columns cover 2010 to March "
                    + "2020; EBT columns cover June 2019 onward.")
    public PageResponse<FamilyRedemptionResponse> byFamily(
            @Parameter(description = "Vendor county, or Statewide") @RequestParam(required = false) String county,
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @Parameter(description = "true for annual totals only, false for monthly rows only")
            @RequestParam(required = false) Boolean annual,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(1000) int size) {
        return redemptionService.byFamily(new PeriodFilter(county, year, month, annual), page, size);
    }

    /**
     * Redemptions by participant category.
     *
     * @param county   vendor county
     * @param year     calendar year
     * @param month    month
     * @param annual   annual or monthly rows only
     * @param category participant category
     * @param page     zero-based page number
     * @param size     page size
     * @return a page of redemptions
     */
    @GetMapping("/by-participant-category")
    @Operation(summary = "Redemptions by participant category, 2010-2018",
            description = "Categories are Breastfeeding Mother, Child, Infant, Non-Breastfeeding Mother and Prenatal, "
                    + "plus All for the statewide annual totals.")
    public PageResponse<CategoryRedemptionResponse> byParticipantCategory(
            @Parameter(description = "Vendor county, or Statewide") @RequestParam(required = false) String county,
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @Parameter(description = "true for annual totals only, false for monthly rows only")
            @RequestParam(required = false) Boolean annual,
            @Parameter(description = "Participant category, such as Infant") @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(1000) int size) {
        return redemptionService.byParticipantCategory(new PeriodFilter(county, year, month, annual), category, page,
                size);
    }
}
