package com.aspect.salary.dao;

import com.aspect.salary.entity.Invoice;
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
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class InvoiceDAO extends JdbcDaoSupport {

    @Autowired
    public InvoiceDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Integer addInvoice(Invoice invoice, int paymentId){
        String sql = "INSERT INTO `invoices` (payment_id, employee_id, salary, payment_to_card, bonus, working_day_duration, confirmed, creation_date, modification_date, uuid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, paymentId);
                    ps.setInt(2, invoice.getEmployeeId());
                    ps.setInt(3, invoice.getSalary());
                    ps.setInt(4, invoice.getPaymentToCard());
                    ps.setInt(5, invoice.getBonus());
                    ps.setFloat(6, invoice.getWorkingDayDuration());
                    ps.setString(7, invoice.isConfirmed() ? "Y" : "N");
                    ps.setTimestamp(8, Timestamp.valueOf(invoice.getCreationDate()));
                    ps.setTimestamp(9, Timestamp.valueOf(invoice.getModificationDate()));
                    ps.setString(10, invoice.getUuid());
                    return ps;
                }, keyHolder
        );

        if (keyHolder.getKey() != null) return keyHolder.getKey().intValue();
        else return null;
    }

    public List<Invoice> getRawInvoicesByPaymentId(int paymentId){
        List<Invoice> invoiceList = new ArrayList<>();
        String sql = "SELECT invoices.id, invoices.employee_id, invoices.salary, invoices.payment_to_card, invoices.bonus, invoices.working_day_duration, " +
                "invoices.confirmed, invoices.creation_date, invoices.modification_date, invoices.uuid, CONCAT(employees.surname, \" \", employees.name) AS username " +
                "FROM `invoices` " +
                "LEFT JOIN employees ON employees.id = invoices.employee_id " +
                "WHERE payment_id = ?";
        Object[] params = new Object[]{paymentId};

        this.getJdbcTemplate().query(sql,params, new InvoiceRowCallbackHandler(invoiceList));


        return invoiceList;
    }

    public Invoice getRawInvoiceByUuid(String uuid){
        String sql = "SELECT invoices.id, invoices.employee_id, invoices.salary, invoices.payment_to_card, invoices.bonus, invoices.working_day_duration, " +
                "invoices.confirmed, invoices.creation_date, invoices.modification_date, invoices.uuid, CONCAT(employees.surname, \" \", employees.name) AS username " +
                "FROM `invoices` " +
                "LEFT JOIN employees ON employees.id = invoices.employee_id " +
                "WHERE invoices.uuid = ? AND invoices.confirmed = 'N'";
        Object[] params = new Object[]{uuid};

        try {
            return this.getJdbcTemplate().queryForObject(sql, params, new InvoiceRowMapper());
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    private class InvoiceRowCallbackHandler implements RowCallbackHandler{
        List<Invoice> invoiceList;
        InvoiceRowCallbackHandler(List<Invoice> invoiceList){
            this.invoiceList = invoiceList;
        }

        @Override
        public void processRow(ResultSet resultSet) throws SQLException {
            Invoice invoice = invoiceFromResultSet(resultSet);
            invoiceList.add(invoice);
        }
    }

    private static class InvoiceRowMapper implements RowMapper<Invoice> {
        @Override
        public Invoice mapRow(ResultSet resultSet, int i) throws SQLException {
            return invoiceFromResultSet(resultSet);
        }
    }

    private static Invoice invoiceFromResultSet (ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int employeeId = rs.getInt("employee_id");
        int salary = rs.getInt("salary");
        int paymentToCard = rs.getInt("payment_to_card");
        int bonus = rs.getInt("bonus");
        float workingDayDuration = rs.getFloat("working_day_duration");
        boolean invoiceConfirmed = rs.getString("confirmed").equals("Y");
        Timestamp creationDate = rs.getTimestamp("creation_date");
        Timestamp modificationDate = rs.getTimestamp("modification_date");
        String uuid = rs.getString("uuid");
        String username = rs.getString("username");

        return new Invoice(
                id,
                employeeId,
                salary,
                paymentToCard,
                bonus,
                workingDayDuration,
                invoiceConfirmed,
                creationDate.toLocalDateTime(),
                modificationDate.toLocalDateTime(),
                username,uuid);

    }
}
