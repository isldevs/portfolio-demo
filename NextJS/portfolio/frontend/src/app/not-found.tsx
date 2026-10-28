import Link from "next/link";
import { ArrowLeft } from "lucide-react";
export default function NotFound() { return <main className="container-main flex min-h-screen flex-col items-start justify-center"><p className="eyebrow mb-6">404 / PAGE NOT FOUND</p><h1 className="section-title">Looks like you&apos;re <span className="text-accent">lost.</span></h1><p className="mt-6 text-muted">That page doesn&apos;t exist.</p><Link href="/" className="mt-8 inline-flex items-center gap-2 rounded-full bg-accent px-6 py-4 text-sm font-bold text-[#15202a]"><ArrowLeft size={16}/> Back home</Link></main>; }
