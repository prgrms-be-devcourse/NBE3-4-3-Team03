'use client';
import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from "next/navigation";

export default function MyPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('profile');
  const [selectedQuote, setSelectedQuote] = useState(null);
  const [selectedQuoteForComment, setSelectedQuoteForComment] = useState(null);
  const [commentText, setCommentText] = useState('');
  const [comments, setComments] = useState([]); // 댓글 상태 정의
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [requestedQuotes, setRequestedQuotes] = useState([]);
  const [writtenQuotes, setWrittenQuotes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deliveryInfo, setDeliveryInfo] = useState({});
  const [sellerInfo, setSellerInfo] = useState({
    id: '',
    username: '',
    companyName: '',
    email: ''
  });



  useEffect(() => {
    getSellerInfo()
  }, [])

  useEffect(() => {
    const fetchDeliveryInfo = async () => {
      try {
        const response = await fetch('http://localhost:8080/delivery', {
          credentials: 'include'
        });
        if (true) {
          const data = await response.json();
          // deliveryId를 key로 하는 객체로 변환
          const deliveryMap = {};
          data.forEach(delivery => {
            deliveryMap[delivery.estimateId] = delivery;
          });
          setDeliveryInfo(deliveryMap);
        }
      } catch (error) {
        console.error('배송 정보 조회 실패:', error);
      }
    };

    if (activeTab === 'written') {
      fetchDeliveryInfo();
    }
  }, [activeTab]);

  useEffect(() => {
    if (selectedQuoteForComment) fetchComments(selectedQuoteForComment.id);
  }, [selectedQuoteForComment]);

  useEffect(() => {
    if (selectedQuoteForComment?.id) {
      fetchComments(selectedQuoteForComment.id);
    }
  }, [selectedQuoteForComment]);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        if (activeTab === 'requested') {
          const response = await fetch('http://localhost:8080/estimate/request', {
            credentials: 'include'
          });
          if (!response.ok) throw new Error('견적 데이터를 불러오는데 실패했습니다');
          const data = await response.json();
          console.log('요청받은 견적 데이터:', data);
          setRequestedQuotes(data);
        } else if (activeTab === 'written') {
          const response = await fetch(`http://localhost:8080/api/estimate/seller`, {
            credentials: 'include'
          });
          if (!response.ok) throw new Error('작성한 견적 데이터를 불러오는데 실패했습니다');
          const data = await response.json();
          console.log('작성한 견적 데이터:', data);
          setWrittenQuotes(data);
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };
    if (activeTab === 'requested' || activeTab === 'written') {
      fetchData();
    }
  }, [activeTab]);


  const getStatusStyle = (status) => {
    switch(status) {
      case 'ADOPT':
      case 'Adopt':
        return {
          bgColor: 'bg-green-100 dark:bg-green-900',
          textColor: 'text-green-800 dark:text-green-200',
          text: '채택됨'
        };
      case 'ORDER_COMPLETED':
        return {
          bgColor: 'bg-green-100 dark:bg-green-900',
          textColor: 'text-green-800 dark:text-green-200',
          text: '주문 완료'
        };
      case 'IN_DELIVERY':
        return {
          bgColor: 'bg-green-100 dark:bg-green-900',
          textColor: 'text-green-800 dark:text-green-200',
          text: '배송중'
        };
      case 'WAIT':
      case 'Wait':
        return {
          bgColor: 'bg-blue-100 dark:bg-blue-900',
          textColor: 'text-blue-800 dark:text-blue-200',
          text: '대기중'
        };
      default:
        return {
          bgColor: 'bg-gray-100 dark:bg-gray-700',
          textColor: 'text-gray-800 dark:text-gray-200',
          text: '상태 없음'
        };
    }
  };

  const getSellerInfo = () => {
    fetch("http://localhost:8080/seller", {
      method: "GET",
      credentials: "include",
    })
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        setSellerInfo(data);
      });
  }

  const handleLogout = async (e) => {
    e.preventDefault();
    const response = await fetch("http://localhost:8080/api/auth/logout", {
      method: 'POST',
      credentials: 'include'
    });

    if (response.ok) {
      router.replace("/");
    }
  }


// ✅ 댓글 가져오기 (GET /api/estimates/comments/{estimateId})
const fetchComments = async (estimateId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/estimates/comments/${estimateId}`, {
      credentials: 'include'
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      console.error('서버 응답:', errorData);
      setComments([]);
      return;
    }

    const data = await response.json();
    console.log('받은 댓글 데이터:', data);
    
    const commentsWithType = data.map(comment => ({
      ...comment,
      type: comment.type || 'CUSTOMER'
    }));
    
    setComments(commentsWithType);
  } catch (error) {
    console.error('댓글 불러오기 실패:', error);
    setComments([]);
  }
};

  // ✅ 댓글 전송 (POST /api/estimates/comments)
  const handleSendComment = async () => {
    if (!commentText.trim()) {
      alert('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      console.log('현재 선택된 견적:', selectedQuoteForComment);
      console.log('현재 판매자 정보:', sellerInfo);

      if (!selectedQuoteForComment?.id) {
        console.error('견적 ID 누락:', selectedQuoteForComment);
        throw new Error('견적 정보가 없습니다.');
      }

      // CommentCreateRequest DTO 형식에 맞춰 데이터 구성
      const newComment = {
        estimateId: parseInt(selectedQuoteForComment.id),
        customerId: parseInt(sellerInfo.id),
        content: commentText,
        createDate: new Date().toISOString(),
        type: window.location.pathname.includes('/sellers') ? 'SELLER' : 'CUSTOMER'  // type 필드 추가
      };

      console.log('전송할 댓글 데이터:', newComment);

      const response = await fetch('http://localhost:8080/api/estimates/comments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(newComment),
      });

      if (!response.ok) {
        const errorData = await response.json();
        console.error('서버 응답 상세:', errorData);
        throw new Error(errorData.message || '댓글 전송에 실패했습니다.');
      }

      const result = await response.json();
      console.log('댓글 전송 성공:', result);

      setCommentText('');
      await fetchComments(selectedQuoteForComment.id);
    } catch (error) {
      console.error('댓글 전송 실패:', error);
      alert(error.message);
    }
  };

  // 날짜 포맷팅 함수 추가
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  };

  return (
      <div className="min-h-screen p-8 dark:bg-gray-900">
        <h1 className="text-2xl font-bold mb-8 dark:text-white">판매자 페이지</h1>
        {/* 탭 메뉴 */}
        <div className="flex gap-4 mb-8 border-b dark:border-gray-700">
          <button
              className={`pb-2 px-4 ${activeTab === 'profile' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
              onClick={() => setActiveTab('profile')}
          >
            회원정보
          </button>
          <button
              className={`pb-2 px-4 ${activeTab === 'requested' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
              onClick={() => setActiveTab('requested')}
          >
            요청 받은 견적
          </button>
          <button
              className={`pb-2 px-4 ${activeTab === 'written' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
              onClick={() => setActiveTab('written')}
          >
            작성한 견적
          </button>
        </div>

        {/* 회원정보 탭 */}
        {activeTab === 'profile' && (
            <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
              <div className="grid grid-cols-2 gap-4">
                <div className="text-gray-600 dark:text-gray-400">아이디</div>
                <div className="dark:text-white">{sellerInfo.username}</div>
                <div className="text-gray-600 dark:text-gray-400">이름</div>
                <div className="dark:text-white">{sellerInfo.companyName}</div>
                <div className="text-gray-600 dark:text-gray-400">이메일</div>
                <div className="dark:text-white">{sellerInfo.email}</div>
              </div>
              <button className="mt-6 text-blue-500 hover:text-blue-400 dark:text-blue-400 dark:hover:text-blue-300">
                회원정보 수정
              </button>
              <div>
                <button onClick={handleLogout}
                        className="mt-6 text-blue-500 hover:text-blue-400 dark:text-blue-400 dark:hover:text-blue-300">
                  로그아웃
                </button>
              </div>
            </div>
        )}
        {/* 요청한 견적 탭 */}
        {activeTab === 'requested' && (
            <div>
              {isLoading ? (
                  <div className="text-center py-8 dark:text-white">로딩중...</div>
              ) : error ? (
                  <div className="text-center py-8 text-red-500">{error}</div>
              ) : (
                  <div className="space-y-8">
                    {requestedQuotes.length === 0 ? (
                        <div className="text-center py-8 dark:text-white">요청받은 견적이 없습니다.</div>
                    ) : (
                        requestedQuotes.map(quote => {
                          const statusStyle = getStatusStyle(quote.status);
                          return (
                              <div key={quote.id} className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
                                <div className="flex justify-between items-center mb-4">
                                  <div className="flex items-center gap-3">
                                    <span className="text-lg font-semibold dark:text-white">견적 요청 #{quote.id}</span>
                                    <span className={`px-2 py-1 rounded-full text-sm ${statusStyle.bgColor} ${statusStyle.textColor}`}>
                              {statusStyle.text}
                            </span>
                                  </div>
                                  {(quote.status === 'Wait') && (
                                      <Link
                                          href={{
                                            pathname: '/estimate/create',
                                            query: {
                                              requestId: quote.id,
                                              customerName: quote.customerId,
                                              budget: quote.budget,
                                              purpose: quote.purpose,
                                              createDate: quote.createDate,
                                            }
                                          }}
                                          className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-colors"
                                      >
                                        견적 작성하기
                                      </Link>
                                  )}
                                </div>
                                <div className="grid grid-cols-2 gap-2 text-sm mb-6">
                                  <div className="text-gray-600 dark:text-gray-400">요청자</div>
                                  <div className="dark:text-white">{quote.customerId}</div>
                                  <div className="text-gray-600 dark:text-gray-400">요청일</div>
                                  <div className="dark:text-white">{new Date(quote.createDate).toLocaleDateString()}</div>
                                  <div className="text-gray-600 dark:text-gray-400">예산</div>
                                  <div className="dark:text-white">{quote.budget}</div>
                                  <div className="text-gray-600 dark:text-gray-400">용도</div>
                                  <div className="dark:text-white">{quote.purpose}</div>
                                </div>
                              </div>
                          );
                        })
                    )}
                  </div>
              )}
            </div>
        )}

        {/* 작성한 견적 탭 */}
        {activeTab === 'written' && (
            <div>
              {isLoading ? (
                  <div className="text-center py-8 dark:text-white">로딩중...</div>
              ) : error ? (
                  <div className="text-center py-8 text-red-500">{error}</div>
              ) : (
                  <div className="space-y-8">
                    {writtenQuotes.length === 0 ? (
                        <div className="text-center py-8 dark:text-white">작성한 견적이 없습니다.</div>
                    ) : (
                        writtenQuotes.map(quote => {
                          console.log('현재 견적:', quote);
                          const delivery = deliveryInfo[quote.id]|| {};
                          const requestStatus = delivery.status || 'Wait';
                          const statusStyle = getStatusStyle(requestStatus);
                          console.log(`견적 ID ${quote.id}의 배송 정보:`, delivery);

                          return (
                              <div key={quote.id} className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
                                <div className="flex justify-between items-center mb-4">
                                  <div className="flex items-center gap-3">
                                    <span className="text-lg font-semibold dark:text-white">견적 #{quote.id}</span>
                                    <span className={`px-2 py-1 rounded-full text-sm ${statusStyle.bgColor} ${statusStyle.textColor}`}>
                            {statusStyle.text}
                          </span>
                                  </div>
                                  <div className="flex gap-2">
                                    <button
                                        className="px-3 py-1 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                                        onClick={() => {
                                          router.push(`/estimate/update?estimateId=${quote.id}&requestId=${quote.estimateRequestId}&customerName=${quote.customer}&budget=${quote.budget}&purpose=${quote.purpose}&createDate=${quote.date}`);
                                        }}
                                    >
                                      수정
                                    </button>
                                    <button
                                        className="px-3 py-1 rounded-lg border border-red-300 dark:border-red-800 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30 transition-colors"
                                        onClick={async () => {
                                          if (window.confirm('정말로 이 견적을 삭제하시겠습니까?')) {
                                            try {
                                              const response = await fetch(`http://localhost:8080/api/estimate/${quote.id}`, {
                                                method: 'DELETE',
                                                credentials: 'include'
                                              });

                                              if (!response.ok) {
                                                throw new Error('견적 삭제에 실패했습니다');
                                              }
                                              setWrittenQuotes(prev => prev.filter(q => q.id !== quote.id));
                                            } catch (error) {
                                              console.error('견적 삭제 오류:', error);
                                              alert('견적 삭제 중 오류가 발생했습니다.');
                                            }
                                          }
                                        }}
                                    >
                                      삭제
                                    </button>
                                    <button
                                        className="px-3 py-1 rounded-lg border border-purple-300 dark:border-purple-800 text-purple-600 dark:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/30 transition-colors"
                                        onClick={() => setSelectedQuoteForComment(quote)}
                                    >
                                      문의하기
                                    </button>
                                  </div>
                                </div>
                                <div className="grid grid-cols-2 gap-2 text-sm mb-6">
                                  <div className="text-gray-600 dark:text-gray-400">요청자</div>
                                  <div className="dark:text-white">{quote.customer}</div>
                                  <div className="text-gray-600 dark:text-gray-400">요청일</div>
                                  <div className="dark:text-white">{new Date(quote.date).toLocaleDateString()}</div>
                                  <div className="text-gray-600 dark:text-gray-400">용도</div>
                                  <div className="dark:text-white">{quote.purpose}</div>
                                  <div className="text-gray-600 dark:text-gray-400">예산</div>
                                  <div className="dark:text-white">{quote.budget.toLocaleString()}원</div>
                                  <div className="text-gray-600 dark:text-gray-400">총 견적금액</div>
                                  <div className="dark:text-white">{quote.totalPrice.toLocaleString()}원</div>
                                  <div className="text-gray-600 dark:text-gray-400">배송 주소</div>
                                  <div className="dark:text-white">{delivery?.address || '배송 정보 없음'}</div>
                                </div>
                                <div className="border dark:border-gray-700 rounded-lg p-4">
                                  <h4 className="font-medium mb-3 dark:text-white">견적 구성</h4>
                                  <div className="grid grid-cols-2 gap-2 text-sm">
                                    {Object.entries(quote.items).map(([key, value]) => (
                                        <div key={key} className="col-span-2 grid grid-cols-2">
                                          <div className="text-gray-600 dark:text-gray-400">{key}</div>
                                          <div className="dark:text-white">{value}</div>
                                        </div>
                                    ))}
                                  </div>
                                </div>
                              </div>
                          );
                        })
                    )}
                  </div>
              )}
            </div>
        )}

      {selectedQuote && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-semibold dark:text-white">견적 상세정보</h3>
              <button
                onClick={() => setSelectedQuote(null)}
                className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
              >
                ✕
              </button>
            </div>

            <div className="space-y-6">
              <div className="bg-gray-50 dark:bg-gray-700 p-4 rounded-lg">
                <div className="flex justify-between items-center">
                  <div>
                    <span className="font-medium dark:text-white text-lg">{selectedQuote.seller}</span>
                    <div className="text-sm text-gray-500 dark:text-gray-400 mt-1">견적 받은 날짜: {selectedQuote.date}</div>
                  </div>
                  <span className="px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-300">
                    {selectedQuote.status}
                  </span>
                    </div>
                  </div>

                  <div className="border dark:border-gray-700 rounded-lg overflow-hidden">
                    <h4 className="font-medium p-4 bg-gray-50 dark:bg-gray-700 dark:text-white border-b dark:border-gray-600">
                      견적 구성 부품
                    </h4>
                    <div className="divide-y dark:divide-gray-700">
                      {Object.entries(selectedQuote.parts).map(([part, name]) => (
                          <div key={part} className="p-4 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                            <div className="grid grid-cols-[140px_1fr] gap-4 items-center">
                              <div className="text-gray-600 dark:text-gray-400 font-medium">
                                {part === 'cpu' ? 'CPU' :
                                    part === 'motherboard' ? '메인보드' :
                                        part === 'memory' ? '메모리' :
                                            part === 'storage' ? '저장장치' :
                                                part === 'gpu' ? '그래픽카드' :
                                                    part === 'case' ? '케이스' :
                                                        part === 'power' ? '파워' : part}
                              </div>
                              <div className="dark:text-white">{name}</div>
                            </div>
                          </div>
                      ))}
                    </div>
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-700 p-4 rounded-lg">
                    <div className="flex justify-between items-center mb-4">
                      <span className="font-medium dark:text-white">총 견적금액</span>
                      <span className="text-xl font-semibold text-blue-600 dark:text-blue-400">
                    {selectedQuote.totalPrice}
                  </span>
                </div>

                <div className="flex justify-end gap-3 mt-4">
                  <button
                    onClick={() => setSelectedQuote(null)}
                    className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                  >
                    닫기
                  </button>
                  <button
                    className="px-4 py-2 rounded-lg bg-green-600 text-white hover:bg-green-700 transition-colors"
                    onClick={() => {
                      setShowConfirmModal(true);
                    }}
                  >
                    견적 채택하기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {selectedQuoteForComment && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-semibold dark:text-white">문의하기</h3>
              <button
                onClick={() => setSelectedQuoteForComment(null)}
                className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
              >
                ✕
              </button>
            </div>

            {/* 댓글 입력 영역 */}
            <div className="flex gap-2 mb-6 border-b pb-4 dark:border-gray-700">
              <input
                type="text"
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendComment();
                  }
                }}
                placeholder="문의사항을 입력하세요..."
                className="flex-grow px-4 py-2 border dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
              />
              <button
                onClick={handleSendComment}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                전송
              </button>
            </div>

            {/* 댓글 목록 */}
            <div className="space-y-2">
              {[...comments]
                .sort((a, b) => new Date(a.createDate) - new Date(b.createDate))
                .map((comment) => (
                  <div key={comment.id}
                    className={`border-b dark:border-gray-700 pb-2 ${
                      comment.type === 'SELLER' 
                        ? 'bg-blue-50 dark:bg-blue-900/20' 
                        : 'bg-green-50 dark:bg-green-900/20'
                    } p-3 rounded-lg`}
                  >
                    <div className="flex items-center gap-2 text-sm mb-1">
                      <span className={`font-semibold px-2 py-1 rounded-full text-white ${
                        comment.type === 'SELLER' 
                          ? 'bg-blue-500 dark:bg-blue-600' 
                          : 'bg-green-500 dark:bg-green-600'
                      }`}>
                        {comment.type === 'SELLER' ? '판매자' : '구매자'}
                      </span>
                      <span className="text-gray-500 dark:text-gray-400">
                        {formatDate(comment.createDate)}
                      </span>
                    </div>
                    <div className="pl-2 dark:text-white mt-2">
                      {comment.content}
                    </div>
                  </div>
                ))}
            </div>

            {comments.length === 0 && (
              <div className="text-center text-gray-500 dark:text-gray-400 py-4">
                아직 댓글이 없습니다.
              </div>
            )}
          </div>
        </div>
      )}

        {/* 채택 확인 모달 */}
        {showConfirmModal && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
              <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-sm w-full">
                <h3 className="text-lg font-semibold mb-4 dark:text-white">견적 채택</h3>
                <p className="text-gray-600 dark:text-gray-300 mb-6">견적을 채택하시겠습니까?</p>
                <div className="flex justify-end gap-3">
                  <button
                      onClick={() => setShowConfirmModal(false)}
                      className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300"
                  >
                    아니오
                  </button>
                  <button
                      onClick={() => {
                        // 채택 로직 구현
                        setShowConfirmModal(false);
                        setSelectedQuote(null);
                      }}
                      className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
                  >
                    예
                  </button>
                </div>
              </div>
            </div>
        )}
      </div>
  );
}

