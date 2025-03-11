'use client';
import { useState, useEffect, useRef } from 'react';
import Link from 'next/link';
import { useRouter } from "next/navigation";
import { Client } from '@stomp/stompjs';

export default function MyPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('profile');
  const [selectedQuote, setSelectedQuote] = useState(null);

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
  const [stompClient, setStompClient] = useState(null);
  const [chatMessages, setChatMessages] = useState([]);
  const [chatInput, setChatInput] = useState('');
  const [chatConnectionStatus, setChatConnectionStatus] = useState('대기 중');
  const [chatError, setChatError] = useState(null);
  const messagesEndRef = useRef(null);



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

  // 판매자 정보 가져오기
      useEffect(() => {
        getSellerInfo();
      }, []);

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
          const response = await fetch(`http://localhost:8080/api/estimate/seller/${sellerInfo.id}`, {
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
  }, [activeTab, sellerInfo.id]);

  // SSE 연결
  useEffect(() => {
    const eventSource = new EventSource(`/sse/seller?username=${sellerInfo.username}`);

    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);
      handleEvent(data);
    };

    eventSource.onerror = (error) => {
      console.error('EventSource failed: ', error);
      setChatError('SSE 연결 실패');
    };

    return () => {
      eventSource.close();
    };
  }, [sellerInfo.username]);

  // SSE 이벤트 처리
  const handleEvent = (data) => {
    if (data.eventName === 'createEstimateRequest') {
      // 견적 요청 이벤트 처리
      console.log('견적요청이 도착했습니다:', data.message);
      setRequestedQuotes(prevState => [...prevState, data.message]); // 실시간 견적 요청 추가
    } else if (data.eventName === 'adopt') {
      // 견적 채택 이벤트 처리
      console.log('작성한 견적이 채택됐습니다:', data.message);
      setWrittenQuotes(prevState => prevState.map(quote =>
          quote.id === data.estimateId ? { ...quote, status: '채택됨' } : quote
      )); // 실시간 견적 상태 업데이트
    }
  };


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

  // 메시지 목록이 업데이트될 때마다 스크롤을 아래로 이동
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [chatMessages]);

  // 채팅 연결 설정
  const connectToChat = (estimateId) => {
    if (stompClient) {
      stompClient.deactivate();
    }
    console.log('채팅 연결 설정 시도:', estimateId);

    setChatConnectionStatus('연결 시도 중...');
    
    const client = new Client({
      brokerURL: 'ws://localhost:8080/chat',
      connectHeaders: {
        'accept-version': '1.2',
      },
      debug: (str) => {
        console.log('STOMP 디버그:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 5000,
      heartbeatOutgoing: 5000,
      onConnect: (frame) => {
        setChatConnectionStatus('연결됨');
        setChatError(null);
        
        // 채팅방 구독 - 견적 ID를 채팅방 ID로 사용
        client.subscribe(`/sub/chat.${estimateId}`, (message) => {
          console.log('메시지 수신:', message.body);
          try {
            const receivedMessage = JSON.parse(message.body);
            setChatMessages((prev) => [...prev, receivedMessage]);
          } catch (error) {
            console.error('메시지 파싱 오류:', error);
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP 에러:', frame);
        setChatError(`STOMP 에러: ${frame.headers?.message || '알 수 없는 오류'}`);
        setChatConnectionStatus('STOMP 에러');
      },
      onWebSocketError: (event) => {
        console.error('웹소켓 에러:', event);
        setChatError('웹소켓 연결 실패. 서버가 실행 중인지 확인하세요.');
        setChatConnectionStatus('웹소켓 에러');
      },
      onDisconnect: () => {
        console.log('웹소켓 연결 종료');
        setChatConnectionStatus('연결 종료');
      },
    });

    setStompClient(client);
    
    try {
      client.activate();
    } catch (error) {
      console.error('웹소켓 활성화 오류:', error);
      setChatError(`연결 오류: ${error.message}`);
      setChatConnectionStatus('활성화 오류');
    }
  };

  // 채팅 메시지 전송
  const sendChatMessage = () => {
    if (!chatInput || !stompClient) {
      return;
    }
    
    try {
      const destination = `/pub/chat.${selectedQuote.id}`;
      const body = JSON.stringify({
        username: sellerInfo.companyName || '판매자',
        content: chatInput
      });
      
      console.log(`메시지 전송 시도: ${destination}`, body);
      
      stompClient.publish({
        destination: destination,
        body: body,
        headers: { 
          'content-type': 'application/json'
        }
      });
      
      console.log('메시지 전송 성공:', chatInput);
      setChatInput('');
    } catch (error) {
      console.error('메시지 전송 실패:', error);
      setChatError(`메시지 전송 실패: ${error.message}`);
    }
  };

  // 문의하기 버튼 클릭 시 호출
  const handleOpenChat = async (quote) => {
    setSelectedQuote(quote);
    setChatMessages([]); // 채팅 메시지 초기화
    
    try {
      // 채팅 기록 가져오기
      const response = await fetch(`http://localhost:8080/api/chat/${quote.id}`, {
        credentials: 'include'
      });
      
      if (response.ok) {
        const chatHistory = await response.json();
        console.log('받은 채팅 기록:', chatHistory);
        
        // 백엔드에서 반환하는 형식에 맞게 처리
        const formattedMessages = [];
        
        // 각 메시지 객체를 처리
        if (Array.isArray(chatHistory)) {
          for (const item of chatHistory) {
            try {
              // ChatMemoryRes 형식으로 변환
              formattedMessages.push({
                username: item.sender,
                content: item.content,
                sendDate: item.sendDate
              });
            } catch (err) {
              console.error('메시지 변환 중 오류:', err, item);
            }
          }
          
          console.log('변환된 메시지:', formattedMessages);
          setChatMessages(formattedMessages);
        } else {
          console.log('채팅 기록이 배열이 아닙니다:', chatHistory);
          // 오류 응답인 경우 빈 메시지 목록 사용
          setChatMessages([]);
        }
      } else {
        // 오류 응답 처리
        console.error('채팅 기록을 가져오는데 실패했습니다:', await response.text());
        setChatMessages([]);
      }
    } catch (error) {
      console.error('채팅 기록 요청 중 오류 발생:', error);
      setChatMessages([]);
    }
    
    // 채팅 연결 설정
    connectToChat(quote.id);
  };

  // 채팅창 닫기
  const handleCloseChat = () => {
    if (stompClient) {
      stompClient.deactivate();
    }
    setSelectedQuote(null);
    setChatMessages([]);
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
                          const delivery = deliveryInfo[quote.id]|| {};
                          const requestStatus = delivery.status || 'Wait';
                          const statusStyle = getStatusStyle(requestStatus);
                        
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
                                          router.push(`/estimate/update?estimateId=${quote.id}&requestId=${quote.estimateRequestId}&customerName=${quote.customerName}&budget=${quote.budget}&purpose=${quote.purpose}&createDate=${quote.createdDate}`);
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
                                        onClick={() => handleOpenChat(quote)}
                                    >
                                      문의하기
                                    </button>
                                  </div>
                                </div>
                                <div className="grid grid-cols-2 gap-2 text-sm mb-6">
                                  <div className="text-gray-600 dark:text-gray-400">요청자</div>
                                  <div className="dark:text-white">{quote.customerName}</div>
                                  <div className="text-gray-600 dark:text-gray-400">요청일</div>
                                  <div className="dark:text-white">{new Date(quote.createdDate).toLocaleDateString()}</div>
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
                                    {quote.items.map((item) => (
                                        <div key={item.categoryName} className="col-span-2 grid grid-cols-2">
                                          <div className="text-gray-600 dark:text-gray-400">{item.categoryName}</div>
                                          <div className="dark:text-white">{item.itemName}</div>
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
              <h3 className="text-xl font-semibold dark:text-white">실시간 문의하기</h3>
              <button
                onClick={handleCloseChat}
                className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
              >
                ✕
              </button>
            </div>

            {/* 연결 상태 표시 */}
            <div className="mb-4 p-2 rounded bg-gray-100 dark:bg-gray-700">
              <div className="flex justify-between items-center">
                <p className="text-sm">연결 상태: {chatConnectionStatus === '연결됨' ? '연결됨 ✅' : chatConnectionStatus}</p>
                {chatConnectionStatus !== '연결됨' && (
                  <button 
                    onClick={() => connectToChat(selectedQuote.id)}
                    className="px-2 py-1 rounded text-xs bg-blue-500 dark:bg-blue-600 text-white"
                  >
                    재연결
                  </button>
                )}
              </div>
              {chatError && (
                <p className="text-red-500 mt-1 text-sm">{chatError}</p>
              )}
            </div>

            {/* 채팅 메시지 영역 */}
            <div className="border dark:border-gray-700 rounded-lg p-4 h-80 overflow-y-auto mb-4">
              {chatMessages.length === 0 ? (
                <div className="text-center text-gray-500 dark:text-gray-400 py-4">
                  아직 메시지가 없습니다. 첫 메시지를 보내보세요!
                </div>
              ) : (
                <div className="space-y-2">
                  {chatMessages.map((msg, index) => (
                    <div 
                      key={index} 
                      className={`p-3 rounded-lg ${
                        msg.username === (sellerInfo.companyName || '판매자')
                          ? 'bg-blue-100 dark:bg-blue-900/30 ml-auto max-w-[60%] text-right'
                          : 'bg-gray-100 dark:bg-gray-700 mr-auto max-w-[60%]'
                      }`}
                    >
                      <div className="font-semibold mb-1">{msg.username}</div>
                      <div>{msg.content}</div>
                      <div className="text-xs text-gray-500 mt-1">{formatDate(msg.sendDate)}</div>
                    </div>
                  ))}
                  <div ref={messagesEndRef} /> {/* 스크롤을 위한 참조 */}
                </div>
              )}
            </div>

            {/* 메시지 입력 영역 */}
            <div className="flex gap-2">
              <input
                type="text"
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendChatMessage();
                  }
                }}
                placeholder="메시지를 입력하세요..."
                className="flex-grow px-4 py-2 border dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
                disabled={chatConnectionStatus !== '연결됨'}
              />
              <button
                onClick={sendChatMessage}
                className={`px-4 py-2 rounded-lg ${
                  chatConnectionStatus !== '연결됨'
                    ? 'bg-gray-300 text-gray-500 dark:bg-gray-600 dark:text-gray-400'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                }`}
                disabled={chatConnectionStatus !== '연결됨'}
              >
                전송
              </button>
            </div>
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