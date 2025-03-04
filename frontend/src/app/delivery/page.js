'use client';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

export default function DeliveryPage() {
    const [deliveries, setDeliveries] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const router = useRouter();

    // 배송 목록 조회
    useEffect(() => {
        fetchDeliveries();
    }, []);

    const fetchDeliveries = async () => {
        try {
            setIsLoading(true);
            const response = await fetch('http://localhost:8080/delivery', {
                headers: {
                    'Authorization': `Bearer ${getCookie('apiKey')} ${getCookie('accessToken')} ${getCookie('userType')}`
                },
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                setDeliveries(data || []); // 데이터가 null이나 undefined인 경우 빈 배열로 설정
            } else if (response.status === 404) {
                setDeliveries([]); // 데이터가 없는 경우 빈 배열로 설정
            } else {
                console.log('배송 목록 조회 실패');
                setDeliveries([]); // 실패 시에도 빈 배열로 설정
            }
        } catch (error) {
            console.log('배송 목록 조회 오류:', error);
            setDeliveries([]); // 에러 발생 시에도 빈 배열로 설정
        } finally {
            setIsLoading(false);
        }
    };

    // 배송 정보 수정
    const handleUpdateDelivery = async (id, newAddress) => {
        try {
            const response = await fetch(`http://localhost:8080/delivery/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('apiKey')} ${getCookie('accessToken')} ${getCookie('userType')}`
                },
                credentials: 'include',
                body: JSON.stringify({ address: newAddress })
            });

            if (!response.ok) throw new Error('배송 정보 수정 실패');

            alert('배송 정보가 수정되었습니다.');
            fetchDeliveries(); // 목록 새로고침
        } catch (error) {
            console.error('배송 정보 수정 오류:', error);
            alert('배송 정보 수정에 실패했습니다.');
        }
    };

    // 배송 취소
    const handleDeleteDelivery = async (id) => {
        if (!window.confirm('배송을 취소하시겠습니까?')) return;

        try {
            const response = await fetch(`http://localhost:8080/delivery/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${getCookie('apiKey')} ${getCookie('accessToken')} ${getCookie('userType')}`
                },
                credentials: 'include'
            });

            if (!response.ok) throw new Error('배송 취소 실패');

            alert('배송이 취소되었습니다.');
            fetchDeliveries(); // 목록 새로고침
        } catch (error) {
            console.error('배송 취소 오류:', error);
            alert('배송 취소에 실패했습니다.');
        }
    };

    return (
        <div className="min-h-screen p-8 bg-gray-50 dark:bg-gray-900">
            {/* 헤더 부분 */}
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-white">배송 관리</h1>
                <div className="flex gap-4">
                    <Link href="/customers/info">
                        <button className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 border border-gray-300 dark:border-gray-600 rounded-lg hover:border-blue-600 dark:hover:border-blue-400 transition-colors">
                            마이페이지
                        </button>
                    </Link>
                </div>
            </div>

            {/* 로딩 상태 */}
            {isLoading ? (
                <div className="text-center py-12">
                    <p className="text-gray-500 dark:text-gray-400">배송 목록을 불러오는 중...</p>
                </div>
            ) : (
                <>
                    {/* 배송 목록 */}
                    {deliveries.length > 0 ? (
                        <div className="space-y-4">
                            {deliveries.map((delivery) => (
                                <div key={delivery.id} className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700">
                                    <div className="flex justify-between items-center mb-4">
                                        <span className="text-lg font-semibold text-gray-900 dark:text-white">배송 #{delivery.id}</span>
                                        <div className="space-x-2">
                                            <button
                                                onClick={() => {
                                                    const newAddress = prompt('새로운 배송 주소를 입력하세요:', delivery.address);
                                                    if (newAddress) {
                                                        handleUpdateDelivery(delivery.id, newAddress);
                                                    }
                                                }}
                                                className="text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 transition-colors"
                                            >
                                                수정
                                            </button>
                                            <button
                                                onClick={() => handleDeleteDelivery(delivery.id)}
                                                className="text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 transition-colors"
                                            >
                                                취소
                                            </button>
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-2 gap-4 text-sm">
                                        <div className="text-gray-600 dark:text-gray-400 font-medium">배송 주소</div>
                                        <div className="text-gray-900 dark:text-white">{delivery.address}</div>
                                        <div className="text-gray-600 dark:text-gray-400 font-medium">상태</div>
                                        <div className="text-gray-900 dark:text-white">
                                            <span className="px-2 py-1 bg-blue-100 dark:bg-blue-900 text-blue-600 dark:text-blue-300 rounded-full text-sm">
                                                {delivery.status}
                                            </span>
                                        </div>
                                        <div className="text-gray-600 dark:text-gray-400 font-medium">견적 ID</div>
                                        <div className="text-gray-900 dark:text-white">{delivery.estimateId}</div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        /* 데이터가 없을 경우 */
                        <div className="text-center py-12">
                            <p className="text-gray-500 dark:text-gray-400">아직 배송 내역이 없습니다.</p>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

// 쿠키 헬퍼 함수
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return '';
}