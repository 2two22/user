package two.two_user.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import two.two_user.domain.Domain;
import two.two_user.exception.BudException;
import two.two_user.util.FilePathUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static two.two_user.exception.ErrorCode.FAILED_UPLOAD_FILE;


@Component
@Slf4j
@RequiredArgsConstructor
public class S3Client  {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;


    public String upload(MultipartFile multipartFile, Domain domain) {
        String fileName = FilePathUtil.getFileNameAndExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String filePath = FilePathUtil.createFilePath(domain);
        try {
            File file = File.createTempFile("file", fileName);
            multipartFile.transferTo(file);
            amazonS3.putObject(new PutObjectRequest(bucket, filePath + fileName, file));
        } catch (IOException e) {
            throw new BudException(FAILED_UPLOAD_FILE);
        }
        return filePath + fileName;
    }


    public String upload(File file, Domain domain) {
        String fileName = FilePathUtil.getFileNameAndExtension(file.getName());
        String filePath = FilePathUtil.createFilePath(domain);
        amazonS3.putObject(new PutObjectRequest(bucket, filePath + fileName, file));
        return filePath + fileName;
    }


    public void delete(String filePath) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }
}
