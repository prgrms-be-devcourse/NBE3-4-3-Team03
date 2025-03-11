'use client';
import { useState, useEffect,useMemo } from 'react';
import { useSearchParams } from 'next/navigation';
import { Suspense } from 'react'
const CategoryItemsComponent= ({category,items:parentItems,handleItemSelect,handleItemPrice:handleItemPrice})=>{
    const [items,setItems] = useState([])
    const [selectedItem,setSelectedItem] = useState(0)
    const [price,setPrice] = useState(0)
    const handleSelectItem = (e)=>{
        const id =parseInt(e.target.value)
        const filtered = items.find(item=>item.id==id)
        if (filtered){
            setSelectedItem(filtered.id)
        }else{
            setSelectedItem(0)
        }
    }
    const handlePrice = (e)=>{
        if (!e.target.value) return;
        const parsed = parseInt(e.target.value)
        if (isNaN(parsed) || !isFinite(parsed)) return;
        if (parsed<0) return;
        setPrice(parsed)
    }

    useEffect(()=>{
        //위의컴포넌트에서parentItems가 변경되면 실행
        setItems(parentItems)
    },[parentItems])

    useEffect(()=>{
        handleItemSelect(category,items.find(item=>item.id==selectedItem))
    },[items,selectedItem])

    useEffect(()=>{
        const item = items.find(item=>item.id==selectedItem)
        if (item){
            handleItemPrice(category,price)
        }else{
            handleItemPrice(category,0)
        }
        
       
        
    },[items,selectedItem,price])
    
    return (
        <div key={category}>
        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              {category}
            </label>
            <select
              value={selectedItem}
              onChange={handleSelectItem}
              className="w-full px-4 py-2 rounded-lg border dark:border-gray-600 dark:bg-gray-700 dark:text-white"
            >
                <option value={0}>부품을 선택하세요</option>
              {items.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.name}
                </option>
              ))}
            </select>
          </div>
          <div className="w-1/3">
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              가격
            </label>
            <input
              type="number"
              defaultValue={price}
              onChange={handlePrice}
            //   onChange={(e) => handleItemChange(category.categoryKey, 'price', e.target.value)}
              className="w-full px-4 py-2 rounded-lg border dark:border-gray-600 dark:bg-gray-700 dark:text-white"
              placeholder="가격을 입력하세요"
            />
          </div>
        </div>
      </div>
    )
}

export default function CreateEstimate() {
  const searchParams = useSearchParams();
  const requestId = searchParams.get('requestId');
  
  const [categories, setCategories] = useState([]);
  const [items, setItems] = useState({});  // 카테고리별 부품 목록
  // 카테고리와 부품 데이터 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        // 카테고리 데이터 가져오기
          const categoryResponse = await fetch('http://localhost:8080/api/admin/categories', {
              credentials: 'include'
          });
        if (!categoryResponse.ok) throw new Error('카테고리 데이터를 가져오는데 실패했습니다');
        const categoryData = await categoryResponse.json();
        
        setCategories(categoryData);

        // 부품 데이터 가져오기
        const itemsResponse = await fetch('http://localhost:8080/api/admin/items', {
            credentials: 'include'
        });
        if (!itemsResponse.ok) throw new Error('부품 데이터를 가져오는데 실패했습니다');
        const itemsData = await itemsResponse.json();
        const itemsByCategory =Object.groupBy(itemsData,(item)=>item.categoryName)
        
    
        setItems(itemsByCategory);
      } catch (error) {
        console.error('Error:', error);
      }
  
    };
    
    fetchData();
  }, []);

  // URL 파라미터에서 직접 데이터를 가져옵니다
  const [requestInfo, setRequestInfo] = useState({
    estimateId: searchParams.get('estimateId'),
    customerName: searchParams.get('customerName'),
    budget: searchParams.get('budget'),
    purpose: searchParams.get('purpose'),
    createDate: searchParams.get('createDate'),
    
  });

  const [estimateData, setEstimateData] = useState([]);

  const handleItemSelect = (category,item)=>{
    if (!category)return
    setEstimateData(items=>{
        const prev ={...items}
        if (item==null || item==undefined){prev[category]=null}
        else{prev[category]=item}
        return prev
    })

  }
  const handleItemPrice = (category,price)=>{
    setEstimateData(items=>{
        const prev = {...items}
        if (prev[category]==null)return prev
        prev[category].price = price
        return prev
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    // 견적 데이터 구성
    const estimateSubmitData = {
      estimateId: requestInfo.estimateId,
      items: Object.values(estimateData)
        .filter(item => item !== null)
        .map(item => ({
          itemId: item.id,
          price: item.price || 0
        }))
    };

    console.log('Submit Data:', estimateSubmitData); // 제출되는 데이터 확인

    try {
      const response = await fetch('http://localhost:8080/api/estimate', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(estimateSubmitData),
      });

      if (!response.ok) throw new Error('견적 저장에 실패했습니다');
      
      // 성공 시 판매자 페이지로 리다이렉트
      window.location.href = '/sellers/info';
    } catch (error) {
      console.error('Error:', error);
      alert('견적 저장에 실패했습니다.');
    }
  };


    const totalPrice = useMemo(()=>{
    return Object.values(estimateData).reduce((acc,item)=>{
        if (!item)return acc
        if (!item.price)return acc
        return acc+item.price},0)
    },[estimateData])
  return (
    <div className="min-h-screen p-8 dark:bg-gray-900">
      <h1 className="text-2xl font-bold mb-8 dark:text-white">PC 견적 수정</h1>
      
      
      {/* 견적 요청 정보 섹션 */}
      {requestInfo && (
        <div className="max-w-2xl mx-auto mb-8">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold mb-4 dark:text-white">견적 요청 정보</h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">요청자</p>
                <p className="dark:text-white">{requestInfo.customerName}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">예산</p>
                <p className="dark:text-white">{requestInfo.budget}원</p>
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">용도</p>
                <p className="dark:text-white">{requestInfo.purpose}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">요청일</p>
                <p className="dark:text-white">{requestInfo.createDate ? new Date(requestInfo.createDate).toLocaleString('ko-KR', { 
                  year: 'numeric', 
                  month: '2-digit', 
                  day: '2-digit', 
                  hour: '2-digit', 
                  minute: '2-digit' 
                }) : ''}</p>
              </div>
              {requestInfo.description && (
                <div className="col-span-2">
                  <p className="text-sm text-gray-500 dark:text-gray-400">상세 요청사항</p>
                  <p className="dark:text-white whitespace-pre-wrap">{requestInfo.description}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="max-w-2xl mx-auto">
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 space-y-6">
            {Object.entries(items).map(([key,items])=>
                (<CategoryItemsComponent 
                key={key} category={key} items={items} 
                handleItemSelect={handleItemSelect}
                handleItemPrice={handleItemPrice}
                />))}

          {/* 총 견적금액 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              총 견적금액
            </label>
            <input
              type="text"
              readOnly
              value={totalPrice}
              className="w-full px-4 py-2 rounded-lg border dark:border-gray-600 dark:bg-gray-700 dark:text-white"
            />
          </div>

          {/* 견적 제출 버튼 */}
          <div className="flex justify-end gap-4">
            <button
              type="button"
              onClick={()=>{
                window.location.href='/sellers/info'
              }}
              className="px-6 py-2 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
            >
              취소
            </button>
            <button
              type="submit"
              className="px-6 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
            >
              견적 수정
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}