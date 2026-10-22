package org.opensac.wicitapi.dataset;

import java.util.List;

/**
 * A published dataset and the files that can be downloaded from this API.
 *
 * @param id        the dataset name on data.chhs.ca.gov
 * @param title     the dataset title
 * @param sourceUrl the dataset's page on data.chhs.ca.gov
 * @param files     the downloadable files
 */
public record DatasetResponse(String id, String title, String sourceUrl, List<FileResponse> files) {

    /**
     * A downloadable file.
     *
     * @param name        the file name
     * @param contentType the media type
     * @param sizeBytes   the size in bytes
     * @param href        the API path that downloads the file
     */
    public record FileResponse(String name, String contentType, long sizeBytes, String href) {
    }
}
