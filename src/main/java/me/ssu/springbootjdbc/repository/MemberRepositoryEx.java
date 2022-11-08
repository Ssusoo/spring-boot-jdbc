package me.ssu.springbootjdbc.repository;

import me.ssu.springbootjdbc.domain.Member;

import java.sql.SQLException;

/**
 * 체크 예외 코드에 인터페이스 도입시 문제점
 */
public interface MemberRepositoryEx {
	Member save(Member member) throws SQLException;
	Member findById(String memberId) throws SQLException;
	void update(String memberId, int money) throws SQLException;
	void delete(String memberId) throws SQLException;
}