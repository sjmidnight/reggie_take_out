package com.itheima.reggie.controller;


import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)  {
        // file  要和前端里的file  相同
        log.info(file.toString());

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID shen生成文件名 防止覆盖

        String fileName = UUID.randomUUID().toString() + suffix;

        File dir = new File(basePath);

        if (!dir.exists()){
            dir.mkdirs();
        }


        try {
            file.transferTo(new File(basePath+fileName));

            log.info("{}",basePath+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * file download
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        // inputstream   read a file
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //outputstream write a file
            ServletOutputStream servletOutputStream = response.getOutputStream();

            response.setContentType("img/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len =fileInputStream.read(bytes))!=-1) {
                servletOutputStream.write(bytes,0,len);
                servletOutputStream.flush();
            }

            servletOutputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
