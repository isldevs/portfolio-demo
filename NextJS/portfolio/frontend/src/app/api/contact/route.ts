import { NextRequest, NextResponse } from "next/server";

export const runtime = "nodejs";
const json = (message: string, status: number) => NextResponse.json({ message }, { status });

export async function POST(request: NextRequest) {
  // A browser sends Origin for JSON POST. Block cross-site form submissions.
  const origin = request.headers.get("origin");
  const host = request.headers.get("x-forwarded-host") || request.headers.get("host");
  if (origin) {
    try { if (new URL(origin).host !== host) return json("Forbidden origin.", 403); }
    catch { return json("Invalid origin.", 403); }
  }
  if (!request.headers.get("content-type")?.toLowerCase().startsWith("application/json")) return json("JSON required.", 415);
  if (Number(request.headers.get("content-length") || 0) > 8500) return json("Message is too large.", 413);
  const text = await request.text();
  if (text.length > 8500) return json("Message is too large.", 413);
  let payload: unknown;
  try { payload = JSON.parse(text); } catch { return json("Invalid JSON.", 400); }
  if (!payload || typeof payload !== "object" || Array.isArray(payload)) return json("Invalid request.", 400);
  const data = payload as Record<string, unknown>;
  for (const key of ["name", "email", "subject", "message", "website"]) {
    if (data[key] !== undefined && typeof data[key] !== "string") return json("Invalid form data.", 400);
  }
  const apiKey = process.env.INTERNAL_API_KEY;
  if (!apiKey) return json("Contact service is not configured.", 503);
  // Only the trusted reverse proxy should be allowed to set X-Forwarded-For.
  const forwarded = request.headers.get("x-forwarded-for")?.split(",")[0]?.trim();
  const clientIp = (forwarded || request.headers.get("x-real-ip") || "unknown").slice(0, 80);
  try {
    const backend = process.env.BACKEND_URL || "http://localhost:8080";
    const response = await fetch(`${backend}/api/v1/contact`, {
      method: "POST",
      headers: { "Content-Type": "application/json", "X-Internal-Api-Key": apiKey, "X-Client-IP": clientIp },
      body: JSON.stringify({ name: data.name || "", email: data.email || "", subject: data.subject || "", message: data.message || "", website: data.website || "" }),
      cache: "no-store",
      signal: AbortSignal.timeout(9000)
    });
    const result: unknown = await response.json().catch(() => null);
    const message = result && typeof result === "object" && "message" in result && typeof result.message === "string" ? result.message : "Request could not be completed.";
    return json(message, response.status);
  } catch {
    return json("Contact service is temporarily unavailable. Please try again later.", 503);
  }
}
