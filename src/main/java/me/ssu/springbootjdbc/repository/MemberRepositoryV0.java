package me.ssu.springbootjdbc.repository;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.connection.DBConnectionUtil;
import me.ssu.springbootjdbc.domain.Member;

import java.sql.*;

/**
 * JDBC -DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

	public Member save(Member member) throws SQLException {
		String sql = "insert into member(member_id, money) values(?, ?)";

		Connection connection = null;
		// PreparedStatement는 Statement의 자식 티입. ?를 통한 파라미터 바인딩을 가능하게 해줌.
		PreparedStatement preparedStatement = null;

		try {
			// TODO 1) 커넥션 연결
			connection = getConnection();

			// TODO 2) Connection을 통해 SQL 전달
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, member.getMemberId());
			preparedStatement.setInt(2, member.getMoney());

			// TODO 3) SQL 실행
			preparedStatement.executeUpdate();  // Statement를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달

			return member;
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			// TODO TCP / IP 연결 끊기(쿼리 실행 후 리소스 정리)
			close(connection, preparedStatement, null);
		}
	}

	private void close(Connection connection, Statement statement, ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				log.info("error", e);
			}
		}

		if (statement != null) {
			try {
				statement.close();      // Exception 터지면
			} catch (SQLException e) {
				log.info("error", e);
			}
		}

		if (connection != null) {
			try {
				connection.close();             // Connection.close 호출이 안 될 수 있다.
			} catch (SQLException e) {
				log.info("error", e);
			}

		}
	}


	private Connection getConnection() {
		return DBConnectionUtil.getConnection();
	}
}
