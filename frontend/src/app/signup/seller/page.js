'use client'

import { useRef, useState } from "react";
import { useRouter } from 'next/navigation';

export default function SellerSignup() {
  const router = useRouter();
  const businessNumberRef = useRef();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    companyName: '',
    email: '',
    verificationQuestion: '',
    verificationAnswer: '',
    isVerified: ''
  });

  const [isBusinessNumberValid, setIsBusinessNumberValid] = useState(false);

  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value
    }));
  };

  const checkBusinessNumber = async (e) => {
    e.preventDefault();
    const businessNumber = businessNumberRef.current.value;

    const response = await fetch('http://localhost:8080/seller/business/' + businessNumber + '/check', {
      method: 'GET'
    })

    response
        .text()
        .then((result) => {
          if (result === "true") {
            setIsBusinessNumberValid(true);
            setFormData((prevState) => ({
              ...prevState,
              isVerified: true
            }));
          } else {
            setIsBusinessNumberValid(false);
            setFormData((prevState) => ({
              ...prevState,
              isVerified: false
            }));
          }
        })
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      setError('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
      return
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/signup/seller', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        router.back();
      } else if (response.status === 409) {
        alert("아이디 또는 이메일이 이미 존재합니다.");
      }
    } catch (error) {
      alert("error");
    }
  };

  return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-8">
        <div className="w-full max-w-lg bg-white dark:bg-gray-800 shadow-lg rounded-lg p-8">
          <h1 className="text-2xl font-bold text-center mb-4 dark:text-white">회원가입</h1>

          {error && <p className="text-red-500 text-center mb-4">{error}</p>}

          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">아이디:</label>
              <input
                  type="text"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">비밀번호:</label>
              <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">비밀번호 확인:</label>
              <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">회사명:</label>
              <input
                  type="text"
                  name="companyName"
                  value={formData.companyName}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">이메일:</label>
              <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">비밀번호 질문:</label>
              <input
                  type="text"
                  name="verificationQuestion"
                  value={formData.verificationQuestion}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">비밀번호 답변:</label>
              <input
                  type="text"
                  name="verificationAnswer"
                  value={formData.verificationAnswer}
                  onChange={handleChange}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 dark:text-gray-300">사업자 번호 인증:</label>
              <input
                  type="text"
                  name="businessNumber"
                  ref={businessNumberRef}
                  className="w-full p-3 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
              />
              <button
                  onClick={checkBusinessNumber}
                  className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500">인증
              </button>
              <span className={`px-3 text-xl ${isBusinessNumberValid ? 'text-green-500' : 'text-red-500'}`}>
              {isBusinessNumberValid ? 'O' : 'X'}
            </span>
            </div>

            <button
                type="submit"
                disabled={!isBusinessNumberValid}
                className={`w-full p-3 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${!isBusinessNumberValid ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-600'}`}
            >
              회원가입
            </button>
          </form>
        </div>
      </div>
  );
}