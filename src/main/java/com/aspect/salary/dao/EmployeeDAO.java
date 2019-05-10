package com.aspect.salary.dao;

import com.aspect.salary.entity.Employee;
import com.aspect.salary.utils.CommonUtils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;

@Repository
@Transactional
public class EmployeeDAO extends JdbcDaoSupport {

    @Autowired
    public EmployeeDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Map<Integer, Employee> getRawEmployeeMap(){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.position, employees.email, employees.vacation_days_left, employees.working_day_start, employees.working_day_end, employees.lunch_start, employees.lunch_end,employees.active, rates.salary,rates.payment_to_card,rates.bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "WHERE employees.active = 'Y' " +
                "ORDER BY employees.name";

        Map<Integer, Employee> employeeMap = new HashMap<>();
        this.getJdbcTemplate().query(sql, new EmployeeMapRowCallbackHandler(employeeMap));

        return employeeMap;

    }

    public List <Employee> getAllRawEmployeeList(){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.position, employees.email, employees.vacation_days_left, employees.working_day_start, employees.working_day_end, employees.lunch_start, employees.lunch_end, employees.active, rates.salary,rates.payment_to_card,rates.bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "ORDER BY employees.surname";

        List<Employee> employeeList = new ArrayList<>();
        this.getJdbcTemplate().query(sql, new EmployeeListRowCallbackHandler(employeeList));

        return employeeList;

    }

    public Employee getEmployeeById(int id){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.position, employees.email, employees.vacation_days_left, employees.working_day_start, employees.working_day_end, employees.lunch_start, employees.lunch_end, employees.active, rates.salary,rates.payment_to_card,rates.bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "WHERE employees.id = ? " +
                "ORDER BY employees.surname";

        Object[] param = new Object[]{id};

        return this.getJdbcTemplate().queryForObject(sql, param, new EmployeeRowMapper());

    }

    public List<Employee> getMissingUsers(){
        BitrixDAO bitrixDAO = new BitrixDAO();
        List<Employee> bitrixUserList;
        List<Employee> missingUserList = new ArrayList<>();
        Map<Integer, Employee> employeeMap = getRawEmployeeMap();
        bitrixUserList = bitrixDAO.getBitrixUserList();

        if(bitrixUserList == null || employeeMap == null) return null;

        for(Employee employee: bitrixUserList){
            int bitrixUserId = employee.getBitrixUserId();
            if(!employeeMap.containsKey(bitrixUserId)){
                if (idNotForbidden(bitrixUserId)){
                    missingUserList.add(employee);
                }
            }
        }
        return missingUserList;
    }

    private static boolean idNotForbidden(int id){
        for (int identifier : BitrixDAO.FORBIDDEN_USER_ID){
            if (id == identifier) return false;
        }
        return true;
    }

    private static class EmployeeRowMapper implements RowMapper<Employee>{
        @Override
        public Employee mapRow(ResultSet rs, int i) throws SQLException {
            return toEmployee(rs);
        }
    }

    private static class EmployeeMapRowCallbackHandler implements RowCallbackHandler {

        private Map<Integer, Employee> map;

        public EmployeeMapRowCallbackHandler(Map<Integer, Employee> map) {
            this.map = Objects.requireNonNull(map);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            map.put(rs.getInt("bitrix_user_id"), toEmployee(rs));
        }
    }

    private static class EmployeeListRowCallbackHandler implements RowCallbackHandler {

        private List<Employee> list;

        public EmployeeListRowCallbackHandler(List <Employee> list) {
            this.list = Objects.requireNonNull(list);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            list.add(toEmployee(rs));
        }
    }

    private static Employee toEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int bitrixUserId = rs.getInt("bitrix_user_id");
        boolean isActive = rs.getString("active").equals("Y");
        float salary = rs.getFloat("salary");
        float paymentToCard = rs.getFloat("payment_to_card");
        float bonus = rs.getFloat("bonus");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String xtrfName = rs.getString("xtrf_Name");
        Position position = Position.valueOf(rs.getString("position"));
        int vacationDaysLeft = rs.getInt("vacation_days_left");
        LocalTime workingDayStart = rs.getObject("working_day_start", LocalTime.class);
        LocalTime workingDayEnd = rs.getObject("working_day_end", LocalTime.class);
        LocalTime lunchStart = rs.getObject("lunch_start", LocalTime.class);
        LocalTime lunchEnd = rs.getObject("lunch_end", LocalTime.class);

        return new Employee(
                id,
                bitrixUserId,
                isActive,
                salary,
                paymentToCard,
                bonus,
                email,
                name,
                surname,
                xtrfName,
                position,
                vacationDaysLeft,
                workingDayStart,
                workingDayEnd,
                lunchStart,
                lunchEnd
        );
    }
}
