'use client';
import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

const ItemModal = ({ isOpen, onClose, onSubmit, item, categories }) => {
    const [name, setName] = useState('');
    const [image, setImage] = useState(null);
    const [categoryId, setCategoryId] = useState('');

    useEffect(() => {
        if (item) {
            setName(item.name);
            setImage(null); // 이미지 초기화
            setCategoryId(item.categoryId);
        }
    }, [item]);

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Image file in submit:', image); // 이미지 파일 상태 로그 추가
        if (onSubmit) {
            onSubmit({ name, image, categoryId });
        }
        onClose();
    };

    return (
        isOpen && (
            <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-full max-w-md">
                    <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-4">{item ? '부품 수정' : '부품 추가'}</h2>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="부품 이름을 입력하세요"
                            className="w-full px-4 py-2 mb-4 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
                            required
                        />
                        <input
                            type="file"
                            onChange={(e) => {
                                const file = e.target.files[0];
                                console.log('Selected file:', file);
                                setImage(file);
                            }}
                            className="w-full px-4 py-2 mb-4 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
                            accept="image/*"
                        />
                        {item && item.filename && (
                            <div className="mb-4">
                                <img
                                    src={`http://localhost:8080/api/image/${item.filename}`}
                                    alt="현재 이미지"
                                    className="w-full h-32 object-cover mb-2 rounded-md"
                                />
                            </div>
                        )}
                        <select
                            value={categoryId}
                            onChange={(e) => setCategoryId(e.target.value)}
                            className="w-full px-4 py-2 mb-4 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-300"
                            required
                        >
                            <option value="" className="dark:bg-gray-700">카테고리 선택</option>
                            {categories.map((category) => (
                                <option key={category.id} value={category.id} className="dark:bg-gray-700">
                                    {category.category}
                                </option>
                            ))}
                        </select>
                        <div className="flex justify-end gap-2">
                            <button
                                type="button"
                                onClick={onClose}
                                className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700"
                            >
                                취소
                            </button>
                            <button 
                                type="submit" 
                                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                            >
                                {item ? '수정' : '추가'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )
    );
};

export default function ItemList() {
    const router = useRouter();
    const [categories, setCategories] = useState([]);
    const [items, setItems] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);
    const [newCategoryName, setNewCategoryName] = useState('');
    const [selectedItems, setSelectedItems] = useState(new Set());
    const [image, setImage] = useState(null); // 이미지 상태 추가

    useEffect(() => {
        fetchCategories();
    }, []);


    const fetchCategories = () => {
        fetch('http://localhost:8080/api/admin/categories',{credentials: "include"})
            .then((response) => response.json())
            .then((data) => {
                if (Array.isArray(data)) {
                    setCategories(data);
                } else {
                    console.error('잘못된 데이터 형식:', data);
                }
            })
            .catch((error) => console.error('카테고리 로딩 실패:', error));
    };

    const fetchItems = (categoryId) => {
        fetch(`http://localhost:8080/api/admin/items?categoryId=${categoryId}`,{credentials: "include"})
            .then((response) => response.json())
            .then((data) => {
                if (Array.isArray(data)) {
                    console.log(data);
                    setItems(data);
                } else {
                    console.error('잘못된 데이터 형식:', data);
                }
            })
            .catch((error) => console.error('부품 로딩 실패:', error));
    };

    const handleCategoryClick = (categoryId) => {
        setSelectedCategory(categoryId);
        fetchItems(categoryId);
    };
    

    const handleAddCategory = () => {
        if (!newCategoryName.trim()) return alert('카테고리 이름을 입력하세요.');
        fetch('http://localhost:8080/api/admin/categories', {
            method: 'POST',
            credentials: "include",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ category: newCategoryName }),
        })
            .then((response) => response.json())
            .then(() => {
                setNewCategoryName('');
                fetchCategories();
            })
            .catch((error) => console.error('카테고리 추가 실패:', error));
    };

    const handleUpdateCategory = () => {
        if (!selectedCategory) return alert('수정할 카테고리를 선택하세요.');
        const newName = prompt('새로운 카테고리 이름을 입력하세요:');
        if (!newName) return;

        fetch(`http://localhost:8080/api/admin/categories/${selectedCategory}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: "include",
            body: JSON.stringify({ category: newName }),
        })
            .then((response) => response.json())
            .then(() => fetchCategories())
            .catch((error) => console.error('카테고리 수정 실패:', error));
    };

    const handleDeleteCategory = () => {
        if (!selectedCategory) return alert('삭제할 카테고리를 선택하세요.');
        if (!confirm('정말로 삭제하시겠습니까?')) return;

        fetch(`http://localhost:8080/api/admin/categories/${selectedCategory}`, {
            method: 'DELETE',
            credentials: "include"
        })
            .then((response) => response.json())
            .then(() => {
                setSelectedCategory(null);
                fetchCategories();
            })
            .catch((error) => console.error('카테고리 삭제 실패:', error));
    };

    const handleAddItem = (newItem) => {
        // 부품 이름이 비어있는지 확인
        if (!newItem.name) {
            return alert('부품 이름을 입력해주세요.'); // 이름 비어 있을 경우 경고 메시지
        }

        // 이미지가 선택되지 않은 경우 경고 메시지 표시
        if (!newItem.image) {
            return alert('이미지를 선택하세요.');
        }

        const formData = new FormData();
        formData.append('name', newItem.name);
        formData.append('categoryId', newItem.categoryId);

        const uuid = crypto.randomUUID(); // UUID 생성
        const extension = newItem.image.name.split('.').pop(); // 파일 확장자 추출
        const filename = `${uuid}.${extension}`; // UUID와 확장자를 결합한 파일 이름

        formData.append('image', new Blob([newItem.image], { type: newItem.image.type }), filename);

        fetch('http://localhost:8080/api/admin/items', {
            method: 'POST',
            credentials: "include",
            body: formData,
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('부품 추가 실패'); // 응답이 성공적이지 않을 경우 에러 처리
                }
                return response.json();
            })
            .then(() => {
                fetchItems(newItem.categoryId); // 아이템 목록 새로고침
            })
            .catch((error) => console.error('부품 추가 실패:', error));
    };

    const handleUpdateItem = (updatedItem) => {
        const formData = new FormData();
        formData.append('name', updatedItem.name);
        formData.append('categoryId', updatedItem.categoryId);

        if (updatedItem.image) {
            // 새 이미지가 있는 경우
            const uuid = crypto.randomUUID(); // UUID 생성
            const extension = updatedItem.image.name.split('.').pop(); // 파일 확장자 추출
            const filename = `${uuid}.${extension}`; // UUID와 확장자를 결합한 파일 이름

            formData.append('image', new Blob([updatedItem.image], { type: updatedItem.image.type }), filename);
        } else {
            // 이미지가 없는 경우 기존 파일 이름 사용
            formData.append('imgFilename', editingItem.filename); // 기존 이미지 파일 이름 사용
        }

        fetch(`http://localhost:8080/api/admin/items/${editingItem.id}`, {
            method: 'PUT',
            credentials: "include",
            body: formData,
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('부품 수정 실패');
                }
                return response.json();
            })
            .then(() => {
                fetchItems(updatedItem.categoryId); // 아이템 목록 새로고침
            })
            .catch((error) => console.error('부품 수정 실패:', error));
    };

    const toggleItemSelection = (itemId) => {
        const updatedSelection = new Set(selectedItems);
        if (updatedSelection.has(itemId)) {
            updatedSelection.delete(itemId);
        } else {
            updatedSelection.add(itemId);
        }
        setSelectedItems(updatedSelection);
    };

    const handleDeleteSelectedItems = () => {
        if (selectedItems.size === 0) return alert('삭제할 부품을 선택하세요.');
        if (!confirm('선택한 부품을 정말로 삭제하시겠습니까?')) return;

        Promise.all(Array.from(selectedItems).map(itemId => {
            return fetch(`http://localhost:8080/api/admin/items/${itemId}`, {
            credentials: "include",
                method: 'DELETE',
            });
        }))
            .then(() => {
                setSelectedItems(new Set());
                fetchItems(selectedCategory);
            })
            .catch((error) => console.error('부품 삭제 실패:', error));
    };

    const openAddModal = () => {
        setEditingItem(null);
        setImage(null); // 이미지 상태 초기화
        setIsModalOpen(true);
    };

    const openEditModal = (item) => {
        setEditingItem(item);
        setImage(null); // 이미지 상태 초기화
        setIsModalOpen(true);
    };

    const crawlItems = () => {
        fetch(`http://localhost:8080/api/crawl-items`, {
            method: 'GET',
            credentials: "include",
        });
    }

    const handleLogout = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/auth/logout", {
                method: 'POST',
                credentials: 'include'
            });

            if (response.ok) {
                router.replace("/admin/login");
            }
        } catch (error) {
            console.error('로그아웃 실패:', error);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white p-8">
            <div className="relative mb-6">
                <div className="absolute right-0">
                    <button
                        onClick={handleLogout}
                        className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                    >
                        로그아웃
                    </button>
                </div>
                <h1 className="text-3xl font-bold dark:text-white text-center">관리자 페이지</h1>
            </div>

            {/* 카테고리 추가, 수정, 삭제 버튼 */}
            <div className="flex justify-center mb-6 gap-2">
                <input
                    type="text"
                    value={newCategoryName}
                    onChange={(e) => setNewCategoryName(e.target.value)}
                    placeholder="새 카테고리 이름"
                    className="px-3 py-1 border border-gray-300 rounded-lg text-sm dark:bg-gray-800 dark:text-white"
                />
                <button
                    onClick={handleAddCategory}
                    className="px-3 py-1 w-28 bg-green-600 text-white rounded-lg text-sm shadow-md hover:bg-green-700 transition"
                >
                    추가
                </button>

                {/* 카테고리 수정 & 삭제 */}
                <button
                    onClick={handleUpdateCategory}
                    className={`px-3 py-1 w-28 rounded-lg text-sm shadow-md transition ${selectedCategory ? "bg-yellow-500 text-white hover:bg-yellow-600" : "bg-gray-300 text-gray-600 cursor-not-allowed"
                        }`}
                    disabled={!selectedCategory}
                >
                    수정
                </button>
                <button
                    onClick={handleDeleteCategory}
                    className={`px-3 py-1 w-28 rounded-lg text-sm shadow-md transition ${selectedCategory ? "bg-red-600 text-white hover:bg-red-700" : "bg-gray-300 text-gray-600 cursor-not-allowed"
                        }`}
                    disabled={!selectedCategory}
                >
                    삭제
                </button>

                <button
                    onClick={crawlItems}
                    className={`px-3 py-1 w-28 rounded-lg text-sm shadow-md transition "bg-gray-600 text-gray-300"
                        }`}
                >
                    부품 크롤링
                </button>
            </div>

            {/* 카테고리 목록 */}
            <div className="flex flex-wrap justify-center gap-4 mb-8">
                {categories.length > 0 ? (
                    categories.map((category) => (
                        <button
                            key={category.id}
                            onClick={() => handleCategoryClick(category.id)}
                            className={`px-6 py-3 rounded-lg text-lg font-medium transition-colors duration-300 shadow-md 
                                ${selectedCategory === category.id ? 'bg-blue-600 text-white' : 'bg-white text-gray-900 border border-gray-300 hover:bg-blue-100 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700'}`}
                        >
                            {category.category}
                        </button>
                    ))
                ) : (
                    <p>카테고리가 없습니다.</p>
                )}
            </div>

            {/* 부품 목록 */}
            {selectedCategory && (
                <div>
                    <h2 className="text-2xl font-semibold text-center mb-4">
                        {categories.find(c => c.id === selectedCategory)?.category} 부품 목록
                    </h2>
                    <div className="flex justify-center mb-4"> {/* 중앙 정렬을 위한 div 추가 */}
                        <button
                            onClick={openAddModal}
                            className="bg-green-500 text-white px-4 py-2 rounded mx-2" // 여백 추가
                        >
                            부품 추가
                        </button>
                        <button
                            onClick={handleDeleteSelectedItems}
                            className="bg-red-500 text-white px-4 py-2 rounded mx-2" // 여백 추가
                        >
                            선택한 부품 삭제
                        </button>
                    </div>
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 p-4">
                        {items.filter(item => item.categoryId === selectedCategory).length > 0 ? (
                            items
                                .filter(item => item.categoryId === selectedCategory)
                                .map((item) => (
                                    <div key={item.id} className="bg-white dark:bg-gray-800 p-4 rounded-lg shadow-lg text-center">
                                        <input
                                            type="checkbox"
                                            checked={selectedItems.has(item.id)}
                                            onChange={() => toggleItemSelection(item.id)}
                                            className="mb-2"
                                        />
                                        <img
                                            src={`http://localhost:8080/api/image/${item.filename}`} // 이미지 URL
                                            alt={item.name}
                                            className="w-full h-32 object-cover mb-2 rounded-md"
                                        />
                                        <p className="text-lg font-medium">{item.name}</p>
                                        <button
                                            onClick={() => openEditModal(item)}
                                            className="mt-2 bg-yellow-500 text-white px-2 py-1 rounded"
                                        >
                                            수정
                                        </button>
                                    </div>
                                ))
                        ) : (
                            <p className="col-span-full text-center">해당 카테고리에 부품이 없습니다.</p>
                        )}
                    </div>
                </div>
            )}

            {/* 모달 */}
            <ItemModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={editingItem ? handleUpdateItem : handleAddItem}
                item={editingItem}
                categories={categories} // 카테고리 목록을 모달에 전달
            />
        </div>
    );
}