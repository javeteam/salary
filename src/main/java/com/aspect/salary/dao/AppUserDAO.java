package com.aspect.salary.dao;

import com.aspect.salary.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
@Transactional
public class AppUserDAO extends JdbcDaoSupport {

    @Autowired
    public AppUserDAO(DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public AppUser findUserAccount(String login){
        String sql =  "SELECT id,login,password,active FROM admin WHERE login = ? AND active = 'Y'";
        Object[] params = new Object[] {login};
        try{
            //String sql = "SELECT e FROM AppUser e WHERE e.login = :login";
            //Query query = this.entityManager.createQuery(sql,AppUser.class);
            //query.setParameter("login", login);
            AppUser user = this.getJdbcTemplate().queryForObject(sql, params,new AppUserMapper());
            return user;
        } catch ( EmptyResultDataAccessException e){
            return null;
        }
    }

    private static class AppUserMapper implements RowMapper<AppUser>{
        @Override
        public AppUser mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt("id");
            String login = rs.getString("login");
            String password = rs.getString("password");
            boolean active = rs.getString("active").equals("Y");
            return new AppUser(id,login,password,active);
        }
    }
}
