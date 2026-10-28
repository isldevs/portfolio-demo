export function canonicalBase(): URL {
  const value = process.env.NEXT_PUBLIC_SITE_URL || "https://example.com";
  try { return new URL(value); } catch { return new URL("https://example.com"); }
}
