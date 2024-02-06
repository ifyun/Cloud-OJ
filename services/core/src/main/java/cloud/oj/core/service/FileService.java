package cloud.oj.core.service;

import cloud.oj.core.config.AppConfig;
import cloud.oj.core.dao.ProblemDao;
import cloud.oj.core.dao.UserDao;
import cloud.oj.core.entity.ProblemData;
import cloud.oj.core.entity.SPJ;
import cloud.oj.core.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final String fileDir;

    private final UserDao userDao;

    private final ProblemDao problemDao;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    public FileService(AppConfig appConfig, UserDao userDao, ProblemDao problemDao) {
        this.fileDir = appConfig.getFileDir();
        this.userDao = userDao;
        this.problemDao = problemDao;
    }

    public ProblemData getProblemData(Integer problemId) {
        var data = new ProblemData();
        var files = new File(fileDir + "data/" + problemId).listFiles();
        var problem = problemDao.getById(problemId, false);

        try {
            if (files != null) {
                for (var file : files) {
                    var ext = FilenameUtils.getExtension(file.getName());

                    if (ext.equals("in") || ext.equals("out")) {
                        data.getTestData().add(new ProblemData.TestData(file.getName(), file.length()));
                    }

                    if (file.getName().equals("spj.so")) {
                        data.setSpj(true);
                    }

                    if (file.getName().equals("spj.cpp")) {
                        data.setSPJSource(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                    }
                }
            }
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        data.setPid(problemId);
        data.setTitle(problem.getTitle());
        data.getTestData().sort(Comparator.comparing(ProblemData.TestData::getFileName));

        return data;
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
            throw new GenericException(HttpStatus.BAD_REQUEST, "题目已开放，不准上传测试数据");
        }

        var testDataDir = fileDir + "data/";
        var dir = new File(testDataDir + problemId);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建目录");
        }

        for (MultipartFile file : files) {
            var fileName = file.getOriginalFilename();
            var dest = new File(dir.getAbsolutePath() + "/" + fileName);

            try {
                file.transferTo(dest);
                log.info("上传文件 {} ", dest.getAbsolutePath());
            } catch (IOException e) {
                throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
            throw new GenericException(HttpStatus.BAD_REQUEST, "题目已开放，不准删除测试数据");
        }

        var testDataDir = fileDir + "data/";
        var file = new File(testDataDir + problemId + "/" + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info("删除文件 {}", file.getAbsolutePath());
                return ResponseEntity.noContent().build();
            } else {
                throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
            }
        } else {
            log.warn("文件 {} 不存在", file.getAbsolutePath());
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }

    /**
     * 保存头像图片
     */
    public ResponseEntity<?> saveAvatar(Integer uid, MultipartFile file) {
        var avatarDir = fileDir + "image/avatar/";
        var dir = new File(avatarDir);
        var avatar = new File(avatarDir + uid + ".png");

        if (!dir.exists() && !dir.mkdirs()) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建目录");
        }

        try {
            file.transferTo(avatar);
            userDao.updateAvatar(uid);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ResponseEntity<?> saveProblemImage(MultipartFile file) {
        var originalName = file.getOriginalFilename();

        if (originalName == null) {
            return ResponseEntity.badRequest().build();
        }

        var dir = new File(fileDir + "image/problem/");

        if (!dir.exists() && !dir.mkdirs()) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建目录");
        }

        var ext = originalName.substring(originalName.lastIndexOf("."));
        var fileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;

        var image = new File(dir.getAbsolutePath() + "/" + fileName);

        try {
            file.transferTo(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileName);
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存 SPJ 代码并编译
     */
    public ResponseEntity<?> saveSPJ(SPJ spj) {
        var pid = spj.getPid();
        var source = spj.getSource();
        var dir = new File(fileDir + "data/" + pid);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建目录");
        }

        var file = new File(fileDir + "data/" + pid + "/spj.cpp");

        try {
            FileUtils.writeStringToFile(file, source, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "无法写入文件");
        }

        try {
            var process = processBuilder.command(
                    "g++",
                    "-fPIC",
                    "-shared",
                    "-o", dir.getAbsolutePath() + "/spj.so",
                    file.getAbsolutePath()
            ).start();

            if (process.waitFor() != 0) {
                var err = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
                throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, err);
            }
        } catch (Exception e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 移除 SPJ 代码和库文件
     *
     * @param pid 题目 Id
     */
    public ResponseEntity<?> removeSPJ(Integer pid) {
        try {
            FileUtils.delete(new File(fileDir + "data/" + pid + "/spj.so"));
            FileUtils.delete(new File(fileDir + "data/" + pid + "/spj.cpp"));
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
}
