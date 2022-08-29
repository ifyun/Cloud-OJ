package cloud.oj.fileservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    private String path;
    private Long lastModified;
    private Boolean deleted = false;

    public FileInfo(String path, Long lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }
}
