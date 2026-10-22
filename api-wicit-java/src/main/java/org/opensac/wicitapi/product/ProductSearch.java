package org.opensac.wicitapi.product;

/**
 * Filters for a product search.
 *
 * @param query        words that must all appear in the code, description, category, subcategory or fruit
 *                     and vegetable type, or null for all products
 * @param codeType     {@code UPC} or {@code PLU}, or null for both
 * @param categoryCode a category code, or null for all categories
 */
public record ProductSearch(String query, String codeType, String categoryCode) {
}
