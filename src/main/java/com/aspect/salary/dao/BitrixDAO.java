package com.aspect.salary.dao;

import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Repository
@Transactional
public class BitrixDAO {

    @Autowired
    @Qualifier("jdbcBitrix")
    private JdbcTemplate jdbcTemplate;

    @Value("${bitrix.userDataType}")
    private int USER_DATA_TYPE;

    @Value("${bitrix.absenceDataType}")
    private int ABSENCE_DATA_TYPE;

    @Value("${bitrix.iBlockId}")
    private int I_BLOCK_ID;

    /**
     *
     * @return
     * List ob absence for employee with specified bitrixUserId and which beginning date or end date is between specified dateFrom and dateTo or equal them
     */
    public List<Absence> getAbsenceList(LocalDate dateFrom, LocalDate dateTo, int bitrixUserId) {
        List <Absence> employeeAbsenceList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timePointFrom = dateFrom.atTime(LocalTime.MIN);
        LocalDateTime timePointTo = dateTo.atTime(LocalTime.MAX);

        String sql = "SELECT " +
                "element.ID, " +
                "element.DATE_CREATE AS CREATION_DATE, " +
                "element.ACTIVE_FROM, " +
                "element.ACTIVE_TO, " +
                "property_1.VALUE AS ABSENCE_ID, " +
                "property_2.VALUE AS USER_ID, " +
                "enum.XML_ID AS ABSENCE_TYPE " +
            "FROM b_iblock_element element " +
            "LEFT JOIN b_iblock_element_property property_1 ON element.ID = property_1.IBLOCK_ELEMENT_ID AND property_1.IBLOCK_PROPERTY_ID = ? " +
            "LEFT JOIN b_iblock_element_property property_2 ON element.ID = property_2.IBLOCK_ELEMENT_ID AND property_2.IBLOCK_PROPERTY_ID = ? " +
            "LEFT JOIN b_iblock_property_enum enum ON property_1.VALUE = enum.ID " +
            "WHERE element.IBLOCK_ID = ? " +
                "AND property_2.VALUE = ? " +
                "AND ((element.ACTIVE_FROM >= ? " +
                "AND element.ACTIVE_FROM <= ?) " +
                "OR (element.ACTIVE_TO >= ? " +
                "AND element.ACTIVE_TO <= ?))";

        this.jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, ABSENCE_DATA_TYPE);
            ps.setInt(2, USER_DATA_TYPE);
            ps.setInt(3, I_BLOCK_ID);
            ps.setInt(4,bitrixUserId);
            ps.setString(5, timePointFrom.format(formatter) );
            ps.setString(6, timePointTo.format(formatter) );
            ps.setString(7, timePointFrom.format(formatter) );
            ps.setString(8, timePointTo.format(formatter) );
            return ps;
        }, new AbsenceRowCallbackHandler(employeeAbsenceList) );

        return employeeAbsenceList;
    }

    public List<Employee> getBitrixUserList(){
        String sql = "SELECT ID,NAME,LAST_NAME FROM `b_user` WHERE ACTIVE = 'Y'";

        List<Employee> employeeList = new ArrayList<>();
        this.jdbcTemplate.query(sql, new EmployeeRowCallbackHandler(employeeList));

        return employeeList;
    }


    private static  class  AbsenceRowCallbackHandler implements RowCallbackHandler{
        List<Absence> absenceList;

        public AbsenceRowCallbackHandler(List<Absence> absenceList){
            this.absenceList = absenceList;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
            Timestamp activeFrom = rs.getTimestamp("ACTIVE_FROM");
            Timestamp activeTo = rs.getTimestamp("ACTIVE_TO");
            int bitrixUserId = rs.getInt("USER_ID");
            String absenceType = rs.getString("ABSENCE_TYPE");
            Absence absence = new Absence(activeFrom,activeTo,creationDate);
            absence.setBitrixUserId(bitrixUserId);
            absence.setAbsenceType(absenceType);
            absenceList.add(absence);
        }
    }

    private static class EmployeeRowCallbackHandler implements RowCallbackHandler{
        List<Employee> employeeList;

        public EmployeeRowCallbackHandler(List<Employee> employeeList){
            this.employeeList = employeeList;
        }
        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int id = rs.getInt("ID");
            String name = rs.getString("NAME");
            String surname = rs.getString("LAST_NAME");
            Employee employee = new Employee(id, name,surname);
            employeeList.add(employee);
        }
    }
}
