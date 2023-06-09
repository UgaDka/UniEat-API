package foorun.unieat.api.model.database.member.repository;

import foorun.unieat.api.model.database.member.entity.UniEatMemberMyPageEntity;
import foorun.unieat.api.model.database.member.entity.primary_key.UniEatMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniEatMemberMyPageRepository extends JpaRepository<UniEatMemberMyPageEntity, UniEatMemberId> {
}