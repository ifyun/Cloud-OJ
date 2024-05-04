package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InviteeRepo {

    private final JdbcClient client;

    public Boolean isInvited(Integer cid, Integer uid) {
        return client.sql("""
                        select exists(
                            select 1
                            from invitee
                            where contest_id = :cid
                              and uid = :uid
                        )
                        """)
                .param("cid", cid)
                .param("uid", uid)
                .query(Boolean.class)
                .single();
    }

    public Integer invite(Integer cid, Integer uid) {
        return client.sql("""
                        insert into invitee(contest_id, uid)
                        values (:cid, :uid)
                        """)
                .param("cid", cid)
                .param("uid", uid)
                .update();
    }
}
