package com.naver.kinow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Connection을 입력받아 PreparedStatement를 반환하는 전략 인터페이 
public interface StatementStrategy {
	PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
