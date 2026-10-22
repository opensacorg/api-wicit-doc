package org.opensac.wicitapi.product;

import org.junit.jupiter.api.Test;
import org.opensac.wicitapi.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void searchReturnsAPage() {
        var product = new ProductResponse("PLU", "4448", "19", "Fruit and Vegetables Cash Value Benefit", "001",
                "Fruits and Vegetables (No Dried Fruits)", "Tamarindo", "$$$", null);
        when(productService.search("tamarindo", null, null, 0, 25))
                .thenReturn(new PageResponse<>(List.of(product), 0, 25, 1, 1));

        assertThat(mvc.get().uri("/api/products?q=tamarindo"))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.totalItems", total -> total.assertThat().isEqualTo(1))
                .extractingPath("$.items[0].code").isEqualTo("4448");
    }

    @Test
    void rejectsUnknownCodeTypes() {
        assertThat(mvc.get().uri("/api/products?type=EAN")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void unknownCodeIsNotFound() {
        when(productService.findByCode("123")).thenReturn(List.of());

        assertThat(mvc.get().uri("/api/products/123"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("No authorized product has code 123");
    }
}
