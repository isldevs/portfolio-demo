"use client";
import Link from "next/link";
import { ArrowUpRight, Menu, X } from "lucide-react";
import { useState } from "react";
import { site } from "@/content/site";

const nav = [
  { href: "/#work", label: "Work" },
  { href: "/#about", label: "About" },
  { href: "/#experience", label: "Experience" },
  { href: "/#contact", label: "Contact" }
];
export function Header() {
  const [open, setOpen] = useState(false);
  return <header className="sticky top-0 z-40 border-b border-white/10 bg-[#111623]/90 backdrop-blur-xl">
    <div className="container-main flex h-[82px] items-center justify-between">
      <Link href="/" className="flex items-center gap-3 font-bold tracking-[-.03em]" onClick={() => setOpen(false)}>
        <span className="flex size-10 items-center justify-center rounded-xl bg-accent text-[#141b25] font-extrabold">{site.initials}</span>
        <span className="text-[17px]">{site.name}<span className="text-accent">.</span></span>
      </Link>
      <nav aria-label="Main navigation" className="hidden items-center gap-9 md:flex">
        {nav.map(item => <Link key={item.href} href={item.href} className="text-[13px] font-medium text-[#afbbca] transition hover:text-white">{item.label}</Link>)}
      </nav>
      <Link href="/#contact" className="hidden items-center gap-2 rounded-full bg-accent px-5 py-3 text-[13px] font-bold text-[#17202a] transition hover:bg-white md:inline-flex">Let&apos;s talk <ArrowUpRight size={15}/></Link>
      <button aria-label={open ? "Close navigation" : "Open navigation"} aria-expanded={open} onClick={() => setOpen(!open)} className="rounded-xl border border-white/10 p-2 md:hidden">{open ? <X size={22}/> : <Menu size={22}/>}</button>
    </div>
    {open && <nav aria-label="Mobile navigation" className="container-main flex flex-col gap-1 border-t border-white/10 py-4 md:hidden">
      {nav.map(item => <Link key={item.href} href={item.href} onClick={() => setOpen(false)} className="rounded-lg px-3 py-3 text-sm text-slate-200 hover:bg-white/5">{item.label}</Link>)}
    </nav>}
  </header>;
}
