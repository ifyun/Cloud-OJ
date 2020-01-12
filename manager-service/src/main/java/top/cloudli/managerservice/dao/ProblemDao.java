package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.Problem;

import java.util.List;

@Mapper
public interface ProblemDao {
    List<Problem> getAll();

    List<Problem> getEnable();

    List<Problem> searchByTitle(String title);

    int add(Problem problem);

    int update(Problem problem);

    int delete(int problemId);
}
