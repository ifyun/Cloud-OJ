package group._204.oj.fileserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {

    @Value("${project.file-dir}")
    private String fileDir;

    @PostMapping("avatar")
    public ResponseEntity<?> uploadAvatar(String userId,
                                          @RequestParam("file") MultipartFile file) {
        String avatarDir = fileDir + "image/avatar/";
        File dir = new File(avatarDir);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("无法创建目录 {}", dir.getName());
                return ResponseEntity.status(500).body("无法创建目录.");
            }
        }

        File avatar = new File(avatarDir + userId + ".png");

        try {
            file.transferTo(avatar);
            log.info("上传头像: {} ", avatar.getAbsolutePath());
        } catch (IOException e) {
            log.error("上传头像失败, path: {}, error: {}", avatar.getAbsolutePath(), e.getMessage());
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
