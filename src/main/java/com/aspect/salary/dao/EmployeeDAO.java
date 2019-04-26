package com.aspect.salary.dao;

import com.aspect.salary.service.BitrixDB;
import com.aspect.salary.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional
public class EmployeeDAO extends JdbcDaoSupport {

    @Autowired
    public EmployeeDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Map<Integer, Employee> getEmployeeMap(){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.email, employees.vacation_days_left, rates.salary,rates.official_salary,rates.bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "WHERE employees.active = 'Y' " +
                "ORDER BY employees.name";

        Map<Integer, Employee> employeeMap = new HashMap<>();
        this.getJdbcTemplate().query(sql, new MyRowCallbackHandler(employeeMap));

        return employeeMap;

    }

    public List<Employee> getMissingUsers(){
        List<Employee> bitrixUserList;
        List<Employee> missingUserList = new ArrayList<>();
        Map<Integer, Employee> employeeMap = getEmployeeMap();
        bitrixUserList = BitrixDB.getBitrixUserList();

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
        for (int identifier : BitrixDB.FORBIDDEN_USER_ID){
            if (id == identifier) return false;
        }
        return true;
    }

    private static class MyRowCallbackHandler implements RowCallbackHandler {

        private Map<Integer, Employee> map;

        public MyRowCallbackHandler(Map<Integer, Employee> map) {
            this.map = Objects.requireNonNull(map);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            map.put(rs.getInt("bitrix_user_id"), toEmployee(rs));
        }

        private Employee toEmployee(ResultSet rs) throws SQLException {
            int id = rs.getInt("id");
            int bitrixUserId = rs.getInt("bitrix_user_id");
            float salary = rs.getFloat("salary");
            float officialSalary = rs.getFloat("official_salary");
            float bonus = rs.getFloat("bonus");
            String email = rs.getString("email");
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String xtrfName = rs.getString("xtrf_Name");
            int vacationDaysLeft = rs.getInt("vacation_days_left");

            return new Employee(id, bitrixUserId, salary, officialSalary, bonus, email, name, surname, xtrfName, vacationDaysLeft);
        }
    }
}
