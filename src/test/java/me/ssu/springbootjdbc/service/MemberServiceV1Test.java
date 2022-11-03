package me.ssu.springbootjdbc.service;

import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

	// TODO 각 테스트가 실행 직전에 호출됨.
	@BeforeEach
	void before() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		memberRepositoryV1 = new MemberRepositoryV1(driverManagerDataSource);
		memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
	}

	// TODO 각 테스트가 실행 후 호출 됨.
	@AfterEach
	void after() {
		memberRepositoryV1.delete(MEMBER_A);
		memberRepositoryV1.delete(MEMBER_B);
		memberRepositoryV1.delete(MEMBER_EX);
	}

	@Test
	@DisplayName("이체 중 예외 발생")
	void accountTransferEx() throws SQLException {
		// given(상황)
		Member memberA = new Member(MEMBER_A, 10000);
		Member memberEx = new Member(MEMBER_EX, 10000);
		memberRepositoryV1.save(memberA);
		memberRepositoryV1.save(memberEx);

		// when(수행) : 예외가 발생하는지 검증
		// 결국, 출금한 A의 돈만 2000원이 차감되고 입금처리가 안 된 상태가 된다.
		// 1) 출금 처리 2) 예외 처리 3) 입금 처리이기에
		assertThatThrownBy(() -> memberServiceV1
				.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
				.isInstanceOf(IllegalStateException.class);

		// then(검증)
		Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());    // 8000
		Member findMemberEx = memberRepositoryV1.findById(memberEx.getMemberId());    // 10000

		assertThat(findMemberA.getMoney()).isEqualTo(8000);
		assertThat(findMemberEx.getMoney()).isEqualTo(10000);   // 입금되기 전에 예외가 터졌기에 출금처리만 된 상황
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