package me.ssu.springbootjdbc.exception.translator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.exception.MyDbException;
import me.ssu.springbootjdbc.repository.exception.MyDuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

	Repository repository;
	Service service;

	@BeforeEach
	void init() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		repository = new Repository(driverManagerDataSource);
		service = new Service(repository);
	}

	@Test
	@DisplayName("키 중복 오류 코드 테스트")
	void duplicateKeySave() {
		service.create("myId");
		service.create("myId"); // 같은 id
	}

	@Slf4j
	@RequiredArgsConstructor
	static class Service {
		private final Repository repository;

		public void create(String memberId) {
			try {
				repository.save(new Member(memberId, 0));
				log.info("saveId={}", memberId);
			} catch (MyDuplicateKeyException e) {
				log.info("키 중복, 복구 시도");
				String retryId = generateNewId(memberId);
				log.info("retryId={}", retryId);
				repository.save(new Member(retryId, 0));
			} catch (MyDbException e) {
				log.info("데이터 접근 계층 예외", e);
				throw e;
			}
		}
		private String generateNewId(String memberId) {
			return memberId + new Random().nextInt(100000);
		}
	}

	@RequiredArgsConstructor
	static class Repository {
		private final DataSource dataSource;

		public Member save(Member member) {
			String sql = "insert into member(member_id, money) values(?, ?)";
			Connection connection = null;
			PreparedStatement preparedStatement = null;

			try {
				connection = dataSource.getConnection();
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, member.getMemberId());
				preparedStatement.setInt(2, member.getMoney());
				preparedStatement.executeUpdate();
				return member;
			} catch (SQLException e) {
				// h2 Db
				if (e.getErrorCode() == 23505) {
					throw new MyDuplicateKeyException(e);
				}
				throw new MyDbException(e);
			} finally {
				JdbcUtils.closeStatement(preparedStatement);
				JdbcUtils.closeConnection(connection);
			}
		}
	}
}