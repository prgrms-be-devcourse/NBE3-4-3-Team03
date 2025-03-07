package com.programmers.pcquotation.domain.seller.service;

import static com.programmers.pcquotation.global.enums.OpenApiStatus.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.global.enums.OpenApiStatus;

@Service
public class BusinessConfirmationService {

	@Value("${openapi.url}")
	private String apiUrl;

	@Value("${openapi.authentication-key.encoding}")
	private String encodedKey;

	public boolean checkCode(String code) {
		try {
			JSONObject jsonData = createJson(code);
			HttpURLConnection conn = connect(apiUrl + encodedKey);
			send(conn, jsonData);
			String bSttValue = receive(conn);
			return bSttValueCheck(bSttValue, OpenApiStatus.RUN);
		} catch (JSONException | IOException e) {
			throw new RuntimeException(e);
		}

	}

	boolean bSttValueCheck(String bSttValue, OpenApiStatus status) {
		return status.getValue().equals(bSttValue);

	}

	JSONObject createJson(String code) throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("b_no", new String[] {code}); // 사업자번호 입력
		return jsonData;
	}

	HttpURLConnection connect(String url) throws IOException {
		URL connectUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		return conn;
	}

	void send(HttpURLConnection conn, JSONObject jsonData) throws IOException {
		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = jsonData.toString().getBytes("utf-8");
			os.write(input, 0, input.length);
		}
	}

	String receive(HttpURLConnection conn) throws IOException, JSONException {
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
		StringBuilder response = new StringBuilder();
		String responseLine;
		while ((responseLine = br.readLine()) != null) {
			response.append(responseLine.trim());
		}
		conn.disconnect();
		return separate(response.toString());

	}

	String separate(String jsonString) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonString);

		JSONArray dataArray = jsonObject.getJSONArray("data");

		JSONObject dataObject = dataArray.getJSONObject(0);

		String bNo = dataObject.getString("b_no");
		String bStt = dataObject.getString("b_stt");
		String bSttCd = dataObject.getString("b_stt_cd");
		String taxType = dataObject.getString("tax_type");
		String taxTypeCd = dataObject.getString("tax_type_cd");
		String endDt = dataObject.getString("end_dt");
		String utccYn = dataObject.getString("utcc_yn");
		String taxTypeChangeDt = dataObject.getString("tax_type_change_dt");
		String invoiceApplyDt = dataObject.getString("invoice_apply_dt");
		String rbfTaxType = dataObject.getString("rbf_tax_type");
		String rbfTaxTypeCd = dataObject.getString("rbf_tax_type_cd");
		return bStt;

	}

}
