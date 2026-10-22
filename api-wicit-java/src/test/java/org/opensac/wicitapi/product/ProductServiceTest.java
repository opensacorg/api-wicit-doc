package org.opensac.wicitapi.product;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensac.wicitapi.DatabaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensac.wicitapi.jooq.Tables.AUTHORIZED_PRODUCT;

/**
 * Runs the search queries against an in-memory database through Spring's jOOQ wiring.
 */
@DatabaseTest
@Import({ProductService.class, AuthorizedProductRepository.class})
class ProductServiceTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private ProductService service;

    @BeforeEach
    void setUp() {
        insert("UPC", "00054800425242", "16", "Bread and Whole Grains", "Brown Rice", "BensOrig-WGBrwnRice-2lbs");
        insert("UPC", "00042187402291", "16", "Bread and Whole Grains", "Brown Rice", "BstY LGBrwn Rice-16OZ");
        insert("UPC", "00046567002071", "19", "Fruit and Vegetables Cash Value Benefit",
                "Fruits and Vegetables (No Dried Fruits)", "Rlys Green Beans-14.5OZ");
        insert("PLU", "4448", "19", "Fruit and Vegetables Cash Value Benefit",
                "Fruits and Vegetables (No Dried Fruits)", "Tamarindo");
    }

    @Test
    void everyWordMustMatchSomeField() {
        var page = service.search("brown rice", null, null, 0, 25);

        assertThat(page.totalItems()).isEqualTo(2);
        assertThat(page.items()).extracting(ProductResponse::description)
                .containsExactly("BensOrig-WGBrwnRice-2lbs", "BstY LGBrwn Rice-16OZ");
    }

    @Test
    void matchesPartialCodes() {
        assertThat(service.search("46567002071", null, null, 0, 25).items())
                .extracting(ProductResponse::code).containsExactly("00046567002071");
    }

    @Test
    void filtersByCodeTypeAndCategory() {
        assertThat(service.search(null, "plu", null, 0, 25).items())
                .extracting(ProductResponse::code).containsExactly("4448");
        assertThat(service.search("", null, "16", 0, 25).totalItems()).isEqualTo(2);
    }

    @Test
    void treatsWildcardsLiterally() {
        assertThat(service.search("%", null, null, 0, 25).totalItems()).isZero();
        assertThat(service.search("_", null, null, 0, 25).totalItems()).isZero();
    }

    @Test
    void pagesResults() {
        var page = service.search(null, null, null, 1, 3);

        assertThat(page.items()).hasSize(1);
        assertThat(page.totalPages()).isEqualTo(2);
    }

    @Test
    void findsByExactCode() {
        assertThat(service.findByCode(" 4448 ")).extracting(ProductResponse::description).containsExactly("Tamarindo");
        assertThat(service.findByCode("444")).isEmpty();
    }

    @Test
    void countsCategories() {
        assertThat(service.categories()).containsExactly(
                new ProductCategoryResponse("16", "Bread and Whole Grains", 2),
                new ProductCategoryResponse("19", "Fruit and Vegetables Cash Value Benefit", 2));
    }

    private void insert(String codeType, String code, String category, String categoryDescription,
                        String subcategoryDescription, String description) {
        dsl.insertInto(AUTHORIZED_PRODUCT)
                .set(AUTHORIZED_PRODUCT.CODE_TYPE, codeType)
                .set(AUTHORIZED_PRODUCT.CODE, code)
                .set(AUTHORIZED_PRODUCT.CATEGORY_CODE, category)
                .set(AUTHORIZED_PRODUCT.CATEGORY_DESCRIPTION, categoryDescription)
                .set(AUTHORIZED_PRODUCT.SUBCATEGORY_DESCRIPTION, subcategoryDescription)
                .set(AUTHORIZED_PRODUCT.DESCRIPTION, description)
                .execute();
    }
}
