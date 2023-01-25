package cloud.oj.judge.dao;

import cloud.oj.judge.entity.Runtime;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuntimeDao {
    void add(Runtime runtime);
    void update(Runtime runtime);
}
