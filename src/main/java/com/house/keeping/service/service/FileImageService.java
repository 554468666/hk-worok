package com.house.keeping.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.house.keeping.service.entity.FileEntity;

public interface FileImageService extends IService<FileEntity> {
    Boolean createFile(FileEntity fileEntity);

    Boolean updateFile(FileEntity fileEntity);

    Boolean deleteFile(Long id);

}
