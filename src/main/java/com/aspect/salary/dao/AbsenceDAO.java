package com.aspect.salary.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Repository
@Transactional
public class AbsenceDAO extends JdbcDaoSupport {

    @Autowired
    public AbsenceDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public void saveAbsenceToDB(){

    }


}
