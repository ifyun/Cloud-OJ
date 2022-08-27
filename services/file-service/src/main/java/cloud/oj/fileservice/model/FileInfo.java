package cloud.oj.fileservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    private String path;
    private long lastModified;
    private Boolean deleted = false;

    public FileInfo(String path, long lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }
}
