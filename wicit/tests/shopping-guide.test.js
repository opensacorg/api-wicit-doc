import { afterEach, beforeEach, describe, expect, it, vi } from "vite-plus/test";
import { initI18n } from "../src/js/i18n/index.js";
import { SearchView } from "../src/js/views/search.js";
import { ShoppingGuideView, initShoppingGuide } from "../src/js/views/shopping-guide.js";

const milk = {
  slug: "milk",
  name: "Milk",
  sourceUrl: "https://myfamily.wic.ca.gov/WicShoppingGuide/Milk",
  sourceLastUpdated: "2026-05-29",
  canBuy: { description: "Plain milk in one gallon containers.", items: ["Nonfat milk"] },
  conditionalPurchases: {
    condition: "Only if included in your WIC Food Balance",
    items: ["Whole milk"],
  },
  cannotBuy: ["Organic milk"],
  helpfulHints: {
    description: "Talk to WIC staff about alternatives.",
    items: [],
    note: "Choose the milk in your benefits.",
  },
  images: [],
  brands: [],
};

function container() {
  return {
    innerHTML: "",
    isConnected: true,
    setAttribute: vi.fn(),
    querySelector: vi.fn(),
  };
}

function response(guide) {
  return { ok: true, json: async () => guide };
}

beforeEach(async () => {
  vi.stubGlobal("window", { location: { search: "" } });
  vi.stubGlobal("navigator", { languages: ["en"] });
  vi.stubGlobal("document", { documentElement: {}, querySelector: vi.fn() });
  vi.stubGlobal("fetch", vi.fn());
  await initI18n("en");
});

afterEach(() => {
  vi.unstubAllGlobals();
  vi.unstubAllEnvs();
  vi.restoreAllMocks();
});

describe("separate shopping-guide page", () => {
  it("keeps search available and links to the dedicated guide instead of embedding it", () => {
    const html = SearchView();
    expect(html).toContain('id="search-input"');
    expect(html).toContain('data-nav="/shopping-guide"');
    expect(html).not.toContain('id="shopping-guide"');
  });

  it("uses category URLs that work on a GitHub Pages project path", () => {
    vi.stubEnv("BASE_URL", "/wicit/");
    const html = ShoppingGuideView({ params: { slug: "infant-fruits" } });
    expect(html).toContain('href="/wicit/shopping-guide/milk"');
    expect(html).toContain('href="/wicit/shopping-guide/infant-fruits"');
    expect(html).toContain('href="/wicit/search"');
    expect(html).toMatch(
      /data-nav="\/shopping-guide\/infant-fruits"\s+class="guide-category is-selected"\s+aria-current="page"/,
    );
  });

  it("defaults to milk and preserves conditional benefits, restrictions and source attribution", async () => {
    const target = container();
    document.querySelector.mockReturnValue(target);
    fetch.mockResolvedValue(response(milk));
    await initShoppingGuide();
    expect(fetch.mock.calls[0][0]).toMatch(/\/api\/shopping-guide\/milk$/);
    expect(target.innerHTML).toContain("Whole milk");
    expect(target.innerHTML).toContain("Organic milk");
    expect(target.innerHTML).toContain("2026-05-29");
    expect(target.innerHTML).toContain(milk.sourceUrl);
    expect(target.setAttribute).toHaveBeenLastCalledWith("aria-busy", "false");
  });

  it("loads infant-food brands and omits the inapplicable conditional section", async () => {
    const target = container();
    document.querySelector.mockReturnValue(target);
    fetch.mockResolvedValue(
      response({
        ...milk,
        slug: "infant-fruits",
        name: "Infant Fruits and Vegetables",
        conditionalPurchases: null,
        brands: [{ name: "Gerber", logoUrl: "https://docs.wic.ca.gov/gerber.png" }],
      }),
    );
    await initShoppingGuide({ params: { slug: "infant-fruits" } });
    expect(fetch.mock.calls[0][0]).toMatch(/\/api\/shopping-guide\/infant-fruits$/);
    expect(target.innerHTML).toContain("Gerber");
    expect(target.innerHTML).toContain('src="https://docs.wic.ca.gov/gerber.png"');
    expect(target.innerHTML).not.toContain('id="guide-conditions-heading"');
  });

  it("escapes API text and rejects executable image and source URLs", async () => {
    const target = container();
    document.querySelector.mockReturnValue(target);
    fetch.mockResolvedValue(
      response({
        ...milk,
        name: '<img src=x onerror="alert(1)">',
        sourceUrl: "javascript:alert(1)",
        images: [{ url: "javascript:alert(2)", alt: "unsafe" }],
        brands: [{ name: "<script>unsafe</script>", logoUrl: "javascript:alert(3)" }],
      }),
    );
    await initShoppingGuide();
    expect(target.innerHTML).toContain("&lt;img");
    expect(target.innerHTML).toContain("&lt;script&gt;");
    expect(target.innerHTML).not.toContain("javascript:");
    expect(target.innerHTML).not.toContain("<script>");
  });

  it("offers a retry after a failed request and can recover", async () => {
    vi.spyOn(console, "error").mockImplementation(() => {});
    const retry = { addEventListener: vi.fn() };
    const target = container();
    target.querySelector.mockReturnValue(retry);
    document.querySelector.mockReturnValue(target);
    fetch.mockResolvedValueOnce({ ok: false, status: 503 }).mockResolvedValueOnce(response(milk));
    await initShoppingGuide();
    expect(target.innerHTML).toContain('role="alert"');
    expect(target.innerHTML).toContain("Try again");
    expect(target.innerHTML).not.toContain("milk guide");
    const handler = retry.addEventListener.mock.calls[0][1];
    handler();
    await vi.waitFor(() => expect(target.innerHTML).toContain('id="guide-title"'));
  });

  it("does not request unrecognized category paths", async () => {
    const target = container();
    document.querySelector.mockReturnValue(target);
    await initShoppingGuide({ params: { slug: "../../private" } });
    expect(fetch).not.toHaveBeenCalled();
    expect(target.innerHTML).toContain("Guide not found");
  });

  it("cancels the previous category request so a late response cannot replace the current one", async () => {
    const pending = Promise.withResolvers();
    const oldTarget = container();
    const newTarget = container();
    document.querySelector.mockReturnValueOnce(oldTarget).mockReturnValueOnce(newTarget);
    fetch
      .mockReturnValueOnce(pending.promise)
      .mockResolvedValueOnce(
        response({ ...milk, slug: "infant-fruits", name: "Infant Fruits and Vegetables" }),
      );
    const first = initShoppingGuide();
    await initShoppingGuide({ params: { slug: "infant-fruits" } });
    expect(fetch.mock.calls[0][1].signal.aborted).toBe(true);
    pending.resolve(response({ ...milk, name: "Stale response" }));
    await first;
    expect(oldTarget.innerHTML).not.toContain("Stale response");
    expect(newTarget.innerHTML).toContain("Infant Fruits and Vegetables");
  });

  it("ignores a response after navigation has removed its container", async () => {
    const target = container();
    document.querySelector.mockReturnValue(target);
    fetch.mockImplementation(async () => {
      target.isConnected = false;
      return response(milk);
    });
    await initShoppingGuide();
    expect(target.innerHTML).not.toContain('id="guide-title"');
  });
});
