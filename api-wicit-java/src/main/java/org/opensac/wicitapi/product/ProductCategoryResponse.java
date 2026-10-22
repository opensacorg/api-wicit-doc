package org.opensac.wicitapi.product;

/**
 * A food category on the Authorized Product List.
 *
 * @param code         the category code, such as {@code 19}
 * @param description  the category name, such as {@code Fruit and Vegetables Cash Value Benefit}
 * @param productCount the number of UPC and PLU items in the category
 */
public record ProductCategoryResponse(String code, String description, long productCount) {
}
