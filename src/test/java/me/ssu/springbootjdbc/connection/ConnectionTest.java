package me.ssu.springbootjdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

	@Test
	@DisplayName("DataSource Connection Pool")
	void dataSourceConnectionPool() throws SQLException, InterruptedException {
		// 커넥션 풀링
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setJdbcUrl(URL);
		hikariDataSource.setUsername(USERNAME);
		hikariDataSource.setPassword(PASSWORD);
		hikariDataSource.getMaximumPoolSize();
		hikariDataSource.setPoolName("MyPool");

		useDataSource(hikariDataSource);
		Thread.sleep(1000);         // 커넥션 풀에서 커넥션 생성 시간 대기
	}

	@Test
	@DisplayName("DriverManager를 통해 커넥션 획득하기")
	void driverManager() throws SQLException {
		Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		log.info("connection={}, class={}", connection1, connection1.getClass());
		log.info("connection={}, class={}", connection2, connection2.getClass());
	}

	@Test
	@DisplayName("스프링이 제공하는 DataSource가 적용된 DriverManager인 DriverManagerDataSource")
	void dataSourceDriverManager() throws SQLException {
		// DriverManagerDataSource - 항상 새로운 커넥션 획득
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		useDataSource(driverManagerDataSource);
	}

	private void useDataSource(DataSource dataSource) throws SQLException {
		Connection connection1 = dataSource.getConnection();
		Connection connection2 = dataSource.getConnection();

		log.info("connection={}, class={}", connection1, connection1.getClass());
		log.info("connection={}, class={}", connection2, connection2.getClass());
	}
}
