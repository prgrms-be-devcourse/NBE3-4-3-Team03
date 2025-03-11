"use client";

import Image from "next/image";
import LoginSignupView from "./main/components/login-signup-view";
require('dotenv').config();

export default function Home() {
	const oAuthLoginHandler = (() => {
		return {
			kakao: () => {
				const kakaoAuthUrl = process.env.NEXT_PUBLIC_OAUTH2_KAKAO_AUTH_URL;
				const kakaoClientId = process.env.NEXT_PUBLIC_OAUTH2_KAKAO_CLIENT_ID;
				const kakaoRedirectUrl = process.env.NEXT_PUBLIC_OAUTH2_KAKAO_REDIRECT_URL;
				window.location.href = `${kakaoAuthUrl}?client_id=${kakaoClientId}&redirect_uri=${kakaoRedirectUrl}/customer&response_type=code`;
			},

			naver: () => {
				const naverAuthUrl = process.env.NEXT_PUBLIC_OAUTH2_NAVER_AUTH_URL;
				const naverClientId = process.env.NEXT_PUBLIC_OAUTH2_NAVER_CLIENT_ID;
				const naverRedirectUrl = process.env.NEXT_PUBLIC_OAUTH2_NAVER_REDIRECT_URL;
				window.location.href = `${naverAuthUrl}?client_id=${naverClientId}&redirect_uri=${naverRedirectUrl}/customer&response_type=code&state=RANDOM_STATE`;
			},
		};
	})();

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
        <div style={{ display: 'flex', alignItems: 'center' }}>
            <img
                style={{ width: 150, height: 40, marginRight: 40 }}
                src={'assets/icons/kakao_login_medium_narrow.png'}
                onClick={oAuthLoginHandler.kakao}
                alt='카카오'
            />
            <img
                style={{ width: 150, height: 40 }}
                src={'assets/icons/naver_login_medium_narrow.png'}
                onClick={oAuthLoginHandler.naver}
                alt='네이버'
            />
        </div>
        <p className="text-gray-600 dark:text-gray-400 text-center mt-4">
          로그인하고 나만의 맞춤 PC 견적을 시작해보세요!
        </p>
      </main>
      		<div style={{ width: '100%', textAlign: 'center' }}>
      			<div style={{ marginBottom: 100 }}>
      				<h1 style={{ textAlign: 'center' }}>OAuth 2.0 기반 소셜 로그인</h1>
      			</div>
      			<div
      				style={{
      					flex: 1,
      					flexDirection: 'row',
      					justifyContent: 'center',
      					alignItems: 'center',
      				}}>
      			</div>
      		</div>
      <footer className="text-sm text-center text-gray-500 dark:text-gray-400 p-8">
        © 2025 PC Builder. All rights reserved.
      </footer>
    </div>
  );
}