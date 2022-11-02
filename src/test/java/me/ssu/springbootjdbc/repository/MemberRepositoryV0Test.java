package me.ssu.springbootjdbc.repository;

import me.ssu.springbootjdbc.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV0Test {

	MemberRepositoryV0 repository = new MemberRepositoryV0();

	@Test
	@DisplayName("회원 데이터 저장")
	void crud() throws SQLException {
		Member member = new Member("memberV0", 10000);
		repository.save(member);
	}
}