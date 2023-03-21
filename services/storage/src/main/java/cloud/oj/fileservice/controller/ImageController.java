package cloud.oj.fileservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {

    @Value("${app.file-dir}")
    private String fileDir;

    /**
     * 上传头像
     */
    @PostMapping(path = "avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader String userId, @RequestParam MultipartFile file) {
        var avatarDir = fileDir + "image/avatar/";
        var avatar = new File(avatarDir + userId + ".png");

        try {
            file.transferTo(avatar);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            log.error("上传头像失败, path: {}, error: {}", avatar.getAbsolutePath(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 上传题目图片
     */
    @PostMapping(path = "problem")
    public ResponseEntity<?> uploadProblemImage(@RequestParam MultipartFile file) {
        var originalName = file.getOriginalFilename();

        if (originalName == null) {
            return ResponseEntity.badRequest().build();
        }

        var dir = new File(fileDir + "image/problem/");

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建目录 {}", dir.getAbsolutePath());
            return ResponseEntity.status(500).body("无法创建目录.");
        }

        var ext = originalName.substring(originalName.lastIndexOf("."));
        var fileName = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(15) + ext;

        var image = new File(dir.getAbsolutePath() + "/" + fileName);

        try {
            file.transferTo(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileName);
        } catch (IOException e) {
            log.error("上传题目图片失败, path: {}, error: {}", image.getAbsolutePath(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
