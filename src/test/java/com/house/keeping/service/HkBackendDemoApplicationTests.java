package com.house.keeping.service;

import com.house.keeping.service.common.PicuiApi;
import com.house.keeping.service.common.SmmsApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class HkBackendDemoApplicationTests {

    @Autowired
    private SmmsApi smmsApi;

    @Autowired
    private PicuiApi picuiApi;

    @Test
    void contextLoads() throws IOException {
        File file = new File("E://ad2.png");
        picuiApi.picuiDeleteImage("Xpkokj");
/*        picuiApi.getToken();

        File file = new File("E://xxx.png");
        smmsApi.putApiImage(file);*/
    }

}
