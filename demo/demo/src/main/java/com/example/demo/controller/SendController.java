package com.example.demo.controller;

import com.example.demo.VO.SendReq;
import com.example.demo.domain.Send.Send;
import com.example.demo.service.SendService;
import com.example.demo.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@CrossOrigin(
        // localhost:5500 과 127.0.0.1 구분
        origins = "http://158.247.194.215:8080", // allowCredentials = "true" 일 경우, orogins="*" 는 X
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT,RequestMethod.HEAD,RequestMethod.OPTIONS}
)
@Controller
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class SendController {

    private final SendService sendService;
    private final UploadService uploadService;

    //발송
    @RequestMapping(method = RequestMethod.POST, path = "/Send")
    @ResponseBody
    public String Send(@RequestBody SendReq req) throws IOException, ParseException {
        System.out.println("req=====================================" + req);

        log.info(req.toString());

        return sendService.sendInsert(req);
    }

    //수정시 발송
    @RequestMapping("/updateSend")
    @ResponseBody
    public String updateSend(@RequestBody SendReq req) throws IOException, ParseException {
        log.info(req.toString());
        uploadService.updateFileName(req.getNewFileName(),req.getUserKey());
        return Send(req);
    }

    //발송 -> 결제완료시 -> 상세정보
    @RequestMapping("/sendDetail")
    @ResponseBody
    public Map<String,Object> sendDetail(@RequestBody Map<String,String> map) throws IOException {
        return sendService.sendDetail(map.get("userKey"));

    }

    //재발송(Job_No있을때)
    @PostMapping("/reSend")
    @ResponseBody
    public String reSendJobNo(@RequestBody Map<String,String> map) throws IOException, ParseException {
        String jobNo = sendService.getJobNo(map.get("userKey"));
        if(jobNo==null){
            reSend(map);
            return null;
        }
        sendService.reSendJobNo(map);
        return "성공";
    }

    //재발송(Job_No없을떄)
    public String reSend(@RequestBody Map<String, String> param) throws IOException, ParseException {
        String userKey = param.get("userKey");
        return sendService.reSend(userKey);
    }

}