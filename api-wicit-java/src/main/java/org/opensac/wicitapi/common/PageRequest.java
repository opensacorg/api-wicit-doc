package org.opensac.wicitapi.common;

/**
 * A zero-based page number and page size, as passed to a repository.
 *
 * @param page the zero-based page number
 * @param size the page size
 */
public record PageRequest(int page, int size) {

    /**
     * The number of rows to skip.
     *
     * @return the offset
     */
    public int offset() {
        return page * size;
    }
}
