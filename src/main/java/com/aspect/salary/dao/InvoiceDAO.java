package com.aspect.salary.dao;

import com.aspect.salary.entity.Invoice;
import com.aspect.salary.entity.Payment;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        String sql = "INSERT INTO `invoices` (payment_id, employee_id, salary, payment_to_card, bonus, management_bonus, working_day_duration, vacation_days_left, confirmed, creation_date, modification_date, uuid, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, paymentId);
                    ps.setInt(2, invoice.getEmployeeId());
                    ps.setInt(3, invoice.getSalary());
                    ps.setInt(4, invoice.getPaymentToCard());
                    ps.setInt(5, invoice.getBonus());
                    ps.setInt(6, invoice.getManagementBonus());
                    ps.setFloat(7, invoice.getWorkingDayDuration());
                    ps.setInt(8, invoice.getVacationDaysLeft());
                    ps.setString(9, invoice.isConfirmed() ? "Y" : "N");
                    ps.setTimestamp(10, Timestamp.valueOf(invoice.getCreationDate()));
                    ps.setTimestamp(11, Timestamp.valueOf(invoice.getModificationDate()));
                    ps.setString(12, invoice.getUuid());
                    ps.setString(13, invoice.getNotes());
                    return ps;
                }, keyHolder
        );

        if (keyHolder.getKey() != null) return keyHolder.getKey().intValue();
        else return null;
    }

    public List<Invoice> getRawInvoicesByPaymentId(int paymentId){
        List<Invoice> invoiceList = new ArrayList<>();
        String sql = "SELECT invoices.id, invoices.employee_id, invoices.salary, invoices.payment_to_card, invoices.bonus, invoices.management_bonus, invoices.working_day_duration, invoices.vacation_days_left, " +
                "invoices.confirmed, invoices.creation_date, invoices.modification_date, invoices.uuid, invoices.notes, CONCAT(employees.surname, \" \", employees.name) AS username " +
                "FROM `invoices` " +
                "LEFT JOIN employees ON employees.id = invoices.employee_id " +
                "WHERE payment_id = ?";
        Object[] params = new Object[]{paymentId};

        this.getJdbcTemplate().query(sql,params, new InvoiceRowCallbackHandler(invoiceList));


        return invoiceList;
    }

    public Invoice getRawInvoiceByUuid(String uuid){
        String sql = "SELECT invoices.id, invoices.employee_id, invoices.salary, invoices.payment_to_card, invoices.bonus, invoices.management_bonus, invoices.working_day_duration, invoices.vacation_days_left, " +
                "invoices.confirmed, invoices.creation_date, invoices.modification_date, invoices.uuid, invoices.notes, CONCAT(employees.surname, \" \", employees.name) AS username " +
                "FROM `invoices` " +
                "LEFT JOIN employees ON employees.id = invoices.employee_id " +
                "WHERE invoices.uuid = ?";
        Object[] params = new Object[]{uuid};

        try {
            return this.getJdbcTemplate().queryForObject(sql, params, new InvoiceRowMapper());
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void updateInvoice(Invoice invoice){
        String sql = "UPDATE `invoices` SET salary = ?, payment_to_card = ?, bonus = ?, management_bonus = ?, working_day_duration = ?, confirmed = ?, modification_date = ?, notes = ?, vacation_days_left = ?  WHERE id = ?";

        this.getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setFloat(1, invoice.getSalary());
            ps.setFloat(2,invoice.getPaymentToCard());
            ps.setFloat(3,invoice.getBonus());
            ps.setFloat(4,invoice.getManagementBonus());
            ps.setFloat(5, invoice.getWorkingDayDuration());
            ps.setString(6, invoice.isConfirmed() ? "Y" : "N");
            ps.setTimestamp(7, Timestamp.valueOf(invoice.getModificationDate()));
            ps.setString(8, invoice.getNotes());
            ps.setInt(9, invoice.getVacationDaysLeft());
            ps.setInt(10, invoice.getId());
            return ps;
        });
    }

    public Integer getMonthsAmountBetweenThisAndLastInvoice(Invoice invoice){
        //int employeeId = invoice.getEmployeeId();
        //String sql = "SELECT TIMESTAMPDIFF(MONTH, MAX(invoices.creation_date), ?) AS MonthAmount FROM invoices WHERE invoices.employee_id = ? AND invoices.creation_date < ?";
        String sql = "SELECT MAX(invoices.creation_date) AS date FROM invoices WHERE invoices.employee_id = ? AND invoices.creation_date < ?";
        Object [] params = new Object[]{invoice.getEmployeeId(), invoice.getCreationDate()};

        Timestamp lastInvoiceCreationDate = this.getJdbcTemplate().queryForObject(sql,params,Timestamp.class);
        if(lastInvoiceCreationDate == null) return 0;
        else{
            LocalDate oldInvoiceDate = lastInvoiceCreationDate.toLocalDateTime().toLocalDate();
            return (int) ChronoUnit.MONTHS.between(oldInvoiceDate.withDayOfMonth(1), invoice.getCreationDate().toLocalDate().withDayOfMonth(1));
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
        Invoice invoice = new Invoice();

        invoice.setId(rs.getInt("id"));
        invoice.setEmployeeId(rs.getInt("employee_id"));
        invoice.setSalary(rs.getInt("salary"));
        invoice.setPaymentToCard(rs.getInt("payment_to_card"));
        invoice.setBonus(rs.getInt("bonus"));
        invoice.setManagementBonus(rs.getInt("management_bonus"));
        invoice.setWorkingDayDuration(rs.getFloat("working_day_duration"));
        invoice.setConfirmed(rs.getString("confirmed").equals("Y"));
        invoice.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        invoice.setModificationDate(rs.getTimestamp("modification_date").toLocalDateTime());
        invoice.setVacationDaysLeft(rs.getInt("vacation_days_left"));
        invoice.setUuid(rs.getString("uuid"));
        invoice.setUsername(rs.getString("username"));
        invoice.setNotes(rs.getString("notes"));

        return  invoice;
    }

}
