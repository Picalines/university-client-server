package com.kostenko.controller

import com.kostenko.model.FileEntity
import com.kostenko.repository.FileEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import kotlin.jvm.optionals.getOrNull

@Controller
@RequestMapping("/files")
class FileController(@Autowired private val files: FileEntityRepository) {
    @GetMapping("")
    fun getUploadedFiles(model: Model): String {
        model.addAttribute("files", files.findAll())
        return "index"
    }

    @PostMapping("")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("filename") filenameWithoutExtension: String
    ): String {
        if (!file.isEmpty) {
            val originalFilename = file.originalFilename ?: ""
            val fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
            val filename = "$filenameWithoutExtension.$fileExtension"

            files.save(
                FileEntity(-1, filename, file.size, file.bytes.toTypedArray())
            )
        }

        return "redirect:/files"
    }

    @GetMapping("/{id}")
    fun downloadFile(@PathVariable id: Long): ResponseEntity<ByteArrayResource> {
        val fileEntity = files.findById(id).getOrNull() ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"${fileEntity.name}\"")

        return ResponseEntity(ByteArrayResource(fileEntity.data.toByteArray()), headers, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: Long): String {
        files.deleteById(id)
        return "redirect:/files"
    }
}