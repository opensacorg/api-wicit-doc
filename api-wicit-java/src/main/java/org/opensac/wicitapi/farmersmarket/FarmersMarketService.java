package org.opensac.wicitapi.farmersmarket;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Looks up WIC authorized farmers' markets and WIC enabled farmers.
 */
@Service
@Transactional(readOnly = true)
public class FarmersMarketService {

    private final FarmersMarketRepository repository;

    /**
     * Creates the service.
     *
     * @param repository the farmers' market repository
     */
    public FarmersMarketService(FarmersMarketRepository repository) {
        this.repository = repository;
    }

    /**
     * Lists farmers' market days.
     *
     * @param county county name, or null for all
     * @param city   city name, or null for all
     * @return the market days ordered by county, market and day
     */
    public List<FarmersMarketResponse> markets(String county, String city) {
        return repository.findMarkets(county, city);
    }

    /**
     * Lists WIC enabled farmers.
     *
     * @param county county name, or null for all
     * @return the farmers ordered by county and farm name
     */
    public List<WicFarmerResponse> farmers(String county) {
        return repository.findFarmers(county);
    }
}
