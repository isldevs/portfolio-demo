import type { MetadataRoute } from "next";
import { site } from "@/content/site";
import { canonicalBase } from "@/lib/metadata";
export default function sitemap(): MetadataRoute.Sitemap {
  const base = canonicalBase();
  return [
    { url: new URL("/", base).toString(), lastModified: new Date(), changeFrequency: "monthly", priority: 1 },
    ...site.projects.map((p) => ({ url: new URL(`/projects/${p.slug}`, base).toString(), lastModified: new Date(), changeFrequency: "monthly" as const, priority: 0.7 }))
  ];
}
