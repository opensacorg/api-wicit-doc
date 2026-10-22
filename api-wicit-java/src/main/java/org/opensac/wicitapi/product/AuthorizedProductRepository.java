package org.opensac.wicitapi.product;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.opensac.wicitapi.common.Conditions;
import org.opensac.wicitapi.common.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

import static org.jooq.impl.DSL.noCondition;
import static org.opensac.wicitapi.jooq.Tables.AUTHORIZED_PRODUCT;

/**
 * Queries the {@code authorized_product} table.
 */
@Repository
public class AuthorizedProductRepository {

    /**
     * Escapes {@code %} and {@code _} in search words so they match literally.
     */
    private static final char ESCAPE = '!';

    private final DSLContext dsl;

    /**
     * Creates the repository.
     *
     * @param dsl the jOOQ context
     */
    public AuthorizedProductRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Every word in the query must appear, ignoring case, in one of the searched columns. Wildcards in the
     * query match literally.
     */
    private static Condition condition(ProductSearch search) {
        Condition condition = Conditions.equalsIgnoreCase(AUTHORIZED_PRODUCT.CODE_TYPE, search.codeType())
                .and(Conditions.equalsIgnoreCase(AUTHORIZED_PRODUCT.CATEGORY_CODE, search.categoryCode()));
        if (search.query() != null) {
            for (String word : search.query().toLowerCase(Locale.ROOT).split("\\s+")) {
                if (!word.isEmpty()) {
                    condition = condition.and(containsWord(word));
                }
            }
        }
        return condition;
    }

    private static Condition containsWord(String word) {
        String pattern = "%" + DSL.escape(word, ESCAPE) + "%";
        return noCondition()
                .or(AUTHORIZED_PRODUCT.CODE.likeIgnoreCase(pattern, ESCAPE))
                .or(AUTHORIZED_PRODUCT.DESCRIPTION.likeIgnoreCase(pattern, ESCAPE))
                .or(AUTHORIZED_PRODUCT.CATEGORY_DESCRIPTION.likeIgnoreCase(pattern, ESCAPE))
                .or(AUTHORIZED_PRODUCT.SUBCATEGORY_DESCRIPTION.likeIgnoreCase(pattern, ESCAPE))
                .or(AUTHORIZED_PRODUCT.FRUIT_VEGETABLE_TYPE.likeIgnoreCase(pattern, ESCAPE));
    }

    /**
     * Finds products, ordered by description.
     *
     * @param search  the search filters
     * @param request the page to return
     * @return the products on the page
     */
    public List<ProductResponse> find(ProductSearch search, PageRequest request) {
        return dsl.selectFrom(AUTHORIZED_PRODUCT)
                .where(condition(search))
                .orderBy(AUTHORIZED_PRODUCT.DESCRIPTION, AUTHORIZED_PRODUCT.CODE)
                .limit(request.size())
                .offset(request.offset())
                .fetch(ProductResponse::from);
    }

    /**
     * Counts products.
     *
     * @param search the search filters
     * @return the number of matching products
     */
    public int count(ProductSearch search) {
        return dsl.fetchCount(AUTHORIZED_PRODUCT, condition(search));
    }

    /**
     * Finds products by exact code. A code can be both a UPC and a PLU, so this can return two products.
     *
     * @param code the UPC or PLU
     * @return the matching products
     */
    public List<ProductResponse> findByCode(String code) {
        return dsl.selectFrom(AUTHORIZED_PRODUCT)
                .where(AUTHORIZED_PRODUCT.CODE.eq(code))
                .orderBy(AUTHORIZED_PRODUCT.CODE_TYPE)
                .fetch(ProductResponse::from);
    }

    /**
     * Counts products in each category.
     *
     * @return one row per category, ordered by category code
     */
    public List<ProductCategoryResponse> findCategories() {
        return dsl.select(AUTHORIZED_PRODUCT.CATEGORY_CODE, AUTHORIZED_PRODUCT.CATEGORY_DESCRIPTION, DSL.count())
                .from(AUTHORIZED_PRODUCT)
                .groupBy(AUTHORIZED_PRODUCT.CATEGORY_CODE, AUTHORIZED_PRODUCT.CATEGORY_DESCRIPTION)
                .orderBy(AUTHORIZED_PRODUCT.CATEGORY_CODE)
                .fetch(row -> new ProductCategoryResponse(row.value1(), row.value2(), row.value3()));
    }
}
