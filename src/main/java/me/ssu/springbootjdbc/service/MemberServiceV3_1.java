package me.ssu.springbootjdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

	private final PlatformTransactionManager platformTransactionManager;
	private final MemberRepositoryV3 memberRepositoryV3;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 트랜잭션 시작
		TransactionStatus transactionStatus = platformTransactionManager
				.getTransaction(new DefaultTransactionDefinition());
		try {
			bizLogic(fromId, toId, money);  // 비즈니스 로직
			platformTransactionManager.commit(transactionStatus);   // 성공시 커밋
		} catch (Exception e) {
			// 실패시 롤백
			// connection.rollback();
			platformTransactionManager.rollback(transactionStatus);
			throw new IllegalStateException(e);
		}
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