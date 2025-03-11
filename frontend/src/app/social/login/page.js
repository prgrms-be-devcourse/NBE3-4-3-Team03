'use client';

import { useRouter } from 'next/navigation'; // 'next/router' 대신 'next/navigation'에서 import
import { useEffect } from 'react';

export default function CustomerSignup() {
  const router = useRouter();

  useEffect(() => {
    // 컴포넌트가 마운트된 후에 리다이렉트
    router.push('http://localhost:3000');
  }, [router]);  // 이 코드가 처음 실행될 때만 실행됨

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-8">
      {/* 콘텐츠를 여기에 넣으세요 */}
    </div>
  );
}