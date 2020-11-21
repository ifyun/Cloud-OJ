package group._204.oj.fileserver.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/image")
@Api(tags = "图片")
public class ImageController {

    @Value("${project.file-dir}")
    private String fileDir;

    @ApiOperation(value = "上传头像", notes = "/image/avatar/{userId}.png 可以获取头像文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true),
            @ApiImplicitParam(name = "file", value = "jpg, png", dataTypeClass = MultipartFile.class, required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "上传成功"),
            @ApiResponse(code = 500, message = "无法写入文件")
    })
    @PostMapping(path = "avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader String userId,
                                          @RequestParam("file") MultipartFile file) {
        String avatarDir = fileDir + "image/avatar/";
        File dir = new File(avatarDir);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建目录 {}", dir.getName());
            return ResponseEntity.status(500).body("无法创建目录.");
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
