package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class Log {
    @JsonIgnore
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String service;
    private String instanceId;
    private String level;
    private String thread;
    private String className;
    private String message;
    private long time;

    /**
     * 全限定名过长时，转为缩写
     */
    private String shortenClassName(String className) {
        var len = className.length();
        if (len > 40) {
            var arr = className.split("\\.");
            for (int i = 0; i < arr.length - 1; i++) {
                if (arr[i].contains("[")) {
                    continue;
                }

                var iLen = arr[i].length();
                arr[i] = arr[i].substring(0, 1);
                len -= iLen - 1;

                if (len <= 40) {
                    break;
                }
            }

            return String.join(".", arr);
        }

        return className;
    }

    @Override
    public String toString() {
       return dateFormat.format(time) + " " +
                StringUtils.leftPad(level, 5) + " --- [" +
                StringUtils.leftPad(instanceId, 24) + "] [" +
                StringUtils.leftPad(StringUtils.right(thread, 15), 15) + "] " +
                StringUtils.rightPad(shortenClassName(className), 40) + " : " +
                message;
    }
}
