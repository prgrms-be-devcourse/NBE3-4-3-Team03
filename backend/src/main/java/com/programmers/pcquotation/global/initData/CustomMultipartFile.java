package com.programmers.pcquotation.global.initData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
	private final byte[] content;
	private final String fileName;
	private final String contentType;

	public CustomMultipartFile(byte[] content, String fileName, String contentType) {
		this.content = content;
		this.fileName = fileName;
		this.contentType = contentType;
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public String getOriginalFilename() {
		return fileName;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return content.length == 0;
	}

	@Override
	public long getSize() {
		return content.length;
	}

	@Override
	public byte[] getBytes() {
		return content;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void transferTo(File dest) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			fos.write(content);
		}
	}

	public static MultipartFile fromUrl(String imageUrl) throws IOException {
		URL url = new URL(imageUrl);
		URLConnection connection = url.openConnection();
		String contentType = connection.getContentType();
		String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

		try (InputStream inputStream = connection.getInputStream();
			 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

			byte[] data = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(data, 0, 1024)) != -1) {
				buffer.write(data, 0, bytesRead);
			}

			return new CustomMultipartFile(buffer.toByteArray(), fileName, contentType);
		}
	}
}
