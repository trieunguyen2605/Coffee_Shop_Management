package com.coffeeshop.app.modules.file.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.file.dto.FileUploadResponse;
import com.coffeeshop.app.modules.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadImage(@RequestParam("file") MultipartFile file) {
        FileUploadResponse response = fileService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success("Upload file thành công", response));
    }
}
