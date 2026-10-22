package org.opensac.wicitapi.participants;

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
 * WIC participants by county of residence.
 */
@RestController
@RequestMapping("/api/participants")
@Tag(name = "Participants", description = "WIC participants by county of residence, 2010 onward")
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * Creates the controller.
     *
     * @param participantService the participant service
     */
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Report 1: participants by WIC category.
     *
     * @param county   county name
     * @param year     calendar year
     * @param month    month
     * @param annual   annual or monthly rows only
     * @param category WIC category
     * @param page     zero-based page number
     * @param size     page size
     * @return a page of counts
     */
    @GetMapping("/by-category")
    @Operation(summary = "Participants by WIC category",
            description = "Categories are Breastfeeding, Child Aged 1 to Child Aged 4, Infant, Non-Breastfeeding "
                    + "and Prenatal. Suppressed counts are null.")
    public PageResponse<ParticipantCountResponse> byCategory(
            @Parameter(description = "County of residence, or Statewide") @RequestParam(required = false) String county,
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @Parameter(description = "true for annual totals only, false for monthly rows only")
            @RequestParam(required = false) Boolean annual,
            @Parameter(description = "WIC category, such as Infant") @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(1000) int size) {
        return participantService.byCategory(new PeriodFilter(county, year, month, annual), category, page, size);
    }

    /**
     * Report 2: participants by race/ethnicity.
     *
     * @param county        county name
     * @param year          calendar year
     * @param month         month
     * @param annual        annual or monthly rows only
     * @param raceEthnicity race/ethnicity
     * @param page          zero-based page number
     * @param size          page size
     * @return a page of counts
     */
    @GetMapping("/by-race-ethnicity")
    @Operation(summary = "Participants by race/ethnicity",
            description = "Groups are Hispanic, Non-Hispanic African American, Non-Hispanic Asian, Non-Hispanic "
                    + "White and Non-Hispanic Other. Suppressed counts are null.")
    public PageResponse<ParticipantCountResponse> byRaceEthnicity(
            @Parameter(description = "County of residence, or Statewide") @RequestParam(required = false) String county,
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @Parameter(description = "true for annual totals only, false for monthly rows only")
            @RequestParam(required = false) Boolean annual,
            @Parameter(description = "Race/ethnicity, such as Hispanic")
            @RequestParam(name = "raceEthnicity", required = false) String raceEthnicity,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(1000) int size) {
        return participantService.byRaceEthnicity(new PeriodFilter(county, year, month, annual), raceEthnicity, page,
                size);
    }
}
