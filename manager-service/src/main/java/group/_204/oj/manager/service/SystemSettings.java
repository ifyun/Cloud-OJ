package group._204.oj.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Service
public class SystemSettings {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private boolean showRankingAfterEnded = false;
        private boolean showNotStartedContest = false;
    }

    @Value("${project.file-dir}")
    private String fileDir;

    @Resource
    private ObjectMapper objectMapper;

    private Config config = new Config();

    @PostConstruct
    public void init() {
        loadConfig();
        log.info("Load system config: {}", config.toString());
    }

    public void loadConfig() {
        File configFile = new File(fileDir + "config.json");

        if (!configFile.exists()) {
            return;
        }

        try {
            config = objectMapper.readValue(configFile, Config.class);
        } catch (IOException e) {
            log.error("Cannot load system config file: {}.", e.getMessage());
        }
    }

    public boolean setConfig(Config config) {
        this.config = config;
        log.info("Update system config: {}", config);
        File configFile = new File(fileDir + "config.json");

        try {
            FileWriter writer = new FileWriter(configFile, false);
            objectMapper.writeValue(writer, config);
            return true;
        } catch (IOException e) {
            log.error("Cannot save system config to file: {}", e.getMessage());
            return false;
        }
    }

    public Config getConfig() {
        return config;
    }
}
