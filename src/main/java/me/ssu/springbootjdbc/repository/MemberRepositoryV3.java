package me.ssu.springbootjdbc.repository;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection()
 * DataSourceUtils.releaseConnection()
 */
@Slf4j
public class MemberRepositoryV3 {

	private Connection getConnection() throws SQLException {
		// TODO 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
		Connection connection = DataSourceUtils.getConnection(dataSource);
		log.info("get connection={}, class={}", connection, connection.getClass());
		return connection;
	}

	private void close(Connection connection, Statement statement, ResultSet resultSet) {
		JdbcUtils.closeResultSet(resultSet);
		JdbcUtils.closeStatement(statement);
		// TODO 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
		DataSourceUtils.releaseConnection(connection, dataSource);
		// JdbcUtils.closeConnection(connection);
	}

	// TODO 1) 주입
	private final DataSource dataSource;

	// TODO 2) 생성자
	public MemberRepositoryV3(DataSource dataSource) {
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
}