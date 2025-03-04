'use client';
import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from "next/navigation";

/**
 *
 * @param {{id:number}} quote
 * @param {({id:number})=>{}} onConfirm
 * @param {({id:number})=>{}} onComment
 * @param {({id:number})=>{}} onSelectQuote
 * @returns
 */
const QuoteComponent = ({quote,onConfirm,onComment,onSelectQuote, onDelete, onEdit})=>{
  const [selected,setSelected] = useState(false)
  const [receivedQuotes,setReceivedQuotes] = useState([])
  // 받은 견적 목록 조회
  useEffect(() => {
    if (!selected)return;
    if (receivedQuotes.length>0)return;
    const fetchReceivedQuotes = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/estimate/${quote.id}`, {
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
                        <span className="font-medium dark:text-white">{receivedQuote.seller}</span>
                      </div>
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div className="text-gray-600 dark:text-gray-400">받은날짜</div>
                        <div className="dark:text-white">{new Date(receivedQuote.date).toLocaleDateString()}</div>
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
                            onClick={()=>onComment(receivedQuote)}
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
  const [selectedQuoteForComment, setSelectedQuoteForComment] = useState(null);
  const [commentText, setCommentText] = useState('');
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
  const [comments, setComments] = useState([]);

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
  const onComment =(quote)=>{
    setSelectedQuoteForComment(quote)
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

  // 댓글 모달이 열릴 때 댓글 조회
  useEffect(() => {
    if (selectedQuoteForComment?.id) {
      fetchComments(selectedQuoteForComment.id);
    }
  }, [selectedQuoteForComment]);

  // 댓글 조회 함수
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

  // 댓글 전송 함수
  const handleSendComment = async () => {
    if (!commentText.trim()) {
      alert('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      if (!selectedQuoteForComment?.id) {
        console.error('견적 ID 누락:', selectedQuoteForComment);
        throw new Error('견적 정보가 없습니다.');
      }

      const newComment = {
        estimateId: parseInt(selectedQuoteForComment.id),
        customerId: parseInt(customerInfo.id),
        content: commentText,
        createDate: new Date().toISOString(),
        type: 'CUSTOMER'
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

  // 댓글 입력 핸들러
  const handleCommentChange = (e) => {
    setCommentText(e.target.value);
  };

  // Enter 키 입력 처리
  const onKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendComment();
    }
  };

  // 견적 목록에서 댓글 버튼 클릭 시 호출되는 함수
  const handleCommentClick = (quote) => {
    console.log('댓글 버튼 클릭됨:', quote);
    setSelectedQuoteForComment(quote);
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
        <h1 className="text-2xl font-bold mb-8 dark:text-white">구매자 페이지</h1>

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
                    <QuoteComponent key={quote.id} quote={quote} onConfirm={onConfirm} onComment={onComment} onSelectQuote={onSelcectQuote}onDelete={handleDelete} onEdit={() => setEditQuote(quote)}/>             ))}
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
                        <span className="font-medium dark:text-white text-lg">{selectedQuote.seller}</span>
                        <div className="text-sm text-gray-500 dark:text-gray-400 mt-1">견적 받은 날짜: {new Date(selectedQuote.date).toLocaleDateString()}</div>
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
                      {Object.entries(selectedQuote.items).map(([part, name]) => (
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
                    onChange={handleCommentChange}
                    onKeyPress={onKeyPress}
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