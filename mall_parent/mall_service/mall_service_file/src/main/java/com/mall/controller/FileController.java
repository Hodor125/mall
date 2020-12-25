package com.mall.controller;

import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.util.FastDFSClient;
import com.mall.util.FastDFSFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/25
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file){
        try {
            //1.获取文件名称
            String orName = file.getOriginalFilename();

            //2.获取文件后缀
            int index = orName.lastIndexOf(".");
            String ext = orName.substring(index);

            //3.获取文件内容
            byte[] content = file.getBytes();

            //4.构建文件对象
            FastDFSFile fastDFSFile = new FastDFSFile(orName, content, ext);

            //5.执行上传
            String[] uploadResult = FastDFSClient.upload(fastDFSFile);
            String groupName = uploadResult[0];
            String remoteFilePath = uploadResult[1];

            //6.拼接url
            String url = FastDFSClient.getTrackerUrl() + groupName + "/" + remoteFilePath;
            //TODO 添加存储文件路径到DB的操作

            return new Result(true, StatusCode.OK, "上传文件成功", url);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "上传文件失败");
        }
    }

    //通过ResponseEntity设置http相应内容、状态以及头信息
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(){
        String groupName = "group1";
        String remoteFilePath = "M00/00/00/wKjIgF_llhCAXfa-AAFj4WYrEFA61..jpg";
        byte[] content = FastDFSClient.downFile2(groupName, remoteFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            headers.setContentDispositionFormData("attachment", new String("脉动.jpg".getBytes("UTF-8"),"iso8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

    @PostMapping("/remove")
    public Result remove(){
        String groupName = "group1";
        String remoteFilePath = "M00/00/00/wKjIgF_llhCAXfa-AAFj4WYrEFA61..jpg";
        try {
            FastDFSClient.deleteFile(groupName, remoteFilePath);
            return new Result(true, StatusCode.OK, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "删除失败");
        }
    }
}
