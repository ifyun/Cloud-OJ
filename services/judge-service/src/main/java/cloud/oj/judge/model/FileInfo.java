package cloud.oj.judge.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileInfo implements Comparable<FileInfo> {
    private String path;
    private long lastModified;
    private boolean deleted;

    public FileInfo(String path, long lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }

    @Override
    public int compareTo(FileInfo other) {
        return other.getPath().compareTo(path);
    }
}
