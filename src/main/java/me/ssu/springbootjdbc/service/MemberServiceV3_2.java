package me.ssu.springbootjdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

	private final TransactionTemplate transactionTemplate;
	private final MemberRepositoryV3 memberRepositoryV3;

	// TransactionTemplate 대신 PlatformTransactionManager을 주입받는다.
	public MemberServiceV3_2(PlatformTransactionManager platformTransactionManager, MemberRepositoryV3 memberRepositoryV3) {
		// TransactionTemplate 클래스이기에 유연성이 없어 PlatformTransactionManager로 사용
		this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
		this.memberRepositoryV3 = memberRepositoryV3;
	}

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 트랜잭션 시작과 롤백 수행
		transactionTemplate.executeWithoutResult((transactionStatus) -> {
			try {
				bizLogic(fromId, toId, money);  // 비즈니스 로직
			} catch (SQLException e) {
				throw new IllegalStateException(e);
			}
		});
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