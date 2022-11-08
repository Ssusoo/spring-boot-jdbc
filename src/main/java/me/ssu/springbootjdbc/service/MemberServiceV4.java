package me.ssu.springbootjdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepository;
import me.ssu.springbootjdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스 의존
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {

	private final MemberRepository memberRepository;

	// 트랜잭션널 애노테이션 : 이 메소드가 성공하면 커밋 실패하면 롤백하겠다.
	@Transactional
	public void accountTransfer(String fromId, String toId, int money) {
		bizLogic(fromId, toId, money);  // 비즈니스 로직
	}

	private void bizLogic(String fromId, String toId, int money) {
		// 비즈니스 로직 수행
		Member fromMember = memberRepository.findById(fromId);
		Member toMember = memberRepository.findById(toId);

		// 출금 계좌
		memberRepository.update(fromId, fromMember.getMoney() - money);

		// 예외 상황 처리
		validation(toMember);

		// 입금 계좌
		memberRepository.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}
}