package cloud.oj.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class InviteeRepo {

    private final DatabaseClient client;

    public Mono<Boolean> isInvited(Integer cid, Integer uid) {
        return client.sql("""
                        select exists(
                            select 1
                            from invitee
                            where contest_id = :cid
                              and uid = :uid
                        )
                        """)
                .bind("cid", cid)
                .bind("uid", uid)
                .mapValue(Boolean.class)
                .first();
    }

    public Mono<Long> invite(Integer cid, Integer uid) {
        return client.sql("""
                        insert into invitee(contest_id, uid)
                        values (:cid, :uid)
                        """)
                .bind("cid", cid)
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }
}
