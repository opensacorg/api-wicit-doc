/**
 * The mapbox tile-layer template URL.
 *
 * @type {string}
 */
export const mapboxIntegrationUrl = import.meta.env.VITE_MAPBOX_INTEGRATION_URL || "";

/**
 * The base URL of the WICit API, without a trailing slash.
 *
 * @type {string}
 */
export const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || "").replace(/\/$/, "");

/**
 * Build a URL for an API path with query parameters. Parameters that are null or undefined are left out.
 *
 * @param {string} path - The API path, such as `/api/products`.
 * @param {Record<string, string | number | boolean | null | undefined>} [params] - Query parameters.
 * @returns {string}
 */
export function apiUrl(path, params = {}) {
  const query = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== null && value !== undefined) query.set(key, String(value));
  }
  const search = query.toString();
  return `${apiBaseUrl}${path}${search ? `?${search}` : ""}`;
}
