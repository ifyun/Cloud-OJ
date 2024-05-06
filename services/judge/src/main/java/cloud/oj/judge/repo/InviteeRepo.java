package cloud.oj.judge.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InviteeRepo {

    private final JdbcClient client;

    /**
     * 检查用户是否已加入竞赛
     *
     * @param cid 竞赛 Id
     * @param uid 用户 Id
     * @return {@link Boolean}
     */
    public Boolean checkInvitee(Integer cid, Integer uid) {
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
}
