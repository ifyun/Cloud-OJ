package cloud.oj.judge.dao;

import cloud.oj.judge.model.Runtime;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuntimeDao {
    int add(Runtime runtime);
    void update(Runtime runtime);
}
