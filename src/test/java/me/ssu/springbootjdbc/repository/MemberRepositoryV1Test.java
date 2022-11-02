package me.ssu.springbootjdbc.repository;

import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static me.ssu.springbootjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

	MemberRepositoryV1 repository;

	// TODO 각 테스트가 실행 직전에 호출됨.
	@BeforeEach
	void beforeEach() {
		// 기본 DriverManager - 항상 새로운 커넥션 획득
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		repository = new MemberRepositoryV1(driverManagerDataSource);
	}

	@Test
	@DisplayName("회원 데이터 저장")
	void crud() throws SQLException {

		// save
		Member member = new Member("memberV5", 10000);
		repository.save(member);

		// delete from member;

		// select
		Member findMember = repository.findById(member.getMemberId());
		log.info("findByMember={}", findMember);
		assertThat(findMember).isEqualTo(member);

		// update
		repository.update(member.getMemberId(), 2000);
		Member updatedMember = repository.findById(member.getMemberId());
		assertThat(updatedMember.getMoney()).isEqualTo(2000);

		// delete
		repository.delete(member.getMemberId());
		// 삭제된 데이터이기 때문에 NoSuchElementException로 확인 가능
		assertThatThrownBy(() -> repository.findById(member.getMemberId()))
				.isInstanceOf(NoSuchElementException.class);
	}
}
