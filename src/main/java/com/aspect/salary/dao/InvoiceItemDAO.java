package com.aspect.salary.dao;

import com.aspect.salary.entity.InvoiceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class InvoiceItemDAO extends JdbcDaoSupport {

    @Autowired
    public InvoiceItemDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public void updateInvoiceItem(InvoiceItem invoiceItem){
        String sql = "UPDATE `items` SET description = ?, prise = ? WHERE id = ?";

        this.getJdbcTemplate().update( connection ->{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,invoiceItem.getDescription());
            ps.setInt(2,invoiceItem.getPrise());
            ps.setInt(3,invoiceItem.getId());

            return ps;
        });

    }

    public void addInvoiceItem(InvoiceItem invoiceItem, int invoiceId){
        String sql = "INSERT INTO `items` (description, prise, invoice_id) VALUES (?, ?, ?)";

        this.getJdbcTemplate().update(connection -> {
           PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,invoiceItem.getDescription());
            ps.setInt(2,invoiceItem.getPrise());
            ps.setInt(3,invoiceId);

            return ps;
        });
    }

    public List<InvoiceItem> getInvoiceItemList (int invoiceId){
        List<InvoiceItem> itemList = new ArrayList<>();

        String sql = "SELECT id, description, prise FROM items WHERE invoice_id = ?";
        Object [] params = new Object[] {invoiceId};

        try {
            this.getJdbcTemplate().query(sql, params, new ItemRowCallbackHandler(itemList) );
        } catch (EmptyResultDataAccessException e){
            e.getLocalizedMessage();
            return null;
        }

        return itemList;
    }

    public void deleteInvoiceItem (int id){
        String sql = "DELETE FROM `items` WHERE id = ?";

        Object[] params = new Object[] {id};
        this.getJdbcTemplate().update(sql,params);
    }

    private static class ItemRowCallbackHandler implements RowCallbackHandler{
        List<InvoiceItem> invoiceItems;

        ItemRowCallbackHandler(List<InvoiceItem> invoiceItems){
            this.invoiceItems = invoiceItems;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setId(rs.getInt("id"));
            invoiceItem.setDescription(rs.getString("description"));
            invoiceItem.setPrise(rs.getInt("prise"));

            invoiceItems.add(invoiceItem);
        }
    }
}
