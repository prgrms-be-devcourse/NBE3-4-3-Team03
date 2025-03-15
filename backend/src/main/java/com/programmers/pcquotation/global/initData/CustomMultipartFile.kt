package com.programmers.pcquotation.global.initData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.web.multipart.MultipartFile;

import kotlin.jvm.JvmStatic;

class CustomMultipartFile(
    private val content: ByteArray,
    private val fileName: String,
    private val contentType: String
) : MultipartFile {

    override fun getName(): String = fileName

    override fun getOriginalFilename(): String = fileName

    override fun getContentType(): String = contentType

    override fun isEmpty(): Boolean = content.isEmpty()

    override fun getSize(): Long = content.size.toLong()

    override fun getBytes(): ByteArray = content

    override fun getInputStream(): InputStream = ByteArrayInputStream(content)

    override fun transferTo(dest: File) {
        FileOutputStream(dest).use { fos ->
            fos.write(content)
        }
    }

    companion object {
        @JvmStatic
        fun fromUrl(imageUrl: String): MultipartFile {
            val url = URL(imageUrl)
            val connection: URLConnection = url.openConnection()
            val contentType = connection.contentType
            val fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1)

            return url.openStream().use { inputStream ->
                ByteArrayOutputStream().use { buffer ->
                    val data = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(data).also { bytesRead = it } != -1) {
                        buffer.write(data, 0, bytesRead)
                    }
                    CustomMultipartFile(buffer.toByteArray(), fileName, contentType)
                }
            }
        }
    }
}