package com.aspect.salary.dao;

import com.aspect.salary.entity.Absence;
import com.aspect.salary.utils.CommonUtils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
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
public class AbsenceDAO extends JdbcDaoSupport {

    @Autowired
    public AbsenceDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Integer addAbsence(Absence absence, int invoiceId){
        String sql = "INSERT INTO `absences` (creation_date, date_from, date_to, type, invoice_id, duration_days, duration_hours, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp createDateValue = absence.getCreationDate() == null ? null : Timestamp.valueOf( absence.getCreationDate());
        Timestamp dateFromValue = absence.getDateFrom() == null ? null : Timestamp.valueOf( absence.getDateFrom());
        Timestamp dateToValue = absence.getDateTo() == null ? null : Timestamp.valueOf( absence.getDateTo());

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setTimestamp(1,  createDateValue);
                    ps.setTimestamp(2, dateFromValue);
                    ps.setTimestamp(3, dateToValue);
                    ps.setString(4, absence.getAbsenceType());
                    ps.setInt(5, invoiceId);
                    ps.setInt(6, absence.getDurationDays());
                    ps.setFloat(7, absence.getDurationHours());
                    ps.setString(8, absence.getWeight().toString());
                    return ps;
                }, keyHolder
        );

        if (keyHolder.getKey() != null) return keyHolder.getKey().intValue();
        else return null;

    }

    public List<Absence> getAbsencesByInvoiceId(int invoiceId){
        List<Absence> absenceList = new ArrayList<>();
        String sql = "SELECT id, creation_date, date_from, date_to, type, duration_days, duration_hours, weight FROM `absences` WHERE invoice_id = ? ";
        Object[] params = new Object[] {invoiceId};
        try {
            this.getJdbcTemplate().query(sql, params, new AbsenceRowCallbackHandler(absenceList) );
        } catch (EmptyResultDataAccessException e){
            e.getLocalizedMessage();
            return null;
        }

        return absenceList;
    }

    private class AbsenceRowCallbackHandler implements RowCallbackHandler{
        List<Absence> absenceList;

        AbsenceRowCallbackHandler (List<Absence> absenceList){
            this.absenceList = absenceList;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int id = rs.getInt("id");
            Timestamp creationDate = rs.getTimestamp("creation_date");
            Timestamp dateFrom = rs.getTimestamp("date_from");
            Timestamp dateTo = rs.getTimestamp("creation_date");
            String absenceType = rs.getString("type");
            int durationDays = rs.getInt("duration_days");
            float durationHours = rs.getFloat("duration_hours");
            Weight weight = Weight.valueOf(rs.getString("weight"));

            Absence absence = new Absence(dateFrom, dateTo, creationDate);
            absence.setId(id);
            absence.setAbsenceType(absenceType);
            absence.setDurationDays(durationDays);
            absence.setDurationHours(durationHours);
            absence.setWeight(weight);

            absenceList.add(absence);
        }
    }
}
