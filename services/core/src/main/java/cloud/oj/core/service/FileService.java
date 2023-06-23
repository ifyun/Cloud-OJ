package cloud.oj.core.service;

import cloud.oj.core.config.AppConfig;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.dao.UserDao;
import cloud.oj.core.entity.TestData;
import cloud.oj.core.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final String fileDir;

    private final UserDao userDao;

    private final ProblemDao problemDao;

    public FileService(AppConfig appConfig, UserDao userDao, ProblemDao problemDao) {
        this.fileDir = appConfig.getFileDir();
        this.userDao = userDao;
        this.problemDao = problemDao;
    }

    public List<TestData> getTestData(Integer problemId) {
        var files = new File(fileDir + "data/" + problemId).listFiles();
        var list = new ArrayList<TestData>();

        if (files != null) {
            for (var file : files) {
                list.add(new TestData(file.getName(), file.length()));
            }
        }

        list.sort(Comparator.comparing(TestData::getFileName));
        return list;
    }

    /**
     * 保存测试数据
     *
     * @param problemId 题目 Id
     * @param files     文件
     */
    public ResponseEntity<?> saveTestData(Integer problemId, MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("未选择文件");
        }

        // ! 开放的题目不允许上传测试数据
        if (problemDao.isEnable(problemId)) {
            throw new GenericException(400, "题目已开放，不准上传测试数据");
        }

        var testDataDir = fileDir + "data/";
        var dir = new File(testDataDir + problemId);

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建目录 {}", dir.getAbsolutePath());
            return ResponseEntity.status(500).body("无法创建目录");
        }

        for (MultipartFile file : files) {
            var fileName = file.getOriginalFilename();
            var dest = new File(dir.getAbsolutePath() + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} ", dest.getAbsolutePath());
            } catch (IOException e) {
                log.error("上传文件 {} 失败: {}", dest.getAbsolutePath(), e.getMessage());
                return ResponseEntity.status(500).build();
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除测试数据
     *
     * @param problemId 题目 Id
     * @param name      文件名
     */
    public ResponseEntity<?> deleteTestData(Integer problemId, String name) {
        // ! 开放的题目不允许删除测试数据
        if (problemDao.isEnable(problemId)) {
            throw new GenericException(400, "题目已开放，不准删除测试数据");
        }

        var testDataDir = fileDir + "data/";
        var file = new File(testDataDir + problemId + "/" + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info("已删除文件 {}", file.getAbsolutePath());
                return ResponseEntity.noContent().build();
            } else {
                log.error("删除文件 {} 失败", file.getAbsolutePath());
                return ResponseEntity.status(500).build();
            }
        } else {
            log.warn("文件 {} 不存在", file.getAbsolutePath());
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }

    /**
     * 保存头像图片
     */
    public ResponseEntity<?> saveAvatar(String userId, MultipartFile file) {
        var avatarDir = fileDir + "image/avatar/";
        var dir = new File(avatarDir);
        var avatar = new File(avatarDir + userId + ".png");

        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建目录 {}", dir.getAbsolutePath());
            return ResponseEntity.status(500).body("无法创建目录.");
        }

        try {
            file.transferTo(avatar);
            userDao.updateAvatar(userId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            log.error("上传头像失败, path: {}, error: {}", avatar.getAbsolutePath(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<?> saveProblemImage(MultipartFile file) {
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
        var fileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;

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
