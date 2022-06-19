package com.cyc.schoolcanteen.controller;

import com.cyc.schoolcanteen.dto.Result;
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
import java.io.IOException;
import java.util.UUID;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-23 15:39
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${school-canteen.file-path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fileName);
    }

    @GetMapping("/download")
    public void download(String fileName, HttpServletResponse response){
        FileInputStream fis = null;
        ServletOutputStream os = null;
        try {
            if(fileName == null){
                fileName = "noImg.png";
            }
            fis = new FileInputStream(new File(basePath + fileName));

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            os = response.getOutputStream();
            while ((len = fis.read(bytes)) != -1){
                os.write(bytes,0,len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
