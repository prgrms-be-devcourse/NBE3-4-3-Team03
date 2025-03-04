"use client"; // Next.js App Router 환경에서 클라이언트 컴포넌트로 선언

import { useState } from "react";
import { useRouter } from "next/navigation"; // next/navigation에서 useRouter 훅 임포트
import { createEstimateRequest } from "../service/service";

export default function EstimateForm() {
    const [purpose, setPurpose] = useState("");
    const [budget, setBudget] = useState("0");
    const [otherRequest, setOtherRequest] = useState("");
    const router = useRouter(); // useRouter 훅 사용

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createEstimateRequest(purpose, parseInt(budget), otherRequest);
            alert("견적 요청이 완료되었습니다!");
            // 입력 필드를 초기화합니다.
            setPurpose("");
            setBudget("0");
            setOtherRequest("");

            router.push("/customers/info");
        } catch (error) {
            alert("요청 실패: " + error);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="max-w-2xl mx-auto p-6 bg-white dark:bg-gray-800 rounded-lg shadow-lg">
            <div className="space-y-6">
                <div>
                    <label className="block mb-2 font-medium dark:text-white">목적:</label>
                    <input
                        type="text"
                        value={purpose}
                        onChange={(e) => setPurpose(e.target.value)}
                        required
                        className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
                    />
                </div>
                <div>
                    <label className="block mb-2 font-medium dark:text-white">예산:</label>
                    <input
                        type="number"
                        value={budget}
                        onFocus={(e) => e.target.value === "0" && setBudget("")}
                        onChange={(e) => setBudget(e.target.value)}
                        required
                        step="10000"
                        className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
                    />
                </div>
                <div>
                    <label className="block mb-2 font-medium dark:text-white">기타 요청 사항:</label>
                    <textarea
                        value={otherRequest}
                        onChange={(e) => setOtherRequest(e.target.value)}
                        className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
                        rows={8}
                    />
                </div>
                <button type="submit" className="w-full py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition">
                    견적 요청
                </button>
            </div>
        </form>
    );
}