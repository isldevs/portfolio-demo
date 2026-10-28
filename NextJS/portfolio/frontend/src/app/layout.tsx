import type { Metadata } from "next";
import { site } from "@/content/site";
import { canonicalBase } from "@/lib/metadata";
import "./globals.css";

export const metadata: Metadata = {
  metadataBase: canonicalBase(),
  title: { default: `${site.name} — ${site.role}`, template: `%s | ${site.name}` },
  description: site.description,
  alternates: { canonical: "/" },
  openGraph: { title: `${site.name} — ${site.role}`, description: site.description, type: "website", url: "/", images: [{ url: "/og-image.svg", width: 1200, height: 630 }] },
  twitter: { card: "summary_large_image" },
  robots: { index: true, follow: true },
  icons: { icon: "/favicon.svg" }
};
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body className="antialiased">{children}</body></html>;
}
