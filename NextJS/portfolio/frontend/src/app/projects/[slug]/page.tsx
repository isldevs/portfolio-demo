import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import { ArrowLeft, ArrowUpRight, Github, Globe } from "lucide-react";
import { site } from "@/content/site";
import { ProjectVisual } from "@/components/project-visual";
import { Header } from "@/components/header";
import { Footer } from "@/components/footer";

type Props = { params: Promise<{ slug: string }> };
export function generateStaticParams() { return site.projects.map(project => ({ slug: project.slug })); }
export async function generateMetadata({params}: Props): Promise<Metadata> {
  const {slug} = await params;
  const project = site.projects.find(p => p.slug === slug);
  if (!project) return { title: "Project not found" };
  return { title: project.title, description: project.summary, alternates: { canonical: `/projects/${slug}` }, openGraph: { title: project.title, description: project.summary } };
}
export default async function ProjectDetails({params}: Props) {
  const {slug} = await params;
  const project = site.projects.find(p => p.slug === slug);
  if (!project) notFound();
  return <><Header/><main className="container-main py-14 md:py-20"><Link href="/#work" className="mb-12 inline-flex items-center gap-2 text-sm text-muted transition hover:text-accent"><ArrowLeft size={17}/> Back to projects</Link><div className="mb-10"><p className="eyebrow mb-5">{project.label} / {project.year}</p><h1 className="section-title max-w-4xl">{project.title}<span className="text-accent">.</span></h1><p className="mt-7 max-w-2xl text-lg leading-8 text-muted">{project.summary}</p></div><div className="mb-12"><ProjectVisual project={project}/></div><div className="grid gap-12 lg:grid-cols-[1fr_.35fr]"><div className="space-y-12"><section><p className="eyebrow mb-4">THE CHALLENGE</p><h2 className="mb-4 text-2xl font-bold">What needed solving</h2><p className="leading-8 text-muted">{project.challenge}</p></section><section><p className="eyebrow mb-4">THE APPROACH</p><h2 className="mb-4 text-2xl font-bold">The solution</h2><p className="leading-8 text-muted">{project.solution}</p></section><section><p className="eyebrow mb-4">THE RESULT</p><h2 className="mb-4 text-2xl font-bold">What came out of it</h2><p className="leading-8 text-muted">{project.outcome}</p></section></div><aside className="h-fit rounded-2xl border border-white/10 bg-white/3 p-6"><p className="eyebrow mb-5">PROJECT DETAILS</p><p className="text-xs text-muted">Year</p><p className="mb-5 mt-1 font-semibold">{project.year}</p><p className="text-xs text-muted">Technology</p><div className="mt-3 flex flex-wrap gap-2">{project.stack.map(t=><span className="pill" key={t}>{t}</span>)}</div>{project.links.demo && <a href={project.links.demo} target="_blank" rel="noopener noreferrer" className="mt-7 flex items-center gap-2 text-sm text-accent">Live project <Globe size={16}/><ArrowUpRight size={15}/></a>}{project.links.source && <a href={project.links.source} target="_blank" rel="noopener noreferrer" className="mt-3 flex items-center gap-2 text-sm text-accent">Source code <Github size={16}/><ArrowUpRight size={15}/></a>}</aside></div><div className="mt-20 border-t border-white/10 pt-10"><Link href="/#work" className="inline-flex items-center gap-3 rounded-full bg-accent px-6 py-4 text-sm font-bold text-[#15202a]">Explore more work <ArrowUpRight size={16}/></Link></div></main><Footer/></>;
}
