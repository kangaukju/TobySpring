package com.naver.kinow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import com.naver.kinow.user.User;

public class UserDao {
	private DataSource dataSource;
		
	// connectionMaker은 읽기전용으로 thread safe 하다.
	// connectionMaker DI 주입.
	private ConnectionMaker connectionMaker;
	
	private JdbcContext jdbcContext;
	
	/*
	public void setJdbcContext(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}
	*/

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		
		// 궅이 인터페이스를 두지 않아도 될 만큼 긴밀한 관계를 갖는 
		// DAO클래스와 JdbcContext클래스를 어색하게 따로 빈으로 분리하지 않고
		// 내부에서 직접 만들어 사용하면서도 다른 오브젝트에 대한 DI를 적용할 수 있다.
		this.jdbcContext = new JdbcContext(); // JdbcContext생성(IoC)
		this.jdbcContext.setDataSource(dataSource); // 의존 오브젝트 주입(DI)
	}

	// setter ConnectionMaker DI
	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	// Context(변하지 않는)코드이다.
	// Context 메서드는 다른 클래스에서도 사용가능하다.
	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) 
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
	
	public void deleteAll() throws SQLException {
		/*
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement(
				"DELETE FROM users");
		ps.executeUpdate();
		ps.close();
		c.close();
		*/
		
		/*
		// jdbc 템플릿 전략 컨텍스트로 변경
		StatementStrategy st = new DeleteAllStatement();
		jdbcContextWithStatementStrategy(st);
		*/
		
		/*
		// 익명 클래스로 jdbc 템플릿 전략 컨텍스트로 변경 (아예 변수명도 없이...)
		jdbcContextWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				PreparedStatement ps = c.prepareStatement("DELETE FROM users");
				return ps;
			}
		});
		*/
		
		/*
		jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				PreparedStatement ps = c.prepareStatement("DELETE FROM users");
				return ps;
			}
		});
		*/
		
		// 익명 클래스로 읽기 불편해보이는 코드를 분리하였다.
		jdbcContext.executeSql("DELETE FROM users");
	}
	
	
	public int getCount() throws SQLException {
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"SELECT count(*) FROM users");
		ResultSet rs = ps.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		
		rs.close();
		ps.close();
		c.close();
		
		return count;
	}
	
	public void add(final User user) throws SQLException {
		/*
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement(
				"INSERT INTO users(id, name, password) VALUES (?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		ps.executeUpdate();
		ps.close();
		c.close();
		*/
		
		/*
		// jdbc 템플릿 전략 컨텍스트로 변경
		StatementStrategy st = new AddStatement(user);
		jdbcContextWithStatementStrategy(st);
		*/
		
		/*
		// 익명 클래스로 jdbc 템플릿 전략 컨텍스트로 변경
		StatementStrategy st = new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				PreparedStatement ps = c.prepareStatement(
						"INSERT INTO users(id, name, password) VALUES (?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		};
		jdbcContextWithStatementStrategy(st);
		*/
		
		/*
		// 익명 클래스로 jdbc 템플릿 전략 컨텍스트로 변경 (아예 변수명도 없이...)
		jdbcContextWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				PreparedStatement ps = c.prepareStatement(
						"INSERT INTO users(id, name, password) VALUES (?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		});
		*/
		
		/*
		jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) 
					throws SQLException {
				PreparedStatement ps = c.prepareStatement(
						"INSERT INTO users(id, name, password) VALUES (?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		});
		*/
		jdbcContext.executeSql(
				"INSERT INTO users(id, name, password) VALUES (?,?,?)", 
				user.getId(),
				user.getName(),
				user.getPassword());
	}
	
	public User get(String id) throws SQLException {
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement(
				"SELECT * FROM users WHERE id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		User user = null;
		if (rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		} 
		
		rs.close();
		ps.close();
		c.close();
		
		if (user == null) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return user;
	}
}
