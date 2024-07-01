package cloud.oj.core.service;

import cloud.oj.core.config.AppConfig;
import cloud.oj.core.entity.ProblemData;
import cloud.oj.core.entity.SPJ;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.ProblemRepo;
import cloud.oj.core.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final UserRepo userRepo;

    private final ProblemRepo problemRepo;

    private final ProcessBuilder processBuilder = new ProcessBuilder();

    public FileService(AppConfig appConfig, UserRepo userRepo, ProblemRepo problemRepo) {
        this.fileDir = appConfig.getFileDir();
        this.userRepo = userRepo;
        this.problemRepo = problemRepo;
    }

    /**
     * 获取题目的测试数据
     *
     * @return {@link ProblemData}
     */
    public ProblemData getProblemData(Integer pid) {
        var data = new ProblemData();
        var files = new File(fileDir + "data/" + pid).listFiles();
        var problem = problemRepo.selectById(pid, false);
        var dataConf = problemRepo.selectDataConf(pid);

        if (problem.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "题目不存在");
        }

        try {
            if (files != null) {
                for (var file : files) {
                    var ext = FilenameUtils.getExtension(file.getName());

                    if (ext.equals("in")) {
                        data.getTestData().add(new ProblemData.TestData(file.getName(), file.length()));
                        continue;
                    }

                    if (ext.equals("out")) {
                        var testData = new ProblemData.TestData(file.getName(), file.length());
                        dataConf.ifPresent(conf -> testData.setScore(conf.get(file.getName())));
                        data.getTestData().add(testData);
                        continue;
                    }

                    if (file.getName().equals("spj.so")) {
                        data.setSpj(true);
                        continue;
                    }

                    if (file.getName().equals("spj.cpp")) {
                        data.setSPJSource(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                    }
                }
            }
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        data.setPid(pid);
        data.setTitle(problem.get().getTitle());
        data.getTestData().sort(Comparator.comparing(ProblemData.TestData::getFileName));

        return data;
    }

    /**
     * 保存测试数据
     *
     * @param pid   题目 Id
     * @param files 文件
     */
    public void saveTestData(Integer pid, MultipartFile[] files) {
        if (files.length == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "没有文件");
        }

        // ! 开放的题目不允许上传测试数据
        var enable = problemRepo.isEnable(pid);

        if (enable.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "题目不存在");
        }

        if (enable.get()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "题目已开放，不准上传测试数据");
        }

        var testDataDir = fileDir + "data/";
        var dir = new File(testDataDir + pid);

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
    }

    /**
     * 删除测试数据
     *
     * @param pid  题目 Id
     * @param name 文件名
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTestData(Integer pid, String name) {
        // ! 开放的题目不允许删除测试数据
        var enable = problemRepo.isEnable(pid);

        if (enable.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "题目不存在");
        }

        if (enable.get()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "题目已开放，不准删除测试数据");
        }

        // ! 同步修改测试数据配置
        problemRepo.removeFromDataConf(pid, name);

        var testDataDir = fileDir + "data/";
        var file = new File(testDataDir + pid + "/" + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info("删除文件 {}", file.getAbsolutePath());
            } else {
                throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
            }
        } else {
            log.warn("文件 {} 不存在", file.getAbsolutePath());
            throw new GenericException(HttpStatus.GONE, "文件不存在");
        }
    }

    /**
     * 保存头像图片
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAvatar(Integer uid, MultipartFile file) {
        var avatarDir = fileDir + "image/avatar/";
        var dir = new File(avatarDir);
        var avatar = new File(avatarDir + uid + ".png");

        if (!dir.exists() && !dir.mkdirs()) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建目录");
        }

        try {
            if (userRepo.updateAvatar(uid) == 0) {
                throw new GenericException(HttpStatus.BAD_REQUEST, "更新头像失败");
            }

            file.transferTo(avatar);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, "更新头像失败");
        }
    }

    /**
     * 保存题目图片
     *
     * @param file 文件
     * @return 文件名
     */
    public String saveProblemImage(MultipartFile file) {
        var originalName = file.getOriginalFilename();

        if (originalName == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "文件为空");
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
            return fileName;
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存 SPJ 代码并编译
     *
     * @param spj {@link SPJ}
     */
    public void saveSPJ(SPJ spj) {
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
    }

    /**
     * 移除 SPJ 代码和库文件
     *
     * @param pid 题目 Id
     */
    public void removeSPJ(Integer pid) {
        try {
            FileUtils.delete(new File(fileDir + "data/" + pid + "/spj.so"));
            FileUtils.delete(new File(fileDir + "data/" + pid + "/spj.cpp"));
        } catch (IOException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
