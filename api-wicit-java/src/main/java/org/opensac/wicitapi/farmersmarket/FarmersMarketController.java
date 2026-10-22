package org.opensac.wicitapi.farmersmarket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * WIC authorized farmers' markets and WIC enabled farmers.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Farmers' markets", description = "WIC enabled farmers and authorized farmers' markets")
public class FarmersMarketController {

    private final FarmersMarketService farmersMarketService;

    /**
     * Creates the controller.
     *
     * @param farmersMarketService the farmers' market service
     */
    public FarmersMarketController(FarmersMarketService farmersMarketService) {
        this.farmersMarketService = farmersMarketService;
    }

    /**
     * Lists farmers' market days.
     *
     * @param county county name
     * @param city   city name
     * @return the market days
     */
    @GetMapping("/farmers-markets")
    @Operation(summary = "WIC authorized farmers' markets", description = "One entry per market day.")
    public List<FarmersMarketResponse> markets(
            @Parameter(description = "County name, such as Alameda") @RequestParam(required = false) String county,
            @Parameter(description = "City name") @RequestParam(required = false) String city) {
        return farmersMarketService.markets(county, city);
    }

    /**
     * Lists WIC enabled farmers.
     *
     * @param county county name
     * @return the farmers
     */
    @GetMapping("/farmers")
    @Operation(summary = "WIC enabled farmers")
    public List<WicFarmerResponse> farmers(
            @Parameter(description = "County name, such as Fresno") @RequestParam(required = false) String county) {
        return farmersMarketService.farmers(county);
    }
}
