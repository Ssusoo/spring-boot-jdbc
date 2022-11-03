package me.ssu.springbootjdbc.repository;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 커넥션을 파라미터로 넘기는 예제
 */
@Slf4j
public class MemberRepositoryV2 {

	/**
	 * 수정 - 커넥션을 파라미터로 받은 경우
	 * @param connection
	 * @param memberId
	 * @param money
	 * @throws SQLException
	 */
	public void update(Connection connection, String memberId, int money) throws SQLException {
		String sql = "update member set money=? where member_id=?";

		// Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			// TODO 1) 커넥션 연결 없애기(파라미터로 커넥션을 받을 경우)
			// connection = getConnection();

			// TODO 2) Connection을 통해 SQL 전달
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, money);
			preparedStatement.setString(2, memberId);

			// TODO 3) SQL 실행(업데이트할 때 executeUpdate())
			int resultSize = preparedStatement.executeUpdate();

			log.info("resultSize={}", resultSize);
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			JdbcUtils.closeConnection(connection);
			// TODO 커넥션을 파라미터로 받은 경우(close하면 안 됨 : 서비스에서 처리해주어야 함)
			// JdbcUtils.closeStatement(statement);
		}
	}

	/**
	 * 조회 - 커넥션을 파라미터로 받은 경우
	 * @param connection
	 * @param memberId
	 * @return
	 * @throws SQLException
	 */
	public Member findById(Connection connection, String memberId) throws SQLException {
		String sql = "select * from member where member_id = ?";

		// Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// TODO 1) 커넥션 연결 없애기(파라미터로 커넥션을 받을 경우)
			// connection = getConnection();

			// TODO 2) Connection을 통해 SQL 전달
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, memberId);

			// TODO 3) SQL 실행(조회할 때 executeQuery(), 결과를 ResultSet에 담아서 반환함)
			resultSet = preparedStatement.executeQuery();

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
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeConnection(connection);
			// TODO 커넥션을 파라미터로 받은 경우(close하면 안 됨 : 서비스에서 처리해주어야 함)
			// JdbcUtils.closeStatement(statement);
		}
	}

	// TODO 1) 주입
	private final DataSource dataSource;

	// TODO 2) 생성자
	public MemberRepositoryV2(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void delete(String memberId) {
		String sql = "delete from member where member_id=?";

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = getConnection();

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, memberId);

			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			log.error("db error", e);
		} finally {
			close(connection, preparedStatement, null);
		}
	}

	public void update(String memberId, int money) throws SQLException {
		String sql = "update member set money=? where member_id=?";

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			// TODO 1) 커넥션 연결
			connection = getConnection();

			// TODO 2) Connection을 통해 SQL 전달
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, money);
			preparedStatement.setString(2, memberId);

			// TODO 3) SQL 실행(업데이트할 때 executeUpdate())
			int resultSize = preparedStatement.executeUpdate();

			log.info("resultSize={}", resultSize);
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(connection, preparedStatement, null);
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
		JdbcUtils.closeResultSet(resultSet);
		JdbcUtils.closeConnection(connection);
		JdbcUtils.closeStatement(statement);
	}

	private Connection getConnection() throws SQLException {
		Connection connection = dataSource.getConnection();
		log.info("get connection={}, class={}", connection, connection.getClass());
		return connection;
	}
}