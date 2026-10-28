import Link from "next/link";
import { ArrowUpRight, Github, Linkedin, Mail } from "lucide-react";
import { site } from "@/content/site";
export function Footer() {
  return <footer className="border-t border-white/10 py-9">
    <div className="container-main flex flex-col items-center justify-between gap-5 md:flex-row">
      <div className="flex items-center gap-3"><span className="flex size-9 items-center justify-center rounded-xl bg-accent text-sm font-extrabold text-[#182029]">{site.initials}</span><div><p className="text-sm font-bold">{site.name}</p><p className="text-xs text-muted">Built with care, curiosity & coffee.</p></div></div>
      <div className="flex items-center gap-5 text-muted">
        <a href={site.github} target="_blank" rel="noopener noreferrer" aria-label="GitHub" className="hover:text-accent"><Github size={18}/></a>
        <a href={site.linkedin} target="_blank" rel="noopener noreferrer" aria-label="LinkedIn" className="hover:text-accent"><Linkedin size={18}/></a>
        <a href={`mailto:${site.email}`} aria-label="Email" className="hover:text-accent"><Mail size={18}/></a>
        <Link href="/#top" className="flex items-center gap-1 text-xs font-semibold hover:text-accent">Back to top <ArrowUpRight size={14}/></Link>
      </div>
      <p className="text-xs text-muted">© {new Date().getFullYear()} {site.name}. All rights reserved.</p>
    </div>
  </footer>;
}
