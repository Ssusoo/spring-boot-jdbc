package me.ssu.springbootjdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
	// TODO 1) 자바에서 제공하는 ConnectionPool
	public static Connection getConnection() {
		try {
			// TODO 2)  JDBC가 제공하는 드라이버(데이터베이스를 연결하기 위해)
			Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			log.info("get connection={}, class={}",
					connection,
					connection.getClass()   // 클래스 정보(connection.getClass)
			);
			return connection;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}