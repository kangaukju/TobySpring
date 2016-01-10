package spring.learning.jdbcTemplete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.naver.kinow.user.User;

public class UserDao {

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void deleteAll() {
		/*
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("DELETE FROM users");
			}
		});
		*/
		this.jdbcTemplate.update("DELETE FROM users");
	}
	
	public void add(User user) {
		this.jdbcTemplate.update(
				"INSERT INTO users(id, name, password) VALUES (?,?,?)",
				user.getId(), user.getName(), user.getPassword());
	}
	
	public int getCount() {
		/*
		return this.jdbcTemplate.query(
				// 첫 번째 콜백 statement 생
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						return con.prepareStatement("SELECT count(*) FROM users");
					}
				}, 
				// 두 번째 콜백, ResultSet으로부터 값 추출 
				new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						rs.next();
						return rs.getInt(1);
					}
				});
		*/
		return this.jdbcTemplate.queryForInt("SELECT count(*) FROM users");
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(
				"SELECT * FROM users WHERE id = ?", 
				new Object[] {id},
				new RowMapper<User>() {
					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new User(
								rs.getString("id"),
								rs.getString("name"),
								rs.getString("password"));
					}
				});
	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query(
				"SELECT * FROM users order by id", 
				new RowMapper<User>() {
					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new User(
								rs.getString("id"),
								rs.getString("name"),
								rs.getString("password"));
					}					
				});
	}
	
}
