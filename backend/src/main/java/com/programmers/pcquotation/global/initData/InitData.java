package com.programmers.pcquotation.global.initData;

import static com.programmers.pcquotation.global.initData.CustomMultipartFile.*;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.pcquotation.domain.admin.entitiy.Admin;
import com.programmers.pcquotation.domain.admin.service.AdminService;
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.item.dto.ItemCreateRequest;
import com.programmers.pcquotation.domain.item.service.ItemService;
import com.programmers.pcquotation.domain.member.service.AuthService;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest;
import com.programmers.pcquotation.domain.seller.service.SellerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class InitData {
	private final AdminService adminService;
	private final CustomerService customerService;
	private final SellerService sellerService;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	private final ItemService itemService;
	@Autowired
	@Lazy
	private InitData self;

	@Bean
	public ApplicationRunner baseInitDataApplicationRunner() {
		return args -> {
				self.insertAdmin();
				self.insertCustomer();
				self.insertSeller();
				//self.insertCPU();
				//self.insertGPU();
				//self.insertRAM();
		};
	}

	public void insertAdmin() {
		if (adminService.findAdminByUsername("admin").isEmpty()) {
			Admin admin = new Admin(
				"admin",
				passwordEncoder.encode("password"));
			admin.setApiKey(UUID.randomUUID().toString());
			adminService.create(admin);
		}
	}

	public void insertSeller() {
		if(sellerService.findSellerByUsername("seller0001").isPresent()) return;
		SellerSignupRequest sellerSignupRequest = new SellerSignupRequest(
			"seller0001",
			"seller",
			"seller",
			"쿠팡주",
			"seller0001@gmail.com",
			true,
			"사과는",
			"빨간색");
		authService.processSignup(sellerSignupRequest);
	}

	public void insertCustomer() {
		if (customerService.findCustomerByUsername("customer0001").isPresent())
			return;
		CustomerSignupRequest customerSignupRequest =
			new CustomerSignupRequest(
				"customer0001"
				, "customer"
				, "customer"
				, "김광식주"
				, "customer0001@gmail.com"
				, "바나나는"
				, "노란색");

		authService.processSignup(customerSignupRequest);
	}

	public void insertCPU() throws Exception {
		self.insertItem(1L, "AMD 라이젠5-6세대 9600X (그래니트 릿지) (정품)", "https://i.ibb.co/xtytrk4N/AMD-5-6-9600-X.jpg");
		self.insertItem(1L, "AMD 라이젠7-6세대 9700X (그래니트 릿지) (정품)", "https://i.ibb.co/3y6QF8r0/AMD-7-6-9700-X.jpg");
		self.insertItem(1L, "인텔 코어 울트라9 시리즈2 285K (애로우레이크) (정품)", "https://i.ibb.co/vxqGWGpC/9-2-285-K.jpg");
		self.insertItem(1L, "인텔 코어i5-14세대 14400F (랩터레이크 리프레시) (정품)", "https://i.ibb.co/ynhXcyx6/i5-14-14400-F.jpg");
		self.insertItem(1L, "인텔 코어i7-14세대 14700K (랩터레이크 리프레시) (정품)", "https://i.ibb.co/fzfnKkVJ/i7-14-14700-K.jpg");
		self.insertItem(1L, "인텔 코어 울트라5 시리즈2 225F (애로우레이크) (정품)", "https://i.ibb.co/C55dJs96/5-2-225-F.jpg");

	}

	public void insertGPU() throws Exception {
		self.insertItem(2L, "COLORFUL 지포스 RTX 4060 토마호크 DUO V2 D6 8GB",
			"https://i.ibb.co/zWfM4PsD/COLORFUL-RTX-4060-DUO-V2-D6-8-GB.webp");
		self.insertItem(2L, "갤럭시 GALAX 지포스 RTX 5080 WHITE OC D7 16GB",
			"https://i.ibb.co/rK1D4KXY/GALAX-RTX-5080-WHITE-OC-D7-16-GB.jpg");
		self.insertItem(2L, "MSI 지포스 RTX 5080 뱅가드 SOC D7 16GB 하이퍼프로져",
			"https://i.ibb.co/yndbCTrk/MSI-RTX-5080-SOC-D7-16-GB.jpg");
		self.insertItem(2L, "PALIT 지포스 RTX 5080 GAMEROCK OC D7 16GB 이엠텍",
			"https://i.ibb.co/HL5xzPwC/PALIT-RTX-5080-GAMEROCK-OC-D7-16-GB.jpg");
		self.insertItem(2L, "이엠텍 지포스 RTX 4060 STORM X Dual OC D6 8GB",
			"https://i.ibb.co/JRSmMsbK/RTX-4060-STORM-X-Dual-OC-D6-8-GB.jpg");
		self.insertItem(2L, "이엠텍 지포스 RTX 4060 MIRACLE WHITE D6 8GB",
			"https://i.ibb.co/ksvLHxM0/RTX-4060-MIRACLE-WHITE-D6-8-GB.jpg");
	}

	public void insertRAM() throws Exception {
		self.insertItem(3L, "마이크론 Crucial DDR5-6400 CL38 PRO Overclocking 패키지 대원씨티에스 (32GB(16Gx2))",
			"https://i.ibb.co/V05VjVH7/Crucial-DDR5-6400-CL38-PRO-Overclocking-32-GB-16-Gx2.jpg");
		self.insertItem(3L, "G.SKILL DDR5-6400 CL32 TRIDENT Z5 ROYAL 실버 패키지 (32GB(16Gx2))",
			"https://i.ibb.co/b5s7ZVjT/G-SKILL-DDR5-6400-CL32-TRIDENT-Z5-ROYAL-32-GB-16-Gx2.jpg");
		self.insertItem(3L, "GeIL DDR5-6000 CL38 ORION V RGB White 패키지 (32GB(16Gx2))",
			"https://i.ibb.co/CKtZHSTB/Ge-IL-DDR5-6000-CL38-ORION-V-RGB-White-32-GB-16-Gx2.jpg");
		self.insertItem(3L, "G.SKILL DDR5-6000 CL30 RIPJAWS M5 RGB 화이트 패키지 (32GB(16Gx2))",
			"https://i.ibb.co/S4w2DX8J/G-SKILL-DDR5-6000-CL30-RIPJAWS-M5-RGB-32-GB-16-Gx2.jpg");
		self.insertItem(3L, "CORSAIR DDR5-6000 CL30 VENGEANCE RGB DP WHITE 패키지 (32GB(16Gx2))",
			"https://i.ibb.co/mCzdwx1B/CORSAIR-DDR5-6000-CL30-VENGEANCE-RGB-DP-WHITE-32-GB-16-Gx2.jpg");
		self.insertItem(3L, "G.SKILL DDR5-6000 CL30 TRIDENT Z5 NEO RGB J 화이트 패키지 (64GB(32Gx2))",
			"https://i.ibb.co/v4Dm2sVn/G-SKILL-DDR5-6000-CL30-TRIDENT-Z5-NEO-RGB-J-64-GB-32-Gx2.jpg");
	}

	public void insertItem(long category, String name, String url) throws Exception {
		if (itemService.findByName(name) != null)
			return;
		MultipartFile multipartFile = fromUrl(url);

		ItemCreateRequest itemCreateRequest =
			new ItemCreateRequest(category, name, multipartFile);
		itemService.addItem(itemCreateRequest);
	}

}
