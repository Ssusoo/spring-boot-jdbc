package me.ssu.springbootjdbc.repository;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.connection.DBConnectionUtil;
import me.ssu.springbootjdbc.domain.Member;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC -DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

	public void update(String memberId, int money) throws SQLException {
		String sql = "update member set money=? where member_id=?";

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, money);
			preparedStatement.setString(2, memberId);

			int resultSize = preparedStatement.executeUpdate();

			log.info("resultSize={}", resultSize);
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(connection, preparedStatement, null);
		}
	}

	public Member findById(String memberId) throws SQLException {
		String sql = "select * from member where member_id = ?";

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// TODO 1) 커넥션 연결
			connection = getConnection();

			// TODO 2) Connection을 통해 SQL 전달
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, memberId);

			// TODO 3) SQL 실행(조회할 때 executeQuery(), 결과를 ResultSet에 담아서 반환함)
			resultSet = preparedStatement.executeQuery();

			// TODO 4) rs.next() : 이것을 호출하면 커서가 다음으로 이동한다
			// rs.next() 의 결과가 true 면 커서의 이동 결과 데이터가 있다는 뜻이다.
			// rs.next() 의 결과가 false 면 더이상 커서가 가리키는 데이터가 없다는 뜻이다.
			if (resultSet.next()) {
				Member member = new Member();
				member.setMemberId(resultSet.getString("member_id"));
				member.setMoney(resultSet.getInt("money"));

				return member;
			} else {
				throw new NoSuchElementException("member not found memberId=" + memberId);
			}
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(connection, preparedStatement, resultSet);
		}

	}

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

			// TODO 3) SQL 실행(데이터를 변경할 때 executeUpdate())
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
