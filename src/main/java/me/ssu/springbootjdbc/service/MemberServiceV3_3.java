package me.ssu.springbootjdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP 적용
 */
@Slf4j
public class MemberServiceV3_3 {

	private final MemberRepositoryV3 memberRepositoryV3;

	public MemberServiceV3_3(MemberRepositoryV3 memberRepositoryV3) {
		this.memberRepositoryV3 = memberRepositoryV3;
	}

	// 트랜잭션널 애노테이션 : 이 메소드가 성공하면 커밋 실패하면 롤백하겠다.
	@Transactional
	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		bizLogic(fromId, toId, money);  // 비즈니스 로직
	}

	private void bizLogic(String fromId, String toId, int money) throws SQLException {
		// 비즈니스 로직 수행
		Member fromMember = memberRepositoryV3.findById(fromId);
		Member toMember = memberRepositoryV3.findById(toId);

		// 출금 계좌
		memberRepositoryV3.update(fromId, fromMember.getMoney() - money);

		// 예외 상황 처리
		validation(toMember);

		// 입금 계좌
		memberRepositoryV3.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}
}