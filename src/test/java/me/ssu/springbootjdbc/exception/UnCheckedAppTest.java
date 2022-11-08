package me.ssu.springbootjdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

	@Test
	@DisplayName("런타임 예외 사용 변환 테스트")
	void unchecked() {
		Controller controller = new Controller();
		assertThatThrownBy(() -> controller.request())
				.isInstanceOf(Exception.class);
	}

	@Test
	void printEx() {
		Controller controller = new Controller();
		try {
			controller.request();
		} catch (Exception e) {
			//e.printStackTrace();
			log.info("ex", e);
		}
	}

	static class Controller {
		Service service = new Service();
		public void request() {
			service.logic();
		}
	}

	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();
		// 컴파일러가 체크 하지 않은 언체크 예외임.
		public void logic() {
			repository.call();
			networkClient.call();
		}
	}

	/**
	 * SQLException
	 */
	static class RuntimeSQLException extends RuntimeException {
		public RuntimeSQLException() {}
		public RuntimeSQLException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * ConnectException
	 */
	static class RuntimeConnectException extends RuntimeException {
		public RuntimeConnectException(String message) {
			super(message);
		}
	}

	static class NetworkClient {
		public void call() {
			throw new RuntimeConnectException("연결 실패");
		}
	}

	static class Repository {
		public void call() {
			try {
				// SQL 호출
				runSQL();
			} catch (SQLException e) {
				// SQLException을 RuntimeSQLException으로 변경해서 던짐
				// 예외를 던질 때 항상 기존 예외를 갖고 있어야 된다.
				throw new RuntimeSQLException(e);
			}
		}
		// SQL 실행
		private void runSQL() throws SQLException {
			throw new SQLException("ex");
		}
	}
}