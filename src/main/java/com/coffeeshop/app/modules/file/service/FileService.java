package com.coffeeshop.app.modules.file.service;

import com.coffeeshop.app.modules.file.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileUploadResponse uploadImage(MultipartFile file);
}
