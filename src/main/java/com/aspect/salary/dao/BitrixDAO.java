package com.aspect.salary.dao;

import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

public class BitrixDAO {

    private static final String BITRIX_USER = "salary";
    private static final String BITRIX_PASS = "90g2IiCcaw";
    private static final String BITRIX_URL = "jdbc:mysql://192.168.99.251/sitemanager?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Kiev";
    private static final int BITRIX_USER_DATA_TYPE = 4;
    private static final int BITRIX_ABSENCE_DATA_TYPE = 7;
    public static final int PAYDAY = 6;
    public static final int[] FORBIDDEN_USER_ID = {1,4,6,8,60};

    private static DriverManagerDataSource getBitrixDataSource() {

        DriverManagerDataSource bitrixDS = new DriverManagerDataSource();
        bitrixDS.setUrl(BITRIX_URL);
        bitrixDS.setUsername(BITRIX_USER);
        bitrixDS.setPassword(BITRIX_PASS);

        return bitrixDS;
    }

    public List<Absence> getAbsenceList() {

        LocalDate lastMonthInitial = LocalDate.now().minusMonths(1);
        LocalDate nextMonthInitial = LocalDate.now().plusMonths(1);
        LocalDateTime lastMonthBegin = lastMonthInitial.with(firstDayOfMonth()).atTime(LocalTime.MIN);
        LocalDateTime nextMonthEnd = nextMonthInitial.withDayOfMonth(PAYDAY).atTime(LocalTime.MAX);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String abcenseSelectionPeriod = " AND ((b_iblock_element.ACTIVE_FROM >= '" + lastMonthBegin.format(formatter) +
                "' AND b_iblock_element.ACTIVE_FROM <= '" + nextMonthEnd.format(formatter) + "') OR (b_iblock_element.ACTIVE_TO >= '" + lastMonthBegin.format(formatter) + "' AND b_iblock_element.ACTIVE_TO <= '" + nextMonthEnd.format(formatter) + "'))";

        String sql = "SELECT b_iblock_element.ID, b_iblock_element.TIMESTAMP_X AS CREATION_DATE, b_iblock_element.ACTIVE_FROM, b_iblock_element.ACTIVE_TO, b_iblock_element_property.IBLOCK_PROPERTY_ID AS DATA_TYPE, b_iblock_element_property.VALUE AS DATA_VALUE, b_iblock_property_enum.XML_ID AS ABSENCE_TYPE " +
                "FROM b_iblock_element " +
                "LEFT JOIN b_iblock_element_property ON b_iblock_element.ID = b_iblock_element_property.IBLOCK_ELEMENT_ID " +
                "LEFT JOIN b_iblock_property_enum ON b_iblock_element_property.VALUE = b_iblock_property_enum.ID AND b_iblock_element_property.IBLOCK_PROPERTY_ID = 7 " +
                "WHERE b_iblock_element.IBLOCK_ID = 3";

        sql = sql + abcenseSelectionPeriod;

        JdbcTemplate jdbcTemplate = new JdbcTemplate(getBitrixDataSource());
        HashMap<Integer, Absence> absenceMap = new HashMap<>();
        jdbcTemplate.query(sql, new AbsenceRowCallbackHandler(absenceMap));


        ArrayList<Absence> absenceArrayList = new ArrayList<>();
        if(absenceMap.size() == 0) return null;
        Set<Integer> keySet = absenceMap.keySet();
        for(Integer key: keySet){
            absenceArrayList.add(absenceMap.get(key));
        }
        return absenceArrayList;
    }

    public List<Employee> getBitrixUserList(){
        String sql = "SELECT ID,NAME,LAST_NAME FROM `b_user` WHERE ACTIVE = 'Y'";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(getBitrixDataSource());
        List<Employee> employeeList = new ArrayList<>();
        jdbcTemplate.query(sql, new EmployeeRowCallbackHandler(employeeList));

        return employeeList;
    }


    private static class AbsenceRowCallbackHandler implements RowCallbackHandler {

        private Map<Integer, Absence> map;

        public AbsenceRowCallbackHandler(Map<Integer, Absence> map) {
            this.map = Objects.requireNonNull(map);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int id = rs.getInt("ID");
            Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
            Timestamp activeFrom = rs.getTimestamp("ACTIVE_FROM");
            Timestamp activeTo = rs.getTimestamp("ACTIVE_TO");
            int dataType = rs.getInt("DATA_TYPE");
            int dataValue = rs.getInt("DATA_VALUE");
            String absenceType = rs.getString("ABSENCE_TYPE");

            Absence absence;

            if (map.containsKey(id)) absence = map.get(id);
            else {
                absence = new Absence(activeFrom, activeTo, creationDate);
                map.put(id, absence);
            }

            if (dataType == BITRIX_USER_DATA_TYPE && absenceType == null) absence.setBitrixUserId(dataValue);
            if (dataType == BITRIX_ABSENCE_DATA_TYPE && absenceType != null) absence.setAbsenceType(absenceType);


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
