import { Activity, ArrowUpRight, Check, CheckCircle2, Layers3, MoreHorizontal, TrendingUp } from "lucide-react";
import type { PortfolioProject } from "@/content/site";

export function ProjectVisual({ project, compact = false }: { project: PortfolioProject; compact?: boolean }) {
  const type = project.gradient;
  return <div className={`project-${type} relative flex min-h-[240px] items-center justify-center overflow-hidden rounded-[18px] p-6 ${compact ? "md:min-h-[280px]" : "md:min-h-[350px]"}`}>
    <div aria-hidden="true" className="absolute inset-0 grid-guide opacity-35"/>
    <span className="absolute left-7 top-7 rounded-full border border-white/20 px-3 py-1 text-[10px] font-semibold tracking-[.16em] text-white/70">{project.number} / FEATURED</span>
    <ArrowUpRight className="absolute right-7 top-7 text-white/70" size={22}/>
    {type === "blue" && <div aria-label="Illustrative fintech interface mockup" className="relative top-5 w-[82%] max-w-[440px] -rotate-[5deg] rounded-2xl border border-white/25 bg-[#101a2e]/95 p-4 shadow-[0_30px_90px_rgba(0,0,0,.42)] transition duration-500 group-hover:rotate-0">
      <div className="mb-5 flex items-center justify-between"><span className="flex items-center gap-2 text-xs font-semibold text-white"><Layers3 size={16} className="text-[#b7ed85]"/> nexus<span className="font-normal text-slate-400">/ workspace</span></span><MoreHorizontal size={18} className="text-slate-400"/></div>
      <div className="mb-3 flex items-end justify-between"><div><p className="text-[10px] text-slate-400">Total portfolio</p><strong className="text-2xl tracking-tight text-white">$248,590<span className="text-sm text-slate-400">.00</span></strong></div><span className="rounded-md bg-emerald-400/15 px-2 py-1 text-[10px] text-emerald-300">+12.8%</span></div>
      <div className="flex h-20 items-end gap-2">{[42,65,50,73,58,87,69,94,72,98,84,100].map((n,i)=><span key={i} className="flex-1 rounded-t-sm bg-[#8faccd]" style={{height:`${n}%`,opacity: .25+i*.055}}/>)}</div>
      <div className="mt-4 flex gap-2"><div className="h-9 flex-1 rounded-md bg-white/8"/><div className="h-9 w-20 rounded-md bg-[#b7ed85]/90"/></div>
    </div>}
    {type === "violet" && <div aria-label="Illustrative analytics dashboard mockup" className="relative top-5 flex w-[82%] max-w-[450px] rotate-[5deg] gap-3 rounded-2xl border border-white/20 bg-[#1b1b32]/95 p-4 shadow-[0_30px_90px_rgba(0,0,0,.4)] transition duration-500 group-hover:rotate-0">
      <div className="flex w-10 flex-col items-center gap-3 border-r border-white/10 pr-3"><Activity size={18} className="text-[#dbc4ff]"/>{[1,2,3].map(i=><div key={i} className="size-4 rounded-md bg-white/10"/>)}</div>
      <div className="min-w-0 flex-1"><div className="mb-3 flex items-center justify-between"><span className="text-xs font-semibold">Overview</span><span className="text-[9px] text-white/40">Last 30 days</span></div><div className="grid grid-cols-2 gap-2"><div className="rounded-lg bg-white/8 p-3"><p className="text-[9px] text-white/50">Revenue</p><strong className="text-lg">$84.2k</strong><p className="mt-1 text-[9px] text-emerald-300">↑ 18.4%</p></div><div className="rounded-lg bg-white/8 p-3"><p className="text-[9px] text-white/50">Growth</p><strong className="text-lg">24.8%</strong><p className="mt-1 text-[9px] text-emerald-300">↑ 6.2%</p></div></div><div className="mt-3 flex h-16 items-center justify-center rounded-lg bg-white/5"><TrendingUp size={80} strokeWidth={1} className="text-[#ba9df4]"/></div></div>
    </div>}
    {type === "orange" && <div aria-label="Illustrative task board mockup" className="relative top-4 w-[85%] max-w-[440px] -rotate-[4deg] rounded-2xl border border-white/20 bg-[#252333]/95 p-4 shadow-[0_30px_90px_rgba(0,0,0,.42)] transition duration-500 group-hover:rotate-0">
      <div className="mb-4 flex items-center justify-between"><div className="flex items-center gap-2 text-xs font-semibold"><CheckCircle2 size={17} className="text-orange-300"/> flow. <span className="font-normal text-white/45">/ team board</span></div><span className="text-[10px] text-white/50">October</span></div><div className="grid grid-cols-3 gap-2">{["To do","In progress","Done"].map((column,i)=><div key={column}><p className="mb-2 text-[9px] text-white/60">{column}</p>{[0,1].slice(0,i===1?1:2).map(j=><div key={j} className="mb-2 rounded-lg border border-white/8 bg-[#343244] p-2"><div className={`mb-2 h-1.5 w-8 rounded-full ${i===0?"bg-orange-300":i===1?"bg-violet-300":"bg-emerald-300"}`}/><div className="mb-1 h-1.5 w-full rounded bg-white/30"/><div className="h-1.5 w-2/3 rounded bg-white/15"/><div className="mt-2 flex justify-end">{i===2?<Check size={13} className="text-emerald-300"/>:<div className="size-3 rounded-full bg-orange-100/70"/>}</div></div>)}</div>)}</div>
    </div>}
    <div className="pointer-events-none absolute bottom-0 left-0 right-0 h-20 bg-gradient-to-t from-black/20 to-transparent"/>
  </div>;
}
