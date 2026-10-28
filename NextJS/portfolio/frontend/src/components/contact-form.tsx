"use client";
import { FormEvent, useState } from "react";
import { ArrowRight, CheckCircle2, LoaderCircle, AlertCircle } from "lucide-react";

type Status = "idle" | "loading" | "success" | "error";
export function ContactForm() {
  const [status, setStatus] = useState<Status>("idle");
  const [error, setError] = useState("");
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = event.currentTarget;
    const data = new FormData(form);
    setStatus("loading"); setError("");
    try {
      const response = await fetch("/api/contact", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: String(data.get("name") || ""),
          email: String(data.get("email") || ""),
          subject: String(data.get("subject") || ""),
          message: String(data.get("message") || ""),
          website: String(data.get("website") || "")
        })
      });
      const result: { message?: string } = await response.json();
      if (!response.ok) throw new Error(result.message || "Sorry, your message could not be sent.");
      setStatus("success"); form.reset();
    } catch (err) {
      setStatus("error"); setError(err instanceof Error ? err.message : "Something went wrong. Please try again.");
    }
  }
  return <form onSubmit={submit} className="space-y-5" aria-label="Contact form">
    <div className="grid gap-5 sm:grid-cols-2"><label className="block text-xs font-semibold text-slate-300">Your name <span className="text-accent">*</span><input name="name" type="text" autoComplete="name" placeholder="Jane Smith" required maxLength={120} className="input-field mt-2 text-sm"/></label><label className="block text-xs font-semibold text-slate-300">Email address <span className="text-accent">*</span><input name="email" type="email" autoComplete="email" placeholder="jane@company.com" required maxLength={180} className="input-field mt-2 text-sm"/></label></div>
    <label className="block text-xs font-semibold text-slate-300">Subject <span className="text-accent">*</span><input name="subject" type="text" placeholder="Let's work together" required maxLength={180} className="input-field mt-2 text-sm"/></label>
    <label className="block text-xs font-semibold text-slate-300">Your message <span className="text-accent">*</span><textarea name="message" placeholder="Tell me a little about your project..." rows={5} required minLength={10} maxLength={5000} className="input-field mt-2 resize-y text-sm"/></label>
    <div className="absolute -left-[9999px]" aria-hidden="true"><label>Website<input name="website" type="text" tabIndex={-1} autoComplete="off"/></label></div>
    <button type="submit" disabled={status === "loading"} className="flex w-full items-center justify-center gap-3 rounded-xl bg-accent px-6 py-4 text-sm font-bold text-[#17202a] transition hover:bg-white disabled:cursor-not-allowed disabled:opacity-60">{status === "loading" ? <><LoaderCircle size={17} className="animate-spin"/> Sending...</> : <>Send message <ArrowRight size={18}/></>}</button>
    <div aria-live="polite" role="status" className="min-h-6">{status === "success" && <p className="flex items-center gap-2 text-sm text-[#c5f3a3]"><CheckCircle2 size={17}/> Message received. Thank you — I&apos;ll be in touch!</p>}{status === "error" && <p className="flex items-center gap-2 text-sm text-rose-300"><AlertCircle size={17}/> {error}</p>}</div>
  </form>;
}
