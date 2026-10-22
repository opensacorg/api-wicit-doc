package org.opensac.wicitapi.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.opensac.wicitapi.common.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * The California WIC Authorized Product List, used by the "Which foods qualify?" search.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Foods on the California WIC Authorized Product List")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates the controller.
     *
     * @param productService the product service
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Searches products.
     *
     * @param q        words to search for
     * @param type     UPC or PLU
     * @param category category code
     * @param page     zero-based page number
     * @param size     page size
     * @return a page of products
     */
    @GetMapping
    @Operation(summary = "Search authorized products",
            description = "Every word in q must appear in the UPC or PLU, description, category, subcategory or "
                    + "fruit and vegetable type. Without q, lists all products.")
    public PageResponse<ProductResponse> search(
            @Parameter(description = "Words to search for, such as \"brown rice\" or a UPC")
            @RequestParam(required = false) @Size(max = 100) String q,
            @Parameter(description = "UPC or PLU") @RequestParam(required = false) @Pattern(regexp = "(?i)UPC|PLU") String type,
            @Parameter(description = "Category code, such as 19") @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "25") @Min(1) @Max(200) int size) {
        return productService.search(q, type, category, page, size);
    }

    /**
     * Lists categories.
     *
     * @return the categories
     */
    @GetMapping("/categories")
    @Operation(summary = "Product categories", description = "Each category with its number of products.")
    public List<ProductCategoryResponse> categories() {
        return productService.categories();
    }

    /**
     * Looks up a product by code.
     *
     * @param code the UPC or PLU
     * @return the matching products
     */
    @GetMapping("/{code}")
    @Operation(summary = "Look up a UPC or PLU",
            description = "Returns the products with this exact code, as published (UPCs have 14 digits with "
                    + "leading zeros). Responds 404 when the code is not on the list.")
    public List<ProductResponse> byCode(@PathVariable @Pattern(regexp = "\\d{1,20}") String code) {
        List<ProductResponse> products = productService.findByCode(code);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No authorized product has code " + code);
        }
        return products;
    }
}
