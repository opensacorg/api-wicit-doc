package org.opensac.wicitapi.common;

import java.util.List;

/**
 * A page of results in a stable JSON shape.
 *
 * @param items      the results on this page
 * @param page       the zero-based page number
 * @param size       the requested page size
 * @param totalItems the number of results across all pages
 * @param totalPages the number of pages
 * @param <T>        the result type
 */
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {

    /**
     * Creates a page, working out the number of pages.
     *
     * @param items      the results on this page
     * @param page       the zero-based page number
     * @param size       the requested page size
     * @param totalItems the number of results across all pages
     * @param <T>        the result type
     * @return the page
     */
    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalItems) {
        return new PageResponse<>(items, page, size, totalItems, (int) ((totalItems + size - 1) / size));
    }
}
