package org.opensac.wicitapi.product;

import org.opensac.wicitapi.common.PageRequest;
import org.opensac.wicitapi.common.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Searches the California WIC Authorized Product List.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final AuthorizedProductRepository productRepository;

    /**
     * Creates the service.
     *
     * @param productRepository the product repository
     */
    public ProductService(AuthorizedProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Searches products. Every word in the query must appear in the code, description, category,
     * subcategory or fruit and vegetable type.
     *
     * @param query        words to search for, or null for all products
     * @param codeType     {@code UPC} or {@code PLU}, or null for both
     * @param categoryCode a category code, or null for all categories
     * @param page         zero-based page number
     * @param size         page size
     * @return a page of products ordered by description
     */
    public PageResponse<ProductResponse> search(String query, String codeType, String categoryCode, int page, int size) {
        var search = new ProductSearch(query, codeType, categoryCode);
        var products = productRepository.find(search, new PageRequest(page, size));
        return PageResponse.of(products, page, size, productRepository.count(search));
    }

    /**
     * Finds products by exact UPC or PLU.
     *
     * @param code the code
     * @return the matching products, empty when the code is not authorized
     */
    public List<ProductResponse> findByCode(String code) {
        return productRepository.findByCode(code.strip());
    }

    /**
     * Lists the product categories.
     *
     * @return the categories with their product counts
     */
    public List<ProductCategoryResponse> categories() {
        return productRepository.findCategories();
    }
}
