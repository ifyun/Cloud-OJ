package cloud.oj.core.dao;

public interface InviteeDao {

    Boolean isInvited(Integer contestId, Integer uid);

    void inviteUser(Integer contestId, Integer uid);
}
