import { formatNumber, t, tCount, tHtml } from "../i18n/index.js";
import { apiUrl } from "../lib/config.js";
import { escapeHtml } from "../lib/utility.js";
import { basePath } from "../lib/paths.js";

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

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  return response.json();
}

/**
 * "Which Foods Qualify?" product search, with a link to the separate shopping guide.
 *
 * @returns {string} HTML for the search view.
 */
export function SearchView() {
  return `
    <div class="col-md-8 col-md-offset-2" id="content">
      <h2>${t("search.heading")}</h2>
      <aside class="shopping-guide-callout">
        <div>
          <h3>${t("shoppingGuide.searchHeading")}</h3>
          <p>${t("shoppingGuide.searchDescription")}</p>
        </div>
        <a href="${basePath("/shopping-guide")}" data-nav="/shopping-guide">${t("shoppingGuide.openGuide")}</a>
      </aside>
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
 */
export function initSearchView() {
  const input = document.querySelector("#search-input");
  const stateEl = document.querySelector("#search-state");
  if (!input || !stateEl) return;

  let timeout = 0;
  let hasResults = false;

  /** @type {AbortController | null} */
  let inFlight = null;

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
              <div class="category">${escapeHtml(
                result.subcategoryDescription ?? result.categoryDescription ?? "",
              )}</div>
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

  const submit = () => {
    const query = input.value.trim();

    clearTimeout(timeout);
    inFlight?.abort();

    if (!query) {
      hasResults = false;
      stateEl.innerHTML = render.initial();
      return;
    }

    if (!hasResults) {
      stateEl.innerHTML = render.pending();
    }

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
