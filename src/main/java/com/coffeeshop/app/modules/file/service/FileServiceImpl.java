package com.coffeeshop.app.modules.file.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.modules.file.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;

    @Override
    public FileUploadResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "File không được để trống");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "webp");
            if (!allowedExtensions.contains(extension)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Định dạng file không hợp lệ. Chỉ chấp nhận jpg, jpeg, png, webp");
            }
        } else {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên file không hợp lệ");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "coffee-shop/products",
                    "resource_type", "image"
            ));

            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return new FileUploadResponse(imageUrl, publicId);

        } catch (IOException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi upload file lên Cloudinary: " + e.getMessage());
        }
    }
}
