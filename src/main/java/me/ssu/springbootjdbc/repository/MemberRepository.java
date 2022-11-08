package me.ssu.springbootjdbc.repository;

import me.ssu.springbootjdbc.domain.Member;

/**
 * 특정 기술에 종속되지 않는 순수한 인터페이스
 */
public interface MemberRepository {
	Member save(Member member);
	Member findById(String memberId);
	void update(String memberId, int money);
	void delete(String memberId);
}
