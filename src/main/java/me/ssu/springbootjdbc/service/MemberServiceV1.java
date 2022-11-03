package me.ssu.springbootjdbc.service;

import lombok.RequiredArgsConstructor;
import me.ssu.springbootjdbc.domain.Member;
import me.ssu.springbootjdbc.repository.MemberRepositoryV1;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

	private final MemberRepositoryV1 memberRepositoryV1;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		Member fromMember = memberRepositoryV1.findById(fromId);
		Member toMember = memberRepositoryV1.findById(toId);
		
		// 출금 계좌 
		memberRepositoryV1.update(fromId, fromMember.getMoney() - money);

		// 예외 상황 처리
		validation(toMember);

		// 입금 계좌 
		memberRepositoryV1.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}
}
