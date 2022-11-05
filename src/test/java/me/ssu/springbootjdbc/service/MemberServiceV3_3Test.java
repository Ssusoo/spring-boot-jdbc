package me.ssu.springbootjdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - @Transactional AOP 적용
 */
@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	@Autowired
	private MemberRepositoryV3 memberRepository;

	@Autowired
	private MemberServiceV3_3 memberService;

	@Test
	@DisplayName("AOP 프록시 적용 확인")
	void AopCheck() {
		log.info("memberService class={}", memberService.getClass());
		log.info("memberRepository class={}", memberRepository.getClass());
		// 트랜잭션 AOP 적용 확인 유무
		assertThat(AopUtils.isAopProxy(memberService)).isTrue();
		// 실패시 정상 롤백
		assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		DataSource dataSource() {
			return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		}

		// 트랜잭션 프록시 역시 트랜잭션 매니저를 불러서 써야하기에
		@Bean
		PlatformTransactionManager platformTransactionManager() {
			return new DataSourceTransactionManager(dataSource());
		}

		// 리포지토리, DataSource의 정보를 가져온다.
		@Bean
		MemberRepositoryV3 memberRepositoryV3() {
			return new MemberRepositoryV3(dataSource());
		}

		// 서비스 계층, 리포지토리의 정보를 가져와서 처리한다.
		@Bean
		MemberServiceV3_3 memberServiceV3_3() {
			return new MemberServiceV3_3(memberRepositoryV3());
		}
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