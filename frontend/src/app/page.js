import Image from "next/image";
import LoginSignupView from "./main/components/login-signup-view";

export default function Home() {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white">
      <main className="flex flex-col gap-8 items-center p-8">
        {/* <Image
          className="dark:invert"
          src="/pc-builder-logo.svg"
          alt="PC Builder 로고"
          width={200}
          height={80}
          priority
        /> */}
        
        <h1 className="text-3xl font-bold text-center mb-8">
          나만의 맞춤 PC 견적
        </h1>

        <LoginSignupView />

        <p className="text-gray-600 dark:text-gray-400 text-center mt-4">
          로그인하고 나만의 맞춤 PC 견적을 시작해보세요!
        </p>
      </main>
      
      <footer className="text-sm text-center text-gray-500 dark:text-gray-400 p-8">
        © 2025 PC Builder. All rights reserved.
      </footer>
    </div>
  );
}