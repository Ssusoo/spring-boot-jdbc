package me.ssu.springbootjdbc.repository;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

	MemberRepositoryV0 repository = new MemberRepositoryV0();

	@Test
	@DisplayName("회원 데이터 저장")
	void crud() throws SQLException {

		// save
		Member member = new Member("memberV0", 10000);
		repository.save(member);

		// delete from member;

		// select
		Member findMember = repository.findById(member.getMemberId());
		log.info("findByMember={}", findMember);
		assertThat(findMember).isEqualTo(member);
	}
}