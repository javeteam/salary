package com.aspect.salary.dao;

import com.aspect.salary.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class PaymentDAO extends JdbcDaoSupport {

    @Autowired
    public PaymentDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Integer getIncompletePaymentID (){
        String sql = "SELECT id,date,complete,total_amount FROM `payments` WHERE complete = 'N'";
        List<Payment> paymentList = new ArrayList<>();
        this.getJdbcTemplate().query(sql, new PaymentDAO.MyRowCallbackHandler(paymentList));
        if (paymentList.size() != 0 ){
            return paymentList.get(0).getId();
        }
        return null;
    }

    public Payment getPaymentById(int id){
        String sql = "SELECT id,date,complete,total_amount FROM `payments` WHERE id = ?";
        Object[] params = new Object[] {id};
        try {
            return this.getJdbcTemplate().queryForObject(sql, params, new PaymentMapper());
        } catch (EmptyResultDataAccessException e){
            e.getLocalizedMessage();
            return null;
        }
    }

    private static class MyRowCallbackHandler implements RowCallbackHandler{
        List<Payment> paymentList;
        public MyRowCallbackHandler(List<Payment> paymentList){
            this.paymentList = paymentList;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int id = rs.getInt("id");
            Timestamp creationDate = rs.getTimestamp("date");
            boolean complete = rs.getString("complete").equals("Y");
            float TotalAmount = rs.getFloat("total_amount");
            Payment payment = new Payment(id, creationDate.toLocalDateTime(), complete, TotalAmount);
            paymentList.add(payment);
        }
    }

    private static class PaymentMapper implements RowMapper<Payment> {
        @Override
        public Payment mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt("id");
            Timestamp creationDate = rs.getTimestamp("date");
            boolean complete = rs.getString("complete").equals("Y");
            float TotalAmount = rs.getFloat("total_amount");
            return new Payment(id, creationDate.toLocalDateTime(), complete, TotalAmount);
        }
    }
}
