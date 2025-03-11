'use client';
import { useState, useEffect, useRef } from 'react';
import Link from 'next/link';
import { useRouter } from "next/navigation";
import { Client } from '@stomp/stompjs';

/**
 *
 * @param {{id:number}} quote
 * @param {({id:number})=>{}} onConfirm
 * @param {({id:number})=>{}} onChat
 * @param {({id:number})=>{}} onSelectQuote
 * @returns
 */
const QuoteComponent = ({quote,onConfirm,onChat,onSelectQuote, onDelete, onEdit})=>{
  const [selected,setSelected] = useState(false)
  const [receivedQuotes,setReceivedQuotes] = useState([])

  // 받은 견적 목록 조회
  useEffect(() => {
    if (!selected)return;
    if (receivedQuotes.length>0)return;
    const fetchReceivedQuotes = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/estimate/estimate-request/${quote.id}`, {
          credentials: 'include'
        });
        if (!response.ok) {
          throw new Error('받은 견적 데이터를 가져오는데 실패했습니다');
        }
        const data = await response.json();
        console.log(data)
        setReceivedQuotes(data);
      } catch (error) {
        console.error('받은 견적 데이터 로딩 오류:', error);
      }
    };
    fetchReceivedQuotes();

  }, [selected]);


    // SSE 연결
    useEffect(() => {
        let eventSource;
        let retryCount = 0;
        const maxRetries = 3;
        const retryInterval = 5000; // 5초

        const connectSSE = () => {
            eventSource = new EventSource(`/sse/customer?estimateRequestId=${quote.id}`);

            eventSource.onmessage = (event) => {
                try {
                    console.log('Received SSE event:', event);
                    if (!event.data) {
                        console.log('No data in event');
                        return;
                    }
                    const data = JSON.parse(event.data);
                    handleEvent(data);
                } catch (error) {
                    console.error('Error parsing SSE event:', error);
                }
            };

            eventSource.addEventListener('createEstimate', (event) => {
                try {
                    console.log('Received createEstimate event:', event);
                    const data = JSON.parse(event.data);
                    console.log('요청하신 견적이 도착했습니다:', data);
                    setReceivedQuotes(prevState => [...prevState, data]);
                } catch (error) {
                    console.error('Error handling createEstimate event:', error);
                }
            });

            eventSource.onerror = (error) => {
                console.error('EventSource failed: ', error);
                eventSource.close();
                
                if (retryCount < maxRetries) {
                    console.log(`재연결 시도 ${retryCount + 1}/${maxRetries}...`);
                    setTimeout(() => {
                        retryCount++;
                        connectSSE();
                    }, retryInterval);
                } else {
                    console.error('최대 재시도 횟수 초과');
                }
            };
        };

        connectSSE();

        return () => {
            if (eventSource) {
                eventSource.close();
            }
        };
    }, [quote.id]);

    // SSE 이벤트 처리
    const handleEvent = (data) => {
        try {
            console.log('Handling event:', data);
            if (data.eventName === 'createEstimate') {
                console.log('요청하신 견적이 도착했습니다:', data.message);
                setReceivedQuotes(prevState => [...prevState, data.message]);
            }
        } catch (error) {
            console.error('Error in handleEvent:', error);
        }
    };

        return (

      <div key={quote.id}
           className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm"
      >
        <div className="flex justify-between items-center mb-4">
   <span className="text-lg font-semibold dark:text-white">
     견적 요청 #{quote.id}

   </span>
          <div className="flex gap-2">
                  {/* 수정/삭제 버튼 추가 */}
                      <>
                          <button
                              onClick={() => onEdit(quote)}
                              className="px-3 py-1 text-blue-600 hover:text-blue-700 transition-colors"
                          >
                              수정
                          </button>
                          <button
                              onClick={() => {
                                  if (window.confirm('정말 삭제하시겠습니까?')) {
                                      onDelete(quote.id);
                                  }
                              }}
                              className="px-3 py-1 text-red-600 hover:text-red-700 transition-colors"
                          >
                              삭제
                          </button>
                      </>
                  <button
                      onClick={() => setSelected(p => !p)}
                      className="px-3 py-1 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                  >
                      {selected ? "닫기" : "열기"}
                  </button>
              </div>
          </div>
        <div className="grid grid-cols-2 gap-2 text-sm mb-6">
          <div className="text-gray-600 dark:text-gray-400">요청일</div>
          <div className="dark:text-white">{new Date(quote.createDate).toLocaleDateString()}</div>
          <div className="text-gray-600 dark:text-gray-400">예산</div>
          <div className="dark:text-white">{quote.budget}</div>
          <div className="text-gray-600 dark:text-gray-400">용도</div>
          <div className="dark:text-white">{quote.purpose}</div>
        </div>
        {/* 받은 견적 목록 */}
        {selected && receivedQuotes.length > 0 && (
            <div className="mt-6 border-t dark:border-gray-700 pt-4">
              <h3 className="text-md font-semibold mb-4 dark:text-white">받은 견적 ({receivedQuotes.length})</h3>
              <div className="space-y-4">
                {receivedQuotes.map(receivedQuote => (
                    <div key={receivedQuote.id} className="bg-gray-50 dark:bg-gray-700 p-4 rounded-lg">
                      <div className="flex justify-between items-center mb-2">
                        <span className="font-medium dark:text-white">{receivedQuote.companyName}</span>
                      </div>
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div className="text-gray-600 dark:text-gray-400">받은날짜</div>
                        <div className="dark:text-white">{new Date(receivedQuote.createdDate).toLocaleDateString()}</div>
                        <div className="text-gray-600 dark:text-gray-400">견적금액</div>
                        <div className="dark:text-white">{receivedQuote.totalPrice}</div>
                      </div>
                      <div className="mt-3 flex gap-2">
                        <button
                            className="text-sm text-blue-500 hover:text-blue-400"
                            onClick={() => onSelectQuote(receivedQuote)}
                        >
                          상세보기
                        </button>
                        <button
                            className="text-sm text-green-600 hover:text-green-500"
                            onClick={()=>onConfirm(receivedQuote)}
                        >
                          채택하기
                        </button>
                        <button
                            className="text-sm text-purple-600 hover:text-purple-500"
                            onClick={()=>onChat(receivedQuote)}
                        >
                          문의하기
                        </button>
                      </div>
                    </div>
                ))}
              </div>
            </div>
        )}
      </div>
  )
}

export default function MyPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('profile');
  const [selectedQuote, setSelectedQuote] = useState(null);
  const [confirmQuote, setConfirmQuote] = useState(null);
  const [requestedQuotes, setRequestedQuotes] = useState([]);
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [editQuote, setEditQuote] = useState(null);
  const [customerInfo, setCustomerInfo] = useState({
    id: '',
    username: '',
    customerName: '',
    email: ''
  });

  // 채팅 관련 상태 추가
  const [stompClient, setStompClient] = useState(null);
  const [chatMessages, setChatMessages] = useState([]);
  const [chatInput, setChatInput] = useState('');
  const [chatConnectionStatus, setChatConnectionStatus] = useState('연결 안됨');
  const [chatError, setChatError] = useState(null);
  const messagesEndRef = useRef(null);

  // 알림 관련 상태 추가
  const [notifications, setNotifications] = useState([]);
  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [eventSource, setEventSource] = useState(null);

  /**
   *
   * @param {{id:number}} quote
   */
  const onSelcectQuote = (quote)=>{
    setSelectedQuote(quote);
  }
  /**
   *
   * @param {{id:number}} quote
   */
  const onConfirm = (quote)=>{
    setConfirmQuote(quote);
  }
  /**
   *
   * @param {{id:number}} quoteId
   */
  const onChat =(quote)=>{
    handleOpenChat(quote);
  }

  useEffect(() => {
    getCustomerInfo();
  }, [])

  // 견적 요청 목록 조회
  useEffect(() => {
    const fetchQuotes = async () => {
      try {
        const response = await fetch('http://localhost:8080/estimate/request', {
          credentials: 'include'
        });
        if (!response.ok) {
          throw new Error('견적 데이터를 가져오는데 실패했습니다');
        }
        const data = await response.json();

        setRequestedQuotes(data);
      } catch (error) {
        console.error('견적 데이터 로딩 오류:', error);
      }
    };

    if (activeTab === 'requested') {
      fetchQuotes();
    }
  }, [activeTab]);

  const getCustomerInfo = () => {
    fetch("http://localhost:8080/customer", {
      method: "GET",
      credentials: "include",
    })
        .then((response) => {
          return response.json();
        })
        .then((data) => {
          setCustomerInfo(data);
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
    // 견적 요청 삭제 함수 추가
    const handleDelete = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/estimate/request/${id}`, {
                method: 'DELETE',
                credentials: 'include',
            });
            if (!response.ok) throw new Error('견적 요청 삭제 실패');

            setRequestedQuotes(prev => prev.filter(quote => quote.id !== id));
            alert('견적 요청이 삭제되었습니다.');
        } catch (error) {
            console.error('견적 요청 삭제 오류:', error);
            alert('견적 요청 삭제 중 오류가 발생했습니다.');
        }
    };

    // 견적 요청 수정 함수 추가
    const handleEdit = async (editedData) => {
        try {
            const response = await fetch(`http://localhost:8080/estimate/request/${editedData.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    purpose: editedData.purpose,
                    budget: editedData.budget,
                    otherRequest: editedData.otherRequest
                })
            });

            if (!response.ok) throw new Error('견적 요청 수정 실패');

            setRequestedQuotes(prev =>
                prev.map(quote =>
                    quote.id === editedData.id ? { ...quote, ...editedData } : quote
                )
            );
            setEditQuote(null);
            alert('견적 요청이 수정되었습니다.');
        } catch (error) {
            console.error('견적 요청 수정 오류:', error);
            alert('견적 요청 수정 중 오류가 발생했습니다.');
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
        username: customerInfo.customerName || '구매자',
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

  // SSE 연결 설정 useEffect 추가
  useEffect(() => {
    if (!customerInfo.username) return;

    const connectSSE = () => {
        const sse = new EventSource('http://localhost:8080/sse/login', {
            withCredentials: true
        });

        sse.onopen = () => {
            console.log('SSE 연결 성공');
        };

        sse.addEventListener('connect', (e) => {
            console.log('SSE 연결됨:', e.data);
        });

        sse.addEventListener('addMessage', (e) => {
            try {
                const notification = JSON.parse(e.data);
                console.log('새 알림:', notification);
                setNotifications(prev => [{
                    id: notification.id,
                    message: notification.message,
                    isRead: notification.isRead,
                    createdAt: notification.createdAt
                }, ...prev]);
                playNotificationSound();
            } catch (error) {
                console.error('알림 처리 중 오류:', error);
            }
        });

        sse.addEventListener('unreadMessage', (e) => {
            try {
                const notification = JSON.parse(e.data);
                console.log('읽지 않은 알림:', notification);
                setNotifications(prev => [{
                    id: notification.id,
                    message: notification.message,
                    isRead: notification.isRead,
                    createdAt: notification.createdAt
                }, ...prev]);
            } catch (error) {
                console.error('읽지 않은 알림 처리 중 오류:', error);
            }
        });

        sse.onerror = (error) => {
            console.error('SSE 에러:', error);
            if (sse.readyState === EventSource.CLOSED) {
                console.log('연결 끊김, 재연결 시도...');
                setTimeout(connectSSE, 5000);
            }
        };

        return sse;
    };

    const sse = connectSSE();

    return () => {
        if (sse) {
            sse.close();
        }
    };
}, [customerInfo.username]);

  // 알림음 재생 함수 추가
  const playNotificationSound = () => {
    const context = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = context.createOscillator();
    const gainNode = context.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(context.destination);
    
    oscillator.frequency.value = 440;
    gainNode.gain.value = 0.1;
    
    oscillator.start();
    setTimeout(() => oscillator.stop(), 200);
  };

  // 알림 벨 컴포넌트 추가
  const NotificationBell = () => {
    const unreadCount = notifications.filter(n => !n.isRead).length;

    return (
      <div className="relative">
                      <button
          onClick={() => setIsNotificationOpen(!isNotificationOpen)}
          className="relative p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full"
        >
          <svg 
            className="w-6 h-6 text-gray-700 dark:text-gray-300" 
            fill="none" 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth="2" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
          </svg>
          {unreadCount > 0 && (
            <span className="absolute -top-1 -right-1 bg-red-500 text-white rounded-full w-5 h-5 text-xs flex items-center justify-center">
              {unreadCount}
            </span>
          )}
                  </button>

        {isNotificationOpen && (
          <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-gray-800 rounded-lg shadow-lg z-50">
            <div className="p-4">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold dark:text-white">알림</h3>
                {notifications.length > 0 && (
                      <button 
                    onClick={() => setNotifications([])}
                    className="text-sm text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
                      >
                    모두 지우기
                      </button>
                    )}
                  </div>
              {notifications.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400 text-center py-4">
                  새로운 알림이 없습니다.
                </p>
              ) : (
                <ul className="space-y-2">
                  {notifications.map((notification) => (
                    <li 
                      key={notification.id}
                          className={`p-3 rounded-lg ${
                        notification.isRead 
                          ? 'bg-gray-50 dark:bg-gray-700' 
                          : 'bg-blue-50 dark:bg-blue-900/30'
                      }`}
                    >
                      <p className="text-sm dark:text-white">
                        {notification.message}
                      </p>
                      <span className="text-xs text-gray-500 dark:text-gray-400 mt-1 block">
                        {formatDate(notification.createdAt)}
                      </span>
                    </li>
                  ))}
                </ul>
              )}
              </div>
            </div>
        )}
      </div>
  );
  };

  return (
    <div className="min-h-screen p-8 dark:bg-gray-900">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl font-bold dark:text-white">구매자 페이지</h1>
        <NotificationBell />
      </div>

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
          요청한 견적
        </button>
      </div>
        {/* 요청한 견적 목록 부분 수정 */}
       

        {/* 수정 모달 추가 */}
        {editQuote && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-md w-full">
                    <h3 className="text-lg font-semibold mb-4 dark:text-white">견적 요청 수정</h3>
                    <form onSubmit={(e) => {
                        e.preventDefault();
                        handleEdit(editQuote);
                    }}>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">용도</label>
                                <input
                                    type="text"
                                    value={editQuote.purpose}
                                    onChange={(e) => setEditQuote({...editQuote, purpose: e.target.value})}
                                    className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">예산</label>
                                <input
                                    type="number"
                                    value={editQuote.budget}
                                    onChange={(e) => setEditQuote({...editQuote, budget: parseInt(e.target.value)})}
                                    className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">상세 요청사항</label>
                                <textarea
                                    value={editQuote.otherRequest}
                                    onChange={(e) => setEditQuote({...editQuote, otherRequest: e.target.value})}
                                    className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                    rows="4"
                                />
                            </div>
                        </div>
                        <div className="flex justify-end gap-3 mt-6">
                            <button
                                type="button"
                                onClick={() => setEditQuote(null)}
                                className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300"
                            >
                                취소
                            </button>
                            <button
                                type="submit"
                                className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
                            >
                                수정
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )}

      {/* 회원정보 탭 */}
      {activeTab === 'profile' && (
          <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
            <div className="grid grid-cols-2 gap-4">
              <div className="text-gray-600 dark:text-gray-400">아이디</div>
              <div className="dark:text-white">{customerInfo.username}</div>
              <div className="text-gray-600 dark:text-gray-400">이름</div>
              <div className="dark:text-white">{customerInfo.customerName}</div>
              <div className="text-gray-600 dark:text-gray-400">이메일</div>
              <div className="dark:text-white">{customerInfo.email}</div>
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
            <div className="space-y-8">
              {requestedQuotes.map(quote => (
                  <QuoteComponent key={quote.id} quote={quote} onConfirm={onConfirm} onChat={onChat} onSelectQuote={onSelcectQuote}onDelete={handleDelete} onEdit={() => setEditQuote(quote)}/>             ))}
            </div>
            <Link href="/estimateRequest">
              <button
                  className="mt-4 px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-colors"
              >
                견적 요청하기
              </button>
            </Link>
          </div>
      )}

      {/* 주문 조회 */}
      <Link href="/delivery">
        <button className="px-4 py-2 hover:text-blue-600 border border-gray-300 rounded-lg hover:border-blue-600 transition-colors">
          주문조회
        </button>
      </Link>

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
                      <span className="font-medium dark:text-white text-lg">{selectedQuote.companyName}</span>
                      <div className="text-sm text-gray-500 dark:text-gray-400 mt-1">견적 받은 날짜: {new Date(selectedQuote.createdDate).toLocaleDateString()}</div>
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
                    {selectedQuote.items.map((item) => (
                        <div key={item.categoryName} className="p-4 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                          <div className="grid grid-cols-[140px_1fr] gap-4 items-center">
                            <div className="text-gray-600 dark:text-gray-400 font-medium">
                              {item.categoryName === 'cpu' ? 'CPU' :
                                  item.categoryName === 'motherboard' ? '메인보드' :
                                      item.categoryName === 'memory' ? '메모리' :
                                          item.categoryName === 'storage' ? '저장장치' :
                                              item.categoryName === 'gpu' ? '그래픽카드' :
                                                  item.categoryName === 'case' ? '케이스' :
                                                      item.categoryName === 'power' ? '파워' : item.categoryName}
                            </div>
                            <div className="dark:text-white">{item.itemName}</div>
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
                          setConfirmQuote(true);
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
                          msg.username === (customerInfo.customerName || '구매자')
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
      {Boolean(confirmQuote) && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-sm w-full">
              <h3 className="text-lg font-semibold mb-4 dark:text-white">견적 채택</h3>
              <div className="mb-4">
                <label className="block text-gray-700 dark:text-gray-300 mb-2">배송 주소</label>
                <input
                    type="text"
                    className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                    placeholder="배송받으실 주소를 입력해주세요"
                    value={deliveryAddress}
                    onChange={(e) => setDeliveryAddress(e.target.value)}
                />
              </div>
              <p className="text-gray-600 dark:text-gray-300 mb-6">
                입력하신 주소로 배송됩니다. 계속하시겠습니까?
              </p>
              <div className="flex justify-end gap-3">
                <button
                    onClick={() => {
                      setConfirmQuote(null);
                      setDeliveryAddress('');
                    }}
                    className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300"
                >
                  아니오
                </button>
                <button
                    onClick={async () => {
                      try {
                        if (!deliveryAddress.trim()) {
                          alert('배송 주소를 입력해주세요.');
                          return;
                        }

                        const response = await fetch(`http://localhost:8080/delivery?id=${confirmQuote.id}`, {
                          method: 'POST',
                          headers: {
                            'Content-Type': 'application/json',
                          },
                          credentials: 'include',
                          body: JSON.stringify({
                            address: deliveryAddress
                          })
                        });

                        if (!response.ok) {
                          throw new Error('배송 요청 실패');
                        }

                        const responseText = await response.text();
                        alert(responseText || '견적이 채택되었습니다.');
                        setConfirmQuote(null);
                        setDeliveryAddress('');

                        // 견적 목록 새로고침
                        if (activeTab === 'requested') {
                          const quotesResponse = await fetch('http://localhost:8080/estimate/request', {
                            credentials: 'include'
                          });
                          if (!quotesResponse.ok) {
                            throw new Error('견적 데이터를 가져오는데 실패했습니다');
                          }
                          const quotesData = await quotesResponse.json();
                          setRequestedQuotes(quotesData);
                        }
                      } catch (error) {
                        console.error('배송 요청 오류:', error);
                        alert('견적 채택 중 오류가 발생했습니다.');
                      }
                    }}
                    className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
                    disabled={!deliveryAddress.trim()}
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