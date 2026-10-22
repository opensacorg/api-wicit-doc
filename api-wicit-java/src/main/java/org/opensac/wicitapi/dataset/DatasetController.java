package org.opensac.wicitapi.dataset;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

/**
 * Downloads of the published dataset files, exactly as they were downloaded from data.chhs.ca.gov.
 */
@RestController
@RequestMapping("/api/datasets")
@Tag(name = "Datasets", description = "Download the published CDPH WIC data files")
public class DatasetController {

    private final DatasetService datasetService;

    /**
     * Creates the controller.
     *
     * @param datasetService the dataset service
     */
    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    private static ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    /**
     * Lists the datasets.
     *
     * @return the datasets with their files
     */
    @GetMapping
    @Operation(summary = "List datasets", description = "Each published dataset with its downloadable files.")
    public List<DatasetResponse> list() {
        return datasetService.list();
    }

    /**
     * Describes one dataset.
     *
     * @param id the dataset id
     * @return the dataset with its files
     */
    @GetMapping("/{id}")
    @Operation(summary = "Describe a dataset")
    public DatasetResponse get(@PathVariable String id) {
        return datasetService.find(id).orElseThrow(() -> notFound("Unknown dataset " + id));
    }

    /**
     * Downloads a file.
     *
     * @param id       the dataset id
     * @param fileName the file name
     * @return the file as an attachment
     */
    @GetMapping("/{id}/files/{fileName:.+}")
    @Operation(summary = "Download a dataset file",
            description = "Returns the file as published: CSV data, data dictionaries, datapackage.json and PDFs.")
    public ResponseEntity<Resource> download(@PathVariable String id, @PathVariable String fileName) {
        Resource file = datasetService.findFile(id, fileName)
                .orElseThrow(() -> notFound("Dataset " + id + " has no file " + fileName));
        return ResponseEntity.ok()
                .contentType(DatasetService.mediaType(fileName))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(file);
    }
}
