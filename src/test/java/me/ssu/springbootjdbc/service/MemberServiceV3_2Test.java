package me.ssu.springbootjdbc.service;

import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
class MemberServiceV3_2Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	private MemberRepositoryV3 memberRepository;
	private MemberServiceV3_2 memberService;

	// TODO 각 테스트가 실행 직전에 호출됨.
	@BeforeEach
	void before() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		memberRepository = new MemberRepositoryV3(driverManagerDataSource);
		PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(driverManagerDataSource);
		memberService = new MemberServiceV3_2(platformTransactionManager, memberRepository);
	}

	// TODO 각 테스트가 실행 후 호출 됨.
	@AfterEach
	void after() {
		memberRepository.delete(MEMBER_A);
		memberRepository.delete(MEMBER_B);
		memberRepository.delete(MEMBER_EX);
	}

	@Test
	@DisplayName("이체 중 예외 발생")
	void accountTransferEx() throws SQLException {
		// given(상황)
		Member memberA = new Member(MEMBER_A, 10000);
		Member memberEx = new Member(MEMBER_EX, 10000);
		memberRepository.save(memberA);
		memberRepository.save(memberEx);

		// when(수행) : 예외가 발생하는지 검증
		// 결국, 출금한 A의 돈만 2000원이 차감되고 입금처리가 안 된 상태가 된다.
		// 1) 출금 처리 2) 예외 처리 3) 입금 처리이기에
		assertThatThrownBy(() -> memberService
				.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
				.isInstanceOf(IllegalStateException.class);

		// then(검증)
		Member findMemberA = memberRepository.findById(memberA.getMemberId());    // 8000
		Member findMemberEx = memberRepository.findById(memberEx.getMemberId());    // 10000

		assertThat(findMemberA.getMoney()).isEqualTo(10000);    // 출금을 통해 8000원이었는데, 예외 발생 후
																// 롤백이 됐기에 10000원이 됨.
		assertThat(findMemberEx.getMoney()).isEqualTo(10000);   // 입금되기 전에 예외가 터졌기에 출금처리만 된 상황
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() throws SQLException {
		// given(상황)
		Member memberA = new Member(MEMBER_A, 10000);
		Member memberB = new Member(MEMBER_B, 10000);
		memberRepository.save(memberA);
		memberRepository.save(memberB);

		// when(수행)
		memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

		// then(검증)
		Member findMemberA = memberRepository.findById(memberA.getMemberId());    // 8000
		Member findMemberB = memberRepository.findById(memberB.getMemberId());    // 12000

		assertThat(findMemberA.getMoney()).isEqualTo(8000);
		assertThat(findMemberB.getMoney()).isEqualTo(12000);
	}

}