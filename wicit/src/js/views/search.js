import { formatNumber, t, tCount, tHtml } from "../i18n/index.js";
import { apiUrl } from "../lib/config.js";
import { escapeHtml } from "../lib/utility.js";

const RESULTS_PER_PAGE = 25;
const DEBOUNCE = 200;

/**
 * Search the California WIC Authorized Product List through the WICit API.
 *
 * @param {string} query - Words to search for.
 * @param {AbortSignal} signal - Cancels the request when a newer search starts.
 * @returns {Promise<{items: object[], totalItems: number}>}
 */
async function searchProducts(query, signal) {
  const response = await fetch(apiUrl("/api/products", { q: query, size: RESULTS_PER_PAGE }), {
    headers: { Accept: "application/json" },
    signal,
  });
  if (!response.ok) throw new Error(`Request failed: ${response.status}`);
  return response.json();
}

/**
 * "Which Foods Qualify?" search page. Searches the authorized product list on the API.
 *
 * @returns {string} HTML for the search view.
 */
export function SearchView() {
  return `
    <div class="col-md-8 col-md-offset-2" id="content">
      <h2>${t("search.heading")}</h2>
      <div class="row search-wrap">
        <div class="search-col col-xs-12 col-sm-8 col-md-7">
          <p class="info">${t("search.disclaimer")}</p>
          <input
            class="form-control"
            id="search-input"
            type="text"
            placeholder="${t("search.placeholder")}"
          />
          <div id="search-state">
            <div class="well">${t("search.initial")}</div>
          </div>
          <p class="info">${t("feedback.tweetUs")}</p>
        </div>
      </div>
    </div>
  `;
}

/**
 * Wire the debounced search input after render.
 * */
export function initSearchView() {
  const input = document.querySelector("#search-input");
  const stateEl = document.querySelector("#search-state");
  if (!input || !stateEl) return;

  let timeout = 0;
  let hasResults = false;
  /** @type {AbortController | null} The request for the latest search, cancelled when another starts. */
  let inFlight = null;

  /** HTML renderers for each state of the search results panel. */
  const render = {
    initial: () => `<div class="well">${t("search.initial")}</div>`,
    pending: () => `<div class="well">${t("search.pending")}</div>`,
    error: () => `<div class="alert alert-danger">${t("search.error")}</div>`,
    noResults: (query) => `<div class="well">${tHtml("search.noResults", { query })}</div>`,
    results: (results, count) => {
      const items = results
        .map(
          (result) => `
            <li class="list-group-item">
              ${escapeHtml(result.description)}
              <div class="category">${escapeHtml(result.subcategoryDescription ?? result.categoryDescription ?? "")}</div>
              <div class="upc">${
                result.codeType === "PLU"
                  ? tHtml("search.plu", { plu: result.code })
                  : tHtml("search.upc", { upc: result.code })
              }</div>
            </li>`,
        )
        .join("");
      return `
        <ul class="list-group" id="search-results">${items}</ul>
        <div>${tCount("search.resultCount", count, {
          shown: formatNumber(results.length),
          total: formatNumber(count),
        })}</div>
      `;
    },
  };

  /** Read the input, debounce, and render the matching results state. */
  const submit = () => {
    const query = input.value.trim();
    clearTimeout(timeout);
    inFlight?.abort();
    if (!query) {
      hasResults = false;
      stateEl.innerHTML = render.initial();
      return;
    }

    if (!hasResults) stateEl.innerHTML = render.pending();

    timeout = window.setTimeout(async () => {
      const controller = new AbortController();
      inFlight = controller;
      try {
        const page = await searchProducts(query, controller.signal);
        hasResults = page.items.length > 0;
        stateEl.innerHTML = hasResults
          ? render.results(page.items, page.totalItems)
          : render.noResults(query);
      } catch (error) {
        if (controller.signal.aborted) return;
        console.error("Product search failed:", error);
        hasResults = false;
        stateEl.innerHTML = render.error();
      }
    }, DEBOUNCE);
  };

  input.addEventListener("input", submit);
}
