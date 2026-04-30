'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Users, FileText, Tag, MessageSquare } from 'lucide-react';

const NAV_ITEMS = [
  { href: '/users', label: 'Users', icon: Users },
  { href: '/articles', label: 'Articles', icon: FileText },
  { href: '/stickers', label: 'Stickers', icon: Tag },
  { href: '/notices', label: 'Notices', icon: MessageSquare },
];

export function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="fixed left-0 top-0 h-screen w-56 border-r border-zinc-200 bg-white dark:border-zinc-800 dark:bg-zinc-950 flex flex-col">
      <div className="px-5 py-5 border-b border-zinc-200 dark:border-zinc-800">
        <h1 className="text-lg font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Distcomp CMS
        </h1>
        <p className="text-xs text-zinc-500 mt-0.5">v1.0 API Client</p>
      </div>

      <nav className="flex-1 px-3 py-4 space-y-1">
        {NAV_ITEMS.map(({ href, label, icon: Icon }) => {
          const active = pathname.startsWith(href);
          return (
            <Link
              key={href}
              href={href}
              className={`flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                active
                  ? 'bg-zinc-100 text-zinc-900 dark:bg-zinc-800 dark:text-zinc-50'
                  : 'text-zinc-600 hover:bg-zinc-50 hover:text-zinc-900 dark:text-zinc-400 dark:hover:bg-zinc-900 dark:hover:text-zinc-50'
              }`}
            >
              <Icon size={18} />
              {label}
            </Link>
          );
        })}
      </nav>

      <div className="px-5 py-4 border-t border-zinc-200 dark:border-zinc-800 text-xs text-zinc-400">
        Betenya Konstantin
      </div>
    </aside>
  );
}
