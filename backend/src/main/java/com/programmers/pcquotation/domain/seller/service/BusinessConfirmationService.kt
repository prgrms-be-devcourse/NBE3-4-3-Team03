package com.programmers.pcquotation.domain.seller.service

import com.programmers.pcquotation.global.enums.OpenApiStatus
import org.json.JSONException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Service
class BusinessConfirmationService(
    @Value("\${openapi.url}")
    private val apiUrl: String,

    @Value("\${openapi.authentication-key.encoding}")
    private val encodedKey: String
) {


    fun checkCode(code: String): Boolean {
        try {
            val jsonData = createJson(code)
            val conn = connect(apiUrl + encodedKey)
            send(conn, jsonData)
            val bSttValue = receive(conn)
            return bSttValueCheck(bSttValue, OpenApiStatus.run)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun bSttValueCheck(bSttValue: String, status: OpenApiStatus): Boolean {
        val value = status.value
        return value  == bSttValue
    }

    @Throws(JSONException::class)
    fun createJson(code: String): JSONObject {
        val jsonData = JSONObject()
        jsonData.put("b_no", arrayOf(code)) // 사업자번호 입력
        return jsonData
    }

    @Throws(IOException::class)
    fun connect(url: String): HttpURLConnection {
        val connectUrl = URL(url)
        val conn = connectUrl.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")
        conn.doOutput = true
        return conn
    }

    @Throws(IOException::class)
    fun send(conn: HttpURLConnection, jsonData: JSONObject) {
        conn.outputStream.use { os ->
            val input = jsonData.toString().toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun receive(conn: HttpURLConnection): String {
        val br = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
        val response = StringBuilder()
        var responseLine: String
        while ((br.readLine().also { responseLine = it }) != null) {
            response.append(responseLine.trim { it <= ' ' })
        }
        conn.disconnect()
        return separate(response.toString())
    }

    @Throws(JSONException::class)
    fun separate(jsonString: String?): String {
        val jsonObject = JSONObject(jsonString)

        val dataArray = jsonObject.getJSONArray("data")

        val dataObject = dataArray.getJSONObject(0)

        val bNo = dataObject.getString("b_no")
        val bStt = dataObject.getString("b_stt")
        val bSttCd = dataObject.getString("b_stt_cd")
        val taxType = dataObject.getString("tax_type")
        val taxTypeCd = dataObject.getString("tax_type_cd")
        val endDt = dataObject.getString("end_dt")
        val utccYn = dataObject.getString("utcc_yn")
        val taxTypeChangeDt = dataObject.getString("tax_type_change_dt")
        val invoiceApplyDt = dataObject.getString("invoice_apply_dt")
        val rbfTaxType = dataObject.getString("rbf_tax_type")
        val rbfTaxTypeCd = dataObject.getString("rbf_tax_type_cd")
        return bStt
    }
}
