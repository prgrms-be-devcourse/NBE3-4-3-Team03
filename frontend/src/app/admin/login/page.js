"use client";

import Link from "next/link";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginSignupView() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    await requestLogin("http://localhost:8080/api/auth/login/admin", "/admin/management")
  }

  const requestLogin = async (url, destination) => {
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData),
        credentials: "include"
      });

      if (response.ok) {
        router.replace(destination);
      } else if (response.status === 400) {
        alert("회원정보가 일치하지 않습니다.");
      }
    } catch (error) {
      alert("error");
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white flex flex-col items-center justify-center p-8">
      <div className="w-full max-w-[400px] bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg">
        <h2 className="text-3xl font-bold text-center mb-6 dark:text-white">
          관리자 로그인
        </h2>

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
              아이디
            </label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className="w-full px-4 py-2 mt-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
              비밀번호
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="w-full px-4 py-2 mt-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition mt-6"
          >
            로그인
          </button>
        </form>

        <div className="mt-6 pt-6 border-t border-gray-300 dark:border-gray-600">
          <p className="text-center text-sm text-gray-600 dark:text-gray-400">
            © 2025 PC Builder. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}