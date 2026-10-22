package org.opensac.wicitapi.product;

import org.opensac.wicitapi.jooq.tables.records.AuthorizedProductRecord;

/**
 * An item on the California WIC Authorized Product List.
 *
 * @param codeType               {@code UPC} or {@code PLU}
 * @param code                   the UPC or PLU as published, including leading zeros
 * @param categoryCode           the category code
 * @param categoryDescription    the category name
 * @param subcategoryCode        the subcategory code
 * @param subcategoryDescription the subcategory name
 * @param description            the product description and size, as abbreviated in the published list
 * @param unitOfMeasure          the benefit unit, such as {@code OZ} or {@code GAL}; {@code $$$} for cash value benefit items
 * @param fruitVegetableType     for fruit and vegetable UPCs, whether canned, frozen, dried or fresh
 */
public record ProductResponse(String codeType, String code, String categoryCode, String categoryDescription,
                              String subcategoryCode, String subcategoryDescription, String description,
                              String unitOfMeasure,
                              String fruitVegetableType) {

    static ProductResponse from(AuthorizedProductRecord product) {
        return new ProductResponse(product.getCodeType(), product.getCode(), product.getCategoryCode(),
                product.getCategoryDescription(), product.getSubcategoryCode(), product.getSubcategoryDescription(),
                product.getDescription(), product.getUnitOfMeasure(), product.getFruitVegetableType());
    }
}
