'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { seedDefaultAuthor } from '@/app/services/api';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    seedDefaultAuthor();
    router.replace('/users');
  }, [router]);

  return (
    <div className="flex items-center justify-center h-screen">
      <p className="text-zinc-500 text-sm">Loading...</p>
    </div>
  );
}
