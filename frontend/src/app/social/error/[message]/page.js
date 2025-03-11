"use client";  // Mark this file as a client component

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function CustomerSignup({ params }) {
  const router = useRouter();
  const [message, setMessage] = useState(null);

  useEffect(() => {
    const fetchMessage = async () => {
      const resolvedParams = await params;
      const decodedMessage = decodeURIComponent(resolvedParams.message);
      setMessage(decodedMessage);
    };

    fetchMessage();
  }, [params]);

  useEffect(() => {
    if (message) {
      alert(message);
    }
    router.push('http://localhost:3000');
  }, [message]);

  return (
    <div>
      <h1>{message ? message : 'Loading message...'}</h1>
    </div>
  );
}