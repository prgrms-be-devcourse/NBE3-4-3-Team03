-- 카테고리 데이터 삽입
INSERT INTO category (category)
SELECT 'CPU'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE category = 'CPU');

INSERT INTO category (category)
SELECT 'GPU'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category = 'GPU');

INSERT INTO category (category)
SELECT 'RAM'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category = 'RAM');

-- 아이템 데이터 삽입
INSERT INTO item (name, img_filename, category_id)
SELECT 'Intel i9-13900K', 'intel_i9_13900k.jpg', 1
WHERE NOT EXISTS (SELECT 1 FROM item WHERE name = 'Intel i9-13900K');

INSERT INTO item (name, img_filename, category_id)
SELECT 'AMD Ryzen 9 7950X', 'amd_ryzen_9_7950x.jpg', 1
WHERE NOT EXISTS (SELECT 1 FROM item WHERE name = 'AMD Ryzen 9 7950X');

INSERT INTO item (name, img_filename, category_id)
SELECT 'NVIDIA RTX 4090', 'nvidia_rtx_4090.jpg', 2
WHERE NOT EXISTS (SELECT 1 FROM item WHERE name = 'NVIDIA RTX 4090');

INSERT INTO item (name, img_filename, category_id)
SELECT 'Corsair 32GB DDR5', 'corsair_32gb_ddr5.jpg', 3
WHERE NOT EXISTS (SELECT 1 FROM item WHERE name = 'Corsair 32GB DDR5');

-- 판매자 데이터 삽입
INSERT INTO seller (username, password, company_Name, email, verification_question, verification_answer, is_verified)
SELECT 'seller1', 'password123', '컴퓨터나라', 'seller1@computer.com', '가장 좋아하는 음식은?', '피자', true
WHERE NOT EXISTS (SELECT 1 FROM seller WHERE username = 'seller1');

INSERT INTO seller (username, password, company_Name, email, verification_question, verification_answer, is_verified)
SELECT 'seller2', 'password456', '디지털마트', 'seller2@digital.com', '좋아하는 취미는?', '독서', true
WHERE NOT EXISTS (SELECT 1 FROM seller WHERE username = 'seller2');

-- 구매자 데이터 삽입
INSERT INTO customer (username, password, customer_Name, email, verification_question, verification_answer)
SELECT 'customer1', 'password123', '맹구', 'customer1@computer.com', '가장 좋아하는 음식은?', '피자'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE username = 'customer1');
INSERT INTO customer (username, password, customer_Name, email, verification_question, verification_answer)
SELECT 'customer2', 'password456', '신짱구', 'customer2@digital.com', '출신 초등학교는?', '행복초등학교'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE username = 'customer2');

-- 견적 요청 데이터 삽입
-- '게임용' 견적 요청이 존재하지 않으면 삽입
INSERT INTO estimate_request (purpose, budget, other_request, create_date, customer_id)
SELECT '게임용', 2000000, '고사양 게임이 잘 돌아갔으면 좋겠습니다', '2024-03-20 10:30:00', 1
WHERE NOT EXISTS (SELECT 1 FROM estimate_request WHERE purpose = '게임용' AND customer_id = 1);

-- '사무용' 견적 요청이 존재하지 않으면 삽입
INSERT INTO estimate_request (purpose, budget, other_request, create_date, customer_id)
SELECT '사무용', 1000000, '문서 작업이 주 용도입니다', '2024-03-21 11:30:00', 2
WHERE NOT EXISTS (SELECT 1 FROM estimate_request WHERE purpose = '사무용' AND customer_id = 2);

-- '영상편집용' 견적 요청이 존재하지 않으면 삽입
INSERT INTO estimate_request (purpose, budget, other_request, create_date, customer_id)
SELECT '영상편집용', 3000000, '4K 영상 편집을 주로 합니다', '2024-03-22 14:30:00', 1
WHERE NOT EXISTS (SELECT 1 FROM estimate_request WHERE purpose = '영상편집용' AND customer_id = 1);

-- 견적서 데이터 삽입
-- 첫 번째 견적서가 존재하지 않으면 삽입
INSERT INTO estimate (estimate_request_id, seller_id, total_price, create_date)
SELECT 1, 1, 2500000, '2024-03-21 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM estimate WHERE estimate_request_id = 1 AND seller_id = 1);

-- 첫 번째 견적서의 구성요소가 존재하지 않으면 삽입
INSERT INTO estimate_component (item_id, price, estimate_id)
SELECT 1, 850000, 1
WHERE NOT EXISTS (SELECT 1 FROM estimate_component WHERE item_id = 1 AND estimate_id = @estimate_id); -- Intel i9-13900K

INSERT INTO estimate_component (item_id, price, estimate_id)
SELECT 3, 1500000, 1
WHERE NOT EXISTS (SELECT 1 FROM estimate_component WHERE item_id = 3 AND estimate_id = @estimate_id); -- NVIDIA RTX 4090

INSERT INTO estimate_component (item_id, price, estimate_id)
SELECT 4, 150000, 1
WHERE NOT EXISTS (SELECT 1 FROM estimate_component WHERE item_id = 4 AND estimate_id = @estimate_id);  -- Corsair 32GB DDR5

-- 두 번째 견적서가 존재하지 않으면 삽입
INSERT INTO estimate (estimate_request_id, seller_id, total_price, create_date)
SELECT 2, 2, 800000, '2024-03-22 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM estimate WHERE estimate_request_id = 2 AND seller_id = 2);

-- 두 번째 견적서의 구성요소가 존재하지 않으면 삽입
INSERT INTO estimate_component (item_id, price, estimate_id)
SELECT 2, 650000, 2
WHERE NOT EXISTS (SELECT 1 FROM estimate_component WHERE item_id = 2 AND estimate_id = @estimate_id); -- AMD Ryzen 9 7950X

INSERT INTO estimate_component (item_id, price, estimate_id)
SELECT 4, 150000, 2
WHERE NOT EXISTS (SELECT 1 FROM estimate_component WHERE item_id = 4 AND estimate_id = @estimate_id); -- Corsair 32GB DDR5

-- 기존 댓글들의 type을 CUSTOMER로 설정
UPDATE comment SET type = 'CUSTOMER' WHERE type IS NULL;