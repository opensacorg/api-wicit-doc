package org.opensac.wicitapi.dataset;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

/**
 * Lists and serves the published dataset files bundled with the API, so they can be downloaded as
 * originally published.
 */
@Service
public class DatasetService {

    private final ResourcePatternResolver resourceResolver;

    /**
     * Creates the service.
     *
     * @param resourceResolver resolves the dataset folders on the classpath
     */
    public DatasetService(ResourcePatternResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    /**
     * The media type for a file name, falling back to {@code application/octet-stream}. CSV files have no
     * charset because they are served as published, and the vendor file is Windows-1252.
     *
     * @param fileName the file name
     * @return the media type
     */
    public static MediaType mediaType(String fileName) {
        if (fileName.endsWith(".csv")) {
            return new MediaType("text", "csv");
        }
        return MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    private static long contentLength(Resource resource) {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read the size of " + resource.getFilename(), e);
        }
    }

    /**
     * Lists every dataset with its files.
     *
     * @return the datasets
     */
    public List<DatasetResponse> list() {
        return Arrays.stream(Dataset.values()).map(this::describe).toList();
    }

    /**
     * Describes one dataset.
     *
     * @param id the dataset id
     * @return the dataset, or empty when the id is unknown
     */
    public Optional<DatasetResponse> find(String id) {
        return Dataset.fromId(id).map(this::describe);
    }

    /**
     * Finds a file of a dataset. Only names returned by {@link #list()} match, so a request cannot reach
     * other classpath resources.
     *
     * @param id       the dataset id
     * @param fileName the file name
     * @return the file, or empty when the dataset or file does not exist
     */
    public Optional<Resource> findFile(String id, String fileName) {
        return Dataset.fromId(id).flatMap(dataset -> files(dataset).stream()
                .filter(resource -> Objects.equals(resource.getFilename(), fileName))
                .findFirst());
    }

    private DatasetResponse describe(Dataset dataset) {
        var files = files(dataset).stream()
                .map(resource -> new DatasetResponse.FileResponse(
                        resource.getFilename(),
                        mediaType(resource.getFilename()).toString(),
                        contentLength(resource),
                        "/api/datasets/" + dataset.id() + "/files/" + resource.getFilename()))
                .toList();
        return new DatasetResponse(dataset.id(), dataset.title(), dataset.sourceUrl(), files);
    }

    private List<Resource> files(Dataset dataset) {
        try {
            return Arrays.stream(resourceResolver.getResources("classpath:" + dataset.id() + "/*.*"))
                    .filter(Resource::isReadable)
                    .sorted(Comparator.comparing(Resource::getFilename))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not list the files of " + dataset.id(), e);
        }
    }
}
