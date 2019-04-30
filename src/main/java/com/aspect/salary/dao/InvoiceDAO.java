package com.aspect.salary.dao;

import com.aspect.salary.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@Repository
@Transactional
public class InvoiceDAO extends JdbcDaoSupport {

    @Autowired
    public InvoiceDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Integer addInvoice(Invoice invoice, int paymentId){
        String sql = "INSERT INTO `invoices` (payment_id, employee_id, confirmed, creation_date, modification_date) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, paymentId);
                    ps.setInt(2, invoice.getEmployeeId());
                    ps.setString(3, invoice.isConfirmed() ? "Y" : "N");
                    ps.setTimestamp(4, Timestamp.valueOf(invoice.getCreationDate()));
                    ps.setTimestamp(5, Timestamp.valueOf(invoice.getModificationDate()));
                    return ps;
                }, keyHolder
        );

        if (keyHolder.getKey() != null) return keyHolder.getKey().intValue();
        else return null;
    }
}
