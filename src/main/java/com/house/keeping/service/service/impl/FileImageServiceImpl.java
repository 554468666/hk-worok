package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.FileEntity;
import com.house.keeping.service.entity.MemberEntity;
import com.house.keeping.service.mapper.FileImageMapper;
import com.house.keeping.service.mapper.MemberMapper;
import com.house.keeping.service.service.FileImageService;
import org.springframework.stereotype.Service;

@Service
public class FileImageServiceImpl extends ServiceImpl<FileImageMapper, FileEntity> implements FileImageService {

    @Override
    public Boolean createFile(FileEntity fileEntity) {
        return this.save(fileEntity);
    }

    @Override
    public Boolean updateFile(FileEntity fileEntity) {
        return this.updateById(fileEntity);
    }

    @Override
    public Boolean deleteFile(Long id) {
        return this.removeById(id);
    }
}
