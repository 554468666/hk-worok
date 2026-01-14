package com.house.keeping.service.controller;

import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传相关接口")
public class FileUploadController {
    
    @Value("${upload.path:./uploads}")
    private String uploadPath;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Value("${server.port:8080}")
    private Integer serverPort;
    
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_ARCHIVE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long MAX_OTHER_SIZE = 20 * 1024 * 1024; // 20MB
    
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "webp"
    );
    
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "xls", "xlsx", "txt"
    );
    
    private static final Set<String> ARCHIVE_EXTENSIONS = Set.of(
        "zip", "rar", "7z"
    );
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    
    /**
     * 上传单个文件
     */
    @PostMapping
    @Operation(summary = "上传文件", description = "上传单个文件到服务器，支持多种文件类型")
    public Result<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "category", required = false) String category) {
        
        // 验证文件
        if (file.isEmpty()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        // 自动检测文件类型
        if (StringUtils.hasText(type)) {
            if (!isValidType(type, extension)) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
            }
        } else {
            type = detectFileType(extension);
        }
        
        // 验证文件大小
        validateFileSize(file.getSize(), type);
        
        // 生成新文件名
        String newFilename = generateFilename(extension);
        
        // 创建目录
        String relativePath = DATE_FORMAT.format(new Date());
        String fullUploadPath = uploadPath + File.separator + relativePath;
        Path uploadDir = Paths.get(fullUploadPath);
        
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 构建访问URL
            String fileUrl = buildFileUrl(relativePath, newFilename);
            
            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", newFilename);
            data.put("originalName", originalFilename);
            data.put("size", file.getSize());
            data.put("type", file.getContentType());
            data.put("uploadTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            
            return Result.success(data);
            
        } catch (IOException e) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
    
    /**
     * 批量上传文件
     */
    @PostMapping("/batch")
    @Operation(summary = "批量上传文件", description = "一次性上传多个文件")
    public Result<Map<String, Object>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "category", required = false) String category) {
        
        if (files == null || files.length == 0) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "请选择要上传的文件");
        }
        
        if (files.length > 20) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "最多支持同时上传20个文件");
        }
        
        int successCount = 0;
        int failedCount = 0;
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) uploadFile(
                    file, null, category
                ).getData();
                
                result.put("filename", file.getOriginalFilename());
                result.put("success", true);
                results.add(result);
                successCount++;
                
            } catch (Exception e) {
                Map<String, Object> result = new HashMap<>();
                result.put("filename", file.getOriginalFilename());
                result.put("success", false);
                result.put("error", e.getMessage());
                results.add(result);
                failedCount++;
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", successCount);
        data.put("failed", failedCount);
        data.put("total", files.length);
        data.put("results", results);
        
        return Result.success(data);
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * 验证文件类型是否匹配扩展名
     */
    private boolean isValidType(String type, String extension) {
        switch (type.toLowerCase()) {
            case "image":
                return IMAGE_EXTENSIONS.contains(extension);
            case "document":
                return DOCUMENT_EXTENSIONS.contains(extension);
            case "archive":
                return ARCHIVE_EXTENSIONS.contains(extension);
            default:
                return true;
        }
    }
    
    /**
     * 自动检测文件类型
     */
    private String detectFileType(String extension) {
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return "image";
        }
        if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return "document";
        }
        if (ARCHIVE_EXTENSIONS.contains(extension)) {
            return "archive";
        }
        return "other";
    }
    
    /**
     * 验证文件大小
     */
    private void validateFileSize(long size, String type) {
        long maxSize;
        switch (type.toLowerCase()) {
            case "image":
                maxSize = MAX_IMAGE_SIZE;
                break;
            case "document":
                maxSize = MAX_DOCUMENT_SIZE;
                break;
            case "archive":
                maxSize = MAX_ARCHIVE_SIZE;
                break;
            default:
                maxSize = MAX_OTHER_SIZE;
                break;
        }
        
        if (size > maxSize) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.FILE_SIZE_EXCEED);
        }
    }
    
    /**
     * 生成新文件名
     */
    private String generateFilename(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return StringUtils.hasText(extension) ? uuid + "." + extension : uuid;
    }
    
    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String relativePath, String filename) {
        return String.format("http://localhost:%d%s/uploads/%s/%s", 
            serverPort, contextPath, relativePath.replace("\\", "/"), filename);
    }
}
