package me.ssu.springbootjdbc.service;

import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 기본 동작, 트랜잭션이 없어서 문제발생
 */
class MemberServiceV1Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	private MemberRepositoryV1 memberRepositoryV1;
	private MemberServiceV1 memberServiceV1;

	@BeforeEach
	void before() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		memberRepositoryV1 = new MemberRepositoryV1(driverManagerDataSource);
		memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() throws SQLException {
		// given(상황)
		Member memberA = new Member(MEMBER_A, 10000);
		Member memberB = new Member(MEMBER_B, 10000);
		memberRepositoryV1.save(memberA);
		memberRepositoryV1.save(memberB);

		// when(수행)
		memberServiceV1.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

		// then(검증)
		Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());    // 8000
		Member findMemberB = memberRepositoryV1.findById(memberB.getMemberId());    // 12000

		assertThat(findMemberA.getMoney()).isEqualTo(8000);
		assertThat(findMemberB.getMoney()).isEqualTo(12000);
	}
}