package me.ssu.springbootjdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedAppTest {

	@Test
	@DisplayName("체크 예외 문제점 테스트")
	void checked() {
		Controller controller = new Controller();
		assertThatThrownBy(() -> controller.request())
				.isInstanceOf(Exception.class);
	}

	/**
	 * 컨트롤러, 처리를 못하기에 던져야 한다.
	 */
	static class Controller {
		Service service = new Service();
		public void request() throws SQLException, ConnectException {
			service.logic();
		}
	}

	/**
	 * 서비스, 둘다 처리할 수 없기에 던져야 한다.
	 */
	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();
		public void logic() throws SQLException, ConnectException {
			repository.call();
			networkClient.call();
		}
	}

	static class NetworkClient {
		public void call() throws ConnectException {
			throw new ConnectException("연결 실패"); }
	}

	static class Repository {
		public void call() throws SQLException {
			throw new SQLException("ex");
		}
	}
}