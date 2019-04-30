package com.aspect.salary.dao;

import com.aspect.salary.entity.Payment;
import com.aspect.salary.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class PaymentDAO extends JdbcDaoSupport {

    @Autowired
    public PaymentDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public LocalDateTime getLatestPaymentDate(){
        String sql = "SELECT max(date) AS date FROM `payments`";
        try {
            return this.getJdbcTemplate().queryForObject(sql, new PaymentDateMapper());
        } catch (EmptyResultDataAccessException e){
            e.getLocalizedMessage();
            return null;
        }
    }

    public Integer addPayment(Payment payment){
        String sql = "INSERT INTO `payments` (date, complete, total_amount) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setTimestamp(1, Timestamp.valueOf(payment.getCreationDate()));
                    ps.setString(2, payment.isComplete() ? "Y" : "N");
                    ps.setFloat(3, payment.getTotalAmount());
                    return ps;
                }, keyHolder
        );

        if (keyHolder.getKey() != null) return keyHolder.getKey().intValue();
        else return null;
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
    private static class PaymentDateMapper implements RowMapper<LocalDateTime> {
        @Override
        public LocalDateTime mapRow(ResultSet rs, int i) throws SQLException {
            Timestamp creationDate = rs.getTimestamp("date");

            if(creationDate == null) return null;
            else return creationDate.toLocalDateTime();
        }
    }
}
