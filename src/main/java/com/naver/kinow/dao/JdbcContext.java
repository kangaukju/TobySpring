package com.naver.kinow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void executeSql(final String query, final Object... objects) throws SQLException {
		workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				int idx = 1;
				PreparedStatement ps = c.prepareStatement(query);
				for (Object obj : objects) {
					if (obj.getClass().isAssignableFrom(String.class)) {
						ps.setString(idx++, (String) obj);
//						System.out.println("get string : " + (String) obj);
					} else 
					if (obj.getClass().isAssignableFrom(Integer.class)) {
						ps.setInt(idx++, (Integer) obj);
//						System.out.println("get int : " + (Integer) obj);
					}
				}
				return ps;				
			}
		});
	}
	
	public void executeSql(final String query) throws SQLException {
		workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				PreparedStatement ps = c.prepareStatement(query);
				return ps;				
			}
		});
	}
	
	private void workWithStatementStrategy(StatementStrategy stmt) 
		throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = dataSource.getConnection();
			ps = stmt.makePreparedStatement(c);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
	}
}
