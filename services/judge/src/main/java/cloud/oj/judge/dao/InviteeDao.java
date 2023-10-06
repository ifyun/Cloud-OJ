package cloud.oj.judge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InviteeDao {

    @Select("""
            select 1
            from invitee
            where contest_id = #{contestId}
            and uid = #{uid}
            """)
    int checkInvitee(Integer contestId, Integer uid);
}
