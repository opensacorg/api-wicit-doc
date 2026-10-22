package org.opensac.wicitapi.dataset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Uses the real service against the dataset files bundled in src/main/resources.
 */
@WebMvcTest(DatasetController.class)
@Import(DatasetService.class)
@ActiveProfiles("test")
class DatasetControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void listsEveryDataset() {
        assertThat(mvc.get().uri("/api/datasets"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()").isEqualTo(Dataset.values().length);
    }

    @Test
    void describesADatasetWithItsFiles() {
        assertThat(mvc.get().uri("/api/datasets/wic-farmers-markets"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.files[*].name").asArray()
                .contains("california-wic-enabled-farmers-list.csv", "datapackage.json");
    }

    @Test
    void downloadsAFileAsAnAttachment() {
        assertThat(mvc.get().uri("/api/datasets/wic-farmers-markets/files/california-wic-enabled-farmers-list.csv"))
                .hasStatusOk()
                .hasContentType("text/csv")
                .hasHeader(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"california-wic-enabled-farmers-list.csv\"")
                .bodyText().startsWith("COUNTY,FARM_NAME");
    }

    @Test
    void unknownDatasetsAndFilesAreNotFound() {
        assertThat(mvc.get().uri("/api/datasets/unknown")).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(mvc.get().uri("/api/datasets/wic-farmers-markets/files/application.yaml"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}
