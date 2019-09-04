package com.aspect.salary.dao;

import com.aspect.salary.entity.Employee;
import com.aspect.salary.utils.CommonUtils;
import com.aspect.salary.utils.CommonUtils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@Transactional
public class EmployeeDAO extends JdbcDaoSupport {

    @Autowired
    public EmployeeDAO (DataSource dataSource){
        this.setDataSource(dataSource);
    }

    public Map<Integer, Employee> getRawEmployeeMap(){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.position, employees.email, employees.vacation_days_left, employees.working_day_start, employees.working_day_end, employees.lunch_start, employees.lunch_end,employees.active, employees.hire_date, employees.dismiss_date, employees.final_invoice_uuid, rates.salary,rates.payment_to_card,rates.bonus, rates.management_bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "WHERE employees.active = 'Y' " +
                "ORDER BY employees.name";

        Map<Integer, Employee> employeeMap = new HashMap<>();
        this.getJdbcTemplate().query(sql, new EmployeeMapRowCallbackHandler(employeeMap));

        return employeeMap;

    }

    public List <Employee> getAllRawEmployeeList(){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.position, employees.email, employees.vacation_days_left, employees.working_day_start, employees.working_day_end, employees.lunch_start, employees.lunch_end, employees.active, employees.hire_date, employees.dismiss_date, employees.final_invoice_uuid, rates.salary,rates.payment_to_card,rates.bonus, rates.management_bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "ORDER BY employees.surname";

        List<Employee> employeeList = new ArrayList<>();
        this.getJdbcTemplate().query(sql, new EmployeeListRowCallbackHandler(employeeList));

        return employeeList;

    }

    public Employee getEmployeeById(int id){
        String sql = "SELECT employees.id, employees.bitrix_user_id, employees.name,employees.surname, employees.xtrf_name, employees.position, employees.email, employees.vacation_days_left, employees.working_day_start, employees.working_day_end, employees.lunch_start, employees.lunch_end, employees.active, employees.hire_date, employees.dismiss_date, employees.final_invoice_uuid, rates.salary,rates.payment_to_card,rates.bonus, rates.management_bonus " +
                "FROM employees " +
                "LEFT JOIN rates ON rates.user_id = employees.id " +
                "WHERE employees.id = ? ";

        Object[] param = new Object[]{id};

        return this.getJdbcTemplate().queryForObject(sql, param, new EmployeeRowMapper());

    }

    public void addEmployee (Employee employee){
        String sql = "INSERT INTO `employees` (name, surname, bitrix_user_id, xtrf_name, position, email, vacation_days_left, working_day_start, working_day_end, lunch_start, lunch_end, active, hire_date, final_invoice_uuid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    setEmployeePreparedStatementParameters(ps, employee);
                    return ps;
                }, keyHolder
        );

        if (keyHolder.getKey() != null){
            employee.setId(keyHolder.getKey().intValue());
            addEmployeeRate(employee);
        }

    }

    public void updateEmployee (Employee employee){
        String sql = "UPDATE `employees` Set name = ?, surname = ?, bitrix_user_id = ?, xtrf_name = ?, position = ?, email= ?, vacation_days_left = ?, working_day_start = ?, working_day_end = ?, lunch_start = ?, lunch_end = ?, active = ?, hire_date = ?, dismiss_date = ?, final_invoice_uuid = ? WHERE id = ?";

        this.getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            setEmployeePreparedStatementParameters(ps, employee);
            ps.setInt(16,employee.getId());
            return ps;
        });

        updateEmployeeRate(employee);
    }

    private void setEmployeePreparedStatementParameters (PreparedStatement ps, Employee employee) throws SQLException{
        ps.setString(1, employee.getName());
        ps.setString(2, employee.getSurname());
        ps.setInt(3, employee.getBitrixUserId());
        ps.setString(4, employee.getXtrfName());
        ps.setString(5, employee.getPosition().toString());
        ps.setString(6, employee.getEmail().equals("") ? null : employee.getEmail());
        ps.setInt(7, employee.getVacationDaysLeft());
        ps.setInt(8, employee.getWorkingDayStart().toSecondOfDay());
        ps.setInt(9, employee.getWorkingDayEnd().toSecondOfDay());
        ps.setInt(10, employee.getLunchStart().toSecondOfDay());
        ps.setInt(11, employee.getLunchEnd().toSecondOfDay());
        ps.setString(12, employee.isActive() ? "Y" : "N");
        ps.setString(13, DateTimeFormatter.ofPattern("yyyy.MM.dd").format(employee.getHireDate()));
        ps.setString(14, employee.getDismissDate() == null ? null : DateTimeFormatter.ofPattern("yyyy.MM.dd").format(employee.getDismissDate()));
        ps.setString(15, employee.getFinalInvoiceUuid());
    }

    private void addEmployeeRate (Employee employee){
        String sql = "INSERT INTO `rates` (user_id, salary, payment_to_card, bonus, management_bonus) VALUES (?, ?, ?, ?, ?)";

        this.getJdbcTemplate().update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1,employee.getId());
                    ps.setFloat(2, employee.getSalary());
                    ps.setFloat(3,employee.getPaymentToCard());
                    ps.setFloat(4,employee.getBonus());
                    ps.setFloat(5,employee.getManagementBonus());
                    return ps;
                }
        );

    }

    private void updateEmployeeRate (Employee employee){
        String sql = "UPDATE `rates` SET salary = ?, payment_to_card = ?, bonus = ?, management_bonus = ?  WHERE user_id = ?";

        this.getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setFloat(1, employee.getSalary());
            ps.setFloat(2,employee.getPaymentToCard());
            ps.setFloat(3,employee.getBonus());
            ps.setFloat(4,employee.getManagementBonus());
            ps.setInt(5,employee.getId());
            return ps;
        });

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
        Employee employee = new Employee();

        employee.setId(rs.getInt("id"));
        employee.setBitrixUserId(rs.getInt("bitrix_user_id"));
        employee.setActive(rs.getString("active").equals("Y"));
        employee.setSalary(rs.getFloat("salary"));
        employee.setPaymentToCard(rs.getFloat("payment_to_card"));
        employee.setBonus(rs.getFloat("bonus"));
        employee.setManagementBonus(rs.getFloat("management_bonus"));
        employee.setEmail(rs.getString("email"));
        employee.setName(rs.getString("name"));
        employee.setSurname(rs.getString("surname"));
        employee.setXtrfName(rs.getString("xtrf_Name"));
        employee.setPosition(Position.valueOf(rs.getString("position")));
        employee.setVacationDaysLeft(rs.getInt("vacation_days_left"));
        employee.setWorkingDayStart(LocalTime.ofSecondOfDay(rs.getInt("working_day_start")));
        employee.setWorkingDayEnd(LocalTime.ofSecondOfDay(rs.getInt("working_day_end")));
        employee.setLunchStart(LocalTime.ofSecondOfDay(rs.getInt("lunch_start")));
        employee.setLunchEnd(LocalTime.ofSecondOfDay(rs.getInt("lunch_end")));
        employee.setHireDate(LocalDate.parse(rs.getString("hire_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String dismissDate = rs.getString("dismiss_date");
        employee.setDismissDate(dismissDate == null ? null : LocalDate.parse(dismissDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        employee.setFinalInvoiceUuid(rs.getString("final_invoice_uuid"));

        return employee;
    }
}
