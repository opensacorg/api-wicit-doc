import { t } from "../i18n/index.js";
import { apiUrl } from "../lib/config.js";
import { basePath } from "../lib/paths.js";
import { escapeHtml } from "../lib/utility.js";

const GUIDES = [
  { slug: "milk", name: "shoppingGuide.milk", description: "shoppingGuide.milkDescription" },
  {
    slug: "infant-fruits",
    name: "shoppingGuide.infantFruits",
    description: "shoppingGuide.infantDescription",
  },
];

let inFlight = null;

function safeUrl(value) {
  try {
    const url = new URL(value);
    return ["http:", "https:"].includes(url.protocol) ? url.href : "";
  } catch {
    return "";
  }
}

function renderList(items = []) {
  return items.length
    ? `<ul>${items.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}</ul>`
    : "";
}

function renderImages(images = []) {
  return images
    .filter((image) => safeUrl(image.url))
    .map(
      (image) => `
        <figure class="guide-illustration">
          <img src="${escapeHtml(safeUrl(image.url))}"
            alt="${escapeHtml(image.alt ?? "")}" loading="lazy" />
        </figure>`,
    )
    .join("");
}

function renderBrands(brands = []) {
  if (!brands.length) return "";

  return `
    <section class="guide-panel guide-brands" aria-labelledby="guide-brands-heading">
      <div class="guide-panel-heading">
        <h3 id="guide-brands-heading">${t("shoppingGuide.brands")}</h3>
        <span class="guide-count">${brands.length}</span>
      </div>
      <div class="guide-brand-grid">
        ${brands
          .map((brand) => {
            const logo = safeUrl(brand.logoUrl);
            return `
              <figure class="guide-brand">
                ${logo ? `<img src="${escapeHtml(logo)}" alt="${escapeHtml(brand.name)}" loading="lazy" />` : ""}
                <figcaption>${escapeHtml(brand.name)}</figcaption>
              </figure>`;
          })
          .join("")}
      </div>
    </section>`;
}

function renderGuide(guide) {
  const conditional = guide.conditionalPurchases;
  const hints = guide.helpfulHints;
  const source = safeUrl(guide.sourceUrl);
  // Milk's first image introduces the category; the remaining illustrations explain shopping tips.
  const hasHero = guide.slug === "milk" && guide.images?.length;
  const hintImages = hasHero ? guide.images.slice(1) : guide.images;

  return `
    <article class="guide-article">
      <header class="guide-article-header">
        <div>
          <p class="guide-eyebrow">${t("shoppingGuide.categoryLabel")}</p>
          <h2 id="guide-title">${escapeHtml(guide.name)}</h2>
          <p class="guide-source-date">${t("shoppingGuide.updated")}
            <time datetime="${escapeHtml(guide.sourceLastUpdated)}">${escapeHtml(guide.sourceLastUpdated)}</time>
          </p>
        </div>
        ${hasHero ? `<div class="guide-hero-image">${renderImages([guide.images[0]])}</div>` : ""}
      </header>

      <div class="guide-rules-grid">
        <section class="guide-panel guide-can-buy" aria-labelledby="guide-can-buy-heading">
          <p class="guide-rule-label"><span aria-hidden="true">✓</span> ${t("shoppingGuide.allowed")}</p>
          <h3 id="guide-can-buy-heading">${t("shoppingGuide.canBuy")}</h3>
          <div lang="en" dir="ltr">
            <p class="guide-rule-summary">${escapeHtml(guide.canBuy.description)}</p>
            ${renderList(guide.canBuy.items)}
          </div>
        </section>

        <section class="guide-panel guide-cannot-buy" aria-labelledby="guide-cannot-buy-heading">
          <p class="guide-rule-label"><span aria-hidden="true">×</span> ${t("shoppingGuide.notAllowed")}</p>
          <h3 id="guide-cannot-buy-heading">${t("shoppingGuide.cannotBuy")}</h3>
          <div lang="en" dir="ltr">${renderList(guide.cannotBuy)}</div>
        </section>
      </div>

      ${
        conditional
          ? `<section class="guide-panel guide-conditions" aria-labelledby="guide-conditions-heading">
              <h3 id="guide-conditions-heading">${t("shoppingGuide.conditionalPurchases")}</h3>
              <div lang="en" dir="ltr">
                <p>${escapeHtml(conditional.condition)}</p>
                ${renderList(conditional.items)}
              </div>
            </section>`
          : ""
      }

      ${renderBrands(guide.brands)}

      ${
        hints
          ? `<section class="guide-panel guide-hints" aria-labelledby="guide-hints-heading">
              <h3 id="guide-hints-heading">${t("shoppingGuide.helpfulHints")}</h3>
              <div lang="en" dir="ltr">
                <p>${escapeHtml(hints.description)}</p>
                ${renderList(hints.items)}
                ${renderImages(hintImages)}
                ${hints.note ? `<p class="guide-hint-note">${escapeHtml(hints.note)}</p>` : ""}
              </div>
            </section>`
          : renderImages(hintImages)
      }

      <footer class="guide-source">
        <p>${t("shoppingGuide.sourceNote")}</p>
        ${source ? `<a href="${escapeHtml(source)}" target="_blank" rel="noopener noreferrer">${t("shoppingGuide.source")} <span aria-hidden="true">↗</span></a>` : ""}
      </footer>
    </article>`;
}

/** A separate, linkable shopping-guide page; product search remains at /search. */
export function ShoppingGuideView({ params = {} } = {}) {
  const slug = params.slug || "milk";

  return `
    <div id="content" class="shopping-guide-page">
      <header class="guide-page-header">
        <p class="guide-eyebrow">${t("shoppingGuide.eyebrow")}</p>
        <h1>${t("shoppingGuide.pageTitle")}</h1>
        <p class="guide-page-intro">${t("shoppingGuide.intro")}</p>
        <a class="guide-search-link" href="${basePath("/search")}" data-nav="/search">${t("shoppingGuide.backToSearch")} <span aria-hidden="true">→</span></a>
      </header>

      <nav class="guide-category-nav" aria-label="${escapeHtml(t("shoppingGuide.chooseCategory"))}">
        ${GUIDES.map(
          (guide, index) => `
            <a href="${basePath(`/shopping-guide/${guide.slug}`)}"
              data-nav="/shopping-guide/${guide.slug}"
              class="guide-category${guide.slug === slug ? " is-selected" : ""}"
              ${guide.slug === slug ? 'aria-current="page"' : ""}>
              <span class="guide-category-number" aria-hidden="true">0${index + 1}</span>
              <span>
                <strong>${t(guide.name)}</strong>
                <span class="guide-category-description">${t(guide.description)}</span>
              </span>
              <span class="guide-category-arrow" aria-hidden="true">→</span>
            </a>`,
        ).join("")}
      </nav>

      <p class="guide-language-note">${t("shoppingGuide.languageNote")}</p>
      <section id="shopping-guide" aria-live="polite" aria-busy="true">
        <div class="guide-status"><p>${t("shoppingGuide.loading")}</p></div>
      </section>
    </div>`;
}

/** Fetch just the selected category, without letting an old response replace a newer page. */
export async function initShoppingGuide({ params = {} } = {}) {
  inFlight?.abort();
  const container = document.querySelector("#shopping-guide");
  if (!container) return;

  const slug = params.slug || "milk";
  if (!GUIDES.some((guide) => guide.slug === slug)) {
    container.innerHTML = `<div class="guide-status" role="alert"><h2>${t("shoppingGuide.notFound")}</h2><p>${t("shoppingGuide.chooseAnother")}</p></div>`;
    container.setAttribute("aria-busy", "false");
    return;
  }

  const controller = new AbortController();
  inFlight = controller;
  container.setAttribute("aria-busy", "true");
  container.innerHTML = `<div class="guide-status"><p>${t("shoppingGuide.loading")}</p></div>`;

  try {
    const response = await fetch(apiUrl(`/api/shopping-guide/${encodeURIComponent(slug)}`), {
      headers: { Accept: "application/json" },
      signal: controller.signal,
    });
    if (!response.ok) throw new Error(`Shopping guide request failed: ${response.status}`);
    const guide = await response.json();
    if (controller.signal.aborted || !container.isConnected) return;
    container.innerHTML = renderGuide(guide);
  } catch (error) {
    if (controller.signal.aborted || !container.isConnected) return;
    console.error(`Shopping guide failed for ${slug}:`, error);
    container.innerHTML = `
      <div class="guide-status guide-status-error" role="alert">
        <h2>${t("shoppingGuide.errorTitle")}</h2>
        <p>${t("shoppingGuide.error")}</p>
        <button type="button" class="guide-retry" data-guide-retry>${t("shoppingGuide.retry")}</button>
      </div>`;
    container.querySelector("[data-guide-retry]")?.addEventListener("click", () => {
      initShoppingGuide({ params: { slug } });
    });
  } finally {
    if (!controller.signal.aborted && container.isConnected) {
      container.setAttribute("aria-busy", "false");
    }
  }
}
