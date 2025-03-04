"use client";

import Link from "next/link";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginSignupView() {
  const router = useRouter();
  const [loginType, setLoginType] = useState("CUSTOMER");
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });

  const handleLoginTypeChange = (type) => {
    setLoginType(type);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    if (loginType === "SELLER") {
      await requestLogin("http://localhost:8080/api/auth/login/seller", "/sellers/info")
    } else if (loginType === "CUSTOMER") {
      await requestLogin("http://localhost:8080/api/auth/login/customer", "/customers/info")
    }
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
    <div className="flex justify-between mb-6">
      <div>
        <button
          onClick={() => handleLoginTypeChange("CUSTOMER")}
          className={`w-1/2 py-2 rounded-lg focus:outline-none p-4 ${
            loginType === "CUSTOMER"
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700"
          }`}
        >
          구매자 로그인
        </button>
        <button
          onClick={() => handleLoginTypeChange("SELLER")}
          className={`w-1/2 py-2 rounded-lg focus:outline-none p-4 ${
            loginType === "SELLER"
              ? "bg-green-600 text-white"
              : "bg-gray-200 text-gray-700"
          }`}
        >
          판매자 로그인
        </button>

        <form onSubmit={handleLogin}>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-600">
              아이디
            </label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className="w-full mt-2 px-4 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-600">
              비밀번호
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="w-full mt-2 px-4 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none"
          >
            로그인
          </button>
        </form>

        <div className="my-4 border-t border-gray-300"></div>

        <div className="text-center">
          {loginType === "CUSTOMER" ? (
            <Link href="/signup/customer">
              <span className="text-blue-600 hover:underline cursor-pointer">
                구매자 회원가입
              </span>
            </Link>
          ) : (
            <Link href="/signup/seller">
              <span className="text-blue-600 hover:underline cursor-pointer">
                판매자 회원가입
              </span>
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}