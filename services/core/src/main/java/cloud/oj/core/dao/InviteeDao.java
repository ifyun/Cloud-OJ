package cloud.oj.core.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InviteeDao {

    @Select("""
            select exists(
                select 1
                from invitee
                where contest_id = #{contestId}
                  and uid = #{uid}
            )
            """)
    Boolean isInvited(Integer contestId, Integer uid);

    @Insert("""
            insert into invitee(contest_id, uid)
            value (#{contestId}, #{uid})
            """)
    void inviteUser(Integer contestId, Integer uid);
}
