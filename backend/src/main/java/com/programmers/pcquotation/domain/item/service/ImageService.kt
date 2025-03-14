package com.programmers.pcquotation.domain.item.service

import com.programmers.pcquotation.domain.item.exception.image.ImageResourceNotFoundException
import com.programmers.pcquotation.domain.item.exception.image.ImageStoreException
import com.programmers.pcquotation.domain.item.exception.image.InvalidImageRequestException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.*

@Service
class ImageService(
    @Value("\${image.file.path}") path: String
) {
    private val uploadPath = File(path)
        .absolutePath + File.separator

    init {
        // 디렉토리 생성
        File(uploadPath).mkdirs()
    }

    /**
     * URL 경로에 해당하는 파일을 반환합니다.
     * filename 으로 null 이나 빈 문자열, 잘못된 확장자가 들어오는 경우는 Resource 탐색로직 전에 예외를 반환합니다.
     * -> 불필요한 IO 바운드를 줄이기 위해 수정
     *
     * @param filename File name
     * @return Product Image Resource
     */
    fun getImageByFilename(filename: String): Resource {
        if (filename.isEmpty() || checkInvalidExt(filename)) {
            throw InvalidImageRequestException("Invalid Type Filename")
        }

        try {
            return UrlResource("file:" + getFullPath(filename))
        } catch (e: Exception) {
            throw ImageResourceNotFoundException("요청한 이미지가 존재하지 않습니다.")
        }
    }

    /**
     * Product Image 를 전달받아 디렉토리에 저장한다.
     *
     * @param multipartFile Image 파일
     * @return 저장한 파일 이름
     */
    fun storeImage(multipartFile: MultipartFile): String {
        try {
            if (multipartFile.isEmpty) {
                throw ImageResourceNotFoundException("파일이 비어있거나 유효하지 않습니다.")
            }

            val originalFilename = multipartFile.originalFilename
            val storeFileName = createStoreFileName(originalFilename!!)
            if (checkInvalidExt(originalFilename)) {
                throw InvalidImageRequestException("File ext is invalid")
            }

            multipartFile.transferTo(File(getFullPath(storeFileName)))

            return storeFileName
        } catch (e: IOException) {
            throw ImageStoreException("파일 저장에 실패했습니다.")
        }
    }

    private fun getFullPath(filename: String): String {
        return uploadPath + filename
    }

    private fun createStoreFileName(originalFilename: String): String {
        val ext = extractExt(originalFilename)
        val uuid = UUID.randomUUID().toString()
        return "$uuid.$ext"
    }

    private fun extractExt(originalFilename: String): String {
        val pos = originalFilename.lastIndexOf(".")
        return originalFilename.substring(pos + 1)
    }

    private fun checkInvalidExt(originalFilename: String): Boolean {
        val ext = extractExt(originalFilename)
        return (ext != "png") && (ext != "jpg") && (ext != "jpeg") && (ext != "webp")
    }
}