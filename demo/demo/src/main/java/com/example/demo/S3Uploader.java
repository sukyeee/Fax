package com.example.demo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.demo.service.DetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    private final GlobalVariables globalVariables;
    private final DetectionService detectionService;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFiles(MultipartFile multipartFile, String dirName,String RealPath) throws IOException {
        log.info("uploadFiles진입");
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
        log.info("변환여부");
        return upload(uploadFile, dirName,RealPath);
    }

    public String upload(File uploadFile, String filePath,String RealPath) {
        log.info("upload진입");
        String fileName = RealPath;   // S3에 저장된 파일 이름
        log.info("진입 여부1"+fileName);
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        log.info("진입 여부2");
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    public boolean uploadV2(File uploadFile, String filePath,String RealPath) throws IOException {
        log.info("upload진입");
        String fileName = RealPath;   // S3에 저장된 파일 이름
        log.info("진입 여부1"+fileName);
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        log.info("진입 여부2");
        log.info(uploadFile.getAbsolutePath());
        boolean result = detectionService.ResidentNumberDetection(uploadFile.getAbsolutePath());
        log.info(String.valueOf(result));
        removeNewFile(uploadFile);
        return result;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        log.info("putS3진입");
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("puts3");
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    public void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일삭제");
            System.out.println("File delete success");
            return;
        }
        System.out.println("File delete fail");
    }

    //S3파일 삭제
    public void removeS3File(String fileName){
        log.info("removeS3File"+fileName);
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket,fileName));
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public File download(String RealPath) throws IOException {
        //RealPath : 폴더명/파일네임.pdf
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket,RealPath));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        try (FileOutputStream stream = new FileOutputStream(System.getProperty("user.dir") + "/" +"temp.pdf")) {
            stream.write(bytes, 0, bytes.length);
        }
        File file = new File(System.getProperty("user.dir") + "/" +"temp.pdf");
        return file;
    }

    //파일 이름 변경하기
    public void changS3FileName(String orgKey,String copyKey){
        try {
            //Copy 객체 생성
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    bucket,
                    orgKey,
                    bucket,
                    copyKey
            );
            //Copy
            amazonS3Client.copyObject(copyObjRequest);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

    }

    //파일 가져오기
    public byte[] getFile(String fileName) throws IOException {
        S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucket, fileName));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        return bytes;
    }
}