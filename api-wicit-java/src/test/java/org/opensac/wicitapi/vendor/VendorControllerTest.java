package org.opensac.wicitapi.vendor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(VendorController.class)
@ActiveProfiles("test")
class VendorControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private VendorService vendorService;

    @Test
    void returnsVendorsInBounds() {
        when(vendorService.findInBounds(any(), eq(500))).thenReturn(List.of(new VendorResponse(
                7L, "BEL AIR #501", "6231 FRUITRIDGE ROAD", null, "Sacramento", "95820", "Sacramento", 38.5, -121.4)));

        assertThat(mvc.get().uri("/api/vendors/in-bounds?west=-121.6&south=38.4&east=-121.3&north=38.7"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].name").isEqualTo("BEL AIR #501");
    }

    @Test
    void rejectsCoordinatesOutOfRange() {
        assertThat(mvc.get().uri("/api/vendors/in-bounds?west=-500&south=38.4&east=-121.3&north=38.7"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void requiresTheWholeBoundingBox() {
        assertThat(mvc.get().uri("/api/vendors/in-bounds?west=-121.6"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void allowsGitHubPagesOrigins() {
        assertThat(mvc.options().uri("/api/vendors/in-bounds")
                .header(HttpHeaders.ORIGIN, "https://opensacorg.github.io")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://opensacorg.github.io");
    }

    @Test
    void rejectsUnknownOrigins() {
        assertThat(mvc.options().uri("/api/vendors/in-bounds")
                .header(HttpHeaders.ORIGIN, "https://example.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .hasStatus(HttpStatus.FORBIDDEN);
    }
}
