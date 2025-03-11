-- 테스트용 카테고리 데이터
INSERT INTO category (id, category) VALUES (1, 'CPU');
INSERT INTO category (id, category) VALUES (2, 'GPU');
INSERT INTO category (id, category) VALUES (3, 'RAM');

-- 테스트용 아이템 데이터
INSERT INTO item (id, name, img_filename, category_id) VALUES (1, 'Intel i9-13900K', 'intel_i9_13900k.jpg', 1);
INSERT INTO item (id, name, img_filename, category_id) VALUES (2, 'AMD Ryzen 9 7950X', 'amd_ryzen_9_7950x.jpg', 1);
INSERT INTO item (id, name, img_filename, category_id) VALUES (3, 'NVIDIA RTX 4090', 'nvidia_rtx_4090.jpg', 2);
INSERT INTO item (id, name, img_filename, category_id) VALUES (4, 'Corsair 32GB DDR5', 'corsair_32gb_ddr5.jpg', 3);

-- 테스트용 판매자 데이터
INSERT INTO seller (id, username, password, company_Name, email, verification_question, verification_answer, is_verified)
VALUES (1, 'seller1', 'password123', '컴퓨터나라', 'seller1@computer.com', '가장 좋아하는 음식은?', '피자', true);
INSERT INTO seller (id, username, password, company_Name, email, verification_question, verification_answer, is_verified)
VALUES (2, 'seller2', 'password456', '디지털마트', 'seller2@digital.com', '좋아하는 취미는?', '독서', true);

-- 테스트용 구매자 데이터
INSERT INTO customer (id, username, password, customer_Name, email, verification_question, verification_answer)
VALUES (1, 'customer1', 'password123', '맹구', 'customer1@computer.com', '가장 좋아하는 음식은?', '피자');
INSERT INTO customer (id, username, password, customer_Name, email, verification_question, verification_answer)
VALUES (2, 'customer2', 'password456', '신짱구', 'customer2@digital.com', '출신 초등학교는?', '행복초등학교');

-- 테스트용 견적 요청 데이터
INSERT INTO estimate_request (id, purpose, budget, other_request, create_date, customer_id, status)
VALUES (1, '게임용', 2000000, '고사양 게임이 잘 돌아갔으면 좋겠습니다', '2024-03-20 10:30:00', 1, 'REQUESTED');
INSERT INTO estimate_request (id, purpose, budget, other_request, create_date, customer_id, status)
VALUES (2, '사무용', 1000000, '문서 작업이 주 용도입니다', '2024-03-21 11:30:00', 2, 'REQUESTED');
INSERT INTO estimate_request (id, purpose, budget, other_request, create_date, customer_id, status)
VALUES (3, '영상편집용', 3000000, '4K 영상 편집을 주로 합니다', '2024-03-22 14:30:00', 1, 'REQUESTED');

-- 테스트용 견적서 데이터
INSERT INTO estimate (id, estimate_request_id, seller_id, total_price, create_date)
VALUES (1, 1, 1, 2500000, '2024-03-21 10:00:00');
INSERT INTO estimate (id, estimate_request_id, seller_id, total_price, create_date)
VALUES (2, 2, 2, 800000, '2024-03-22 11:00:00');

-- 테스트용 견적서 구성요소 데이터
INSERT INTO estimate_component (id, item_id, price, estimate_id)
VALUES (1, 1, 850000, 1); -- Intel i9-13900K
INSERT INTO estimate_component (id, item_id, price, estimate_id)
VALUES (2, 3, 1500000, 1); -- NVIDIA RTX 4090
INSERT INTO estimate_component (id, item_id, price, estimate_id)
VALUES (3, 4, 150000, 1); -- Corsair 32GB DDR5
INSERT INTO estimate_component (id, item_id, price, estimate_id)
VALUES (4, 2, 650000, 2); -- AMD Ryzen 9 7950X
INSERT INTO estimate_component (id, item_id, price, estimate_id)
VALUES (5, 4, 150000, 2); -- Corsair 32GB DDR5 