package me.ssu.springbootjdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동과 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

	private final DataSource dataSource;
	private final MemberRepositoryV2 memberRepositoryV2;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {

		Connection connection = dataSource.getConnection();

		try {
			connection.setAutoCommit(false);    // 트랜잭션 시작(수동 모드)

			// 비즈니스 로직 수행
			Member fromMember = memberRepositoryV2.findById(connection, fromId);
			Member toMember = memberRepositoryV2.findById(connection, toId);

			// 출금 계좌
			memberRepositoryV2.update(connection, fromId, fromMember.getMoney() - money);

			// 예외 상황 처리
			validation(toMember);

			// 입금 계좌
			memberRepositoryV2.update(connection, toId, toMember.getMoney() + money);

			connection.commit();    // 성공시 커밋
		} catch (Exception e) {
			connection.rollback();  // 실패시 롤백
			throw new IllegalStateException(e);
		} finally {
			if (connection != null) {
				try {
					// 오토 커밋을 해서 커넥션 풀로 돌아가기 때문에 그냥 커넥션 종료를 하면 안 된다.
					connection.setAutoCommit(true); // 커넥션 풀 고래(자동 모드)
					connection.close();
				} catch (Exception e) {
					log.info("error", e);
				}
			}
		}
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}
}
