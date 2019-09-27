package com.aspect.salary.service;

import com.aspect.salary.dao.EmployeeDAO;
import com.aspect.salary.entity.*;
import com.aspect.salary.form.EmployeeForm;
import com.aspect.salary.utils.EmployeeAbsenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeService {

    @Value("${bitrix.forbiddenUserId}")
    private int[] FORBIDDEN_USER_ID;

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private BitrixService bitrixService;



    /*
    public List<Employee> getEmployeeListForLastMonthInvoice(Session session) {
        List<CSVAbsence> csvAbsenceList = session.getCsvAbsenceList();
        List<Employee> employeeList = this.employeeDAO.getAllRawEmployeeList(true);
        LocalDate initial = LocalDate.now().minusMonths(1);

        // Get absences for each employee in list
        for (Employee employee: employeeList){
            int bitrixUserId = employee.getBitrixUserId();
            employee.setAbsences(this.bitrixService.getBitrixAbsences(initial, bitrixUserId));
        }


        //Puts all Bitrix absences into Employee objects which they connected with
        for (Absence absence: bitrixAbsenceList){
            int bitrixUserId = absence.getBitrixUserId();
            if(employeeMap.containsKey(bitrixUserId)){
                Employee employee = employeeMap.get(bitrixUserId);
                employee.addAbsence(absence);
            }
        }

        List <Employee> employeeList = new ArrayList<>(employeeMap.values());


        // Handle absences for each Employee
        for(Employee employee : employeeList){
            handleEmployeeAbsences(employee);
            addCSVAbsencesToEmployee(employee, csvAbsenceList);
            modifyCardPaymentInfo(employee, session);
        }
        return employeeList;
    }
    */

    public List<String> printMissingUsers (){

        List <Employee> missingEmployees = this.getMissingEmployees();
        List <String> missingEmployeesInfo = new ArrayList<>();
        for(Employee employee : missingEmployees){
            String employeeInfo = employee.getName() + " " + employee.getSurname() + " { bitrix_id: " + employee.getBitrixUserId() + " }";
            missingEmployeesInfo.add(employeeInfo);
        }
        return missingEmployeesInfo;
    }

    public List<Employee> getMissingEmployees(){
        List <Employee> bitrixUserList = this.bitrixService.getBitrixUserList();
        List<Employee> missingUserList = new ArrayList<>();
        Map<Integer, Employee> employeeMap = this.employeeDAO.getRawEmployeeMap();

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

    private boolean idNotForbidden (int id){
        for(int forbiddenId : FORBIDDEN_USER_ID){
            if (id == forbiddenId) return false;
        }
        return true;
    }


    public List<Employee> getAllEmployees(boolean activeOnly){
        List<Employee> employeeList = this.employeeDAO.getAllRawEmployeeList( activeOnly);
        employeeList.sort(Comparator.comparing(Employee::getSurname));
        return employeeList;
    }

    public Employee getEmployeeById(int id){
        return this.employeeDAO.getEmployeeById(id);

    }

    public void updateEmployee (Employee employee){
        this.saveEmployeeToDB(employee);
    }

    public void saveEmployeeToDB(Employee employee){
        if (employee.getId() == null){
            this.employeeDAO.addEmployee(employee);
        } else this.employeeDAO.updateEmployee(employee);
    }

    /*
    public static void handleEmployeeAbsences(Employee employee){
        EmployeeAbsenceHandler handler = new EmployeeAbsenceHandler(employee);
        handler.removeInappropriateItems();
        handler.splitIntoDays();
        handler.shrinkUnpaidLeavesToWorkingHours();
        handler.checkForIntersection();
        handler.prepareInvoiceData();
    }
    */

    public EmployeeForm employeeFormByEmployee (Employee employee){
        EmployeeForm employeeForm = new EmployeeForm();
        employeeForm.setId(employee.getId());
        employeeForm.setBitrixUserId(employee.getBitrixUserId());
        employeeForm.setActive(employee.isActive());
        employeeForm.setSalary(employee.getSalary());
        employeeForm.setPaymentToCard(employee.getPaymentToCard());
        employeeForm.setBonus(employee.getBonus());
        employeeForm.setManagementBonus(employee.getManagementBonus());
        employeeForm.setEmail(employee.getEmail());
        employeeForm.setName(employee.getName());
        employeeForm.setSurname(employee.getSurname());
        employeeForm.setXtrfName(employee.getXtrfName());
        employeeForm.setPosition(employee.getPosition());
        employeeForm.setVacationDaysLeft(employee.getVacationDaysLeft());
        employeeForm.setWorkingDayStart(employee.getWorkingDayStart());
        employeeForm.setWorkingDayEnd(employee.getWorkingDayEnd());
        employeeForm.setLunchStart(employee.getLunchStart());
        employeeForm.setLunchEnd(employee.getLunchEnd());
        employeeForm.setHireDate(employee.getHireDate());
        employeeForm.setDismissDate(employee.getDismissDate());
        employeeForm.setFinalInvoiceUuid(employee.getFinalInvoiceUuid());

        return employeeForm;
    }

    public Employee employeeByEmployeeForm (EmployeeForm employeeForm){
        Employee employee = new Employee();
        employee.setId(employeeForm.getId());
        employee.setBitrixUserId(employeeForm.getBitrixUserId());
        employee.setActive(employeeForm.isActive());
        employee.setSalary(employeeForm.getSalary());
        employee.setPaymentToCard(employeeForm.getPaymentToCard());
        employee.setBonus(employeeForm.getBonus());
        employee.setManagementBonus(employeeForm.getManagementBonus());
        employee.setEmail(employeeForm.getEmail());
        employee.setName(employeeForm.getName());
        employee.setSurname(employeeForm.getSurname());
        employee.setXtrfName(employeeForm.getXtrfName());
        employee.setPosition(employeeForm.getPosition());
        employee.setVacationDaysLeft(employeeForm.getVacationDaysLeft());
        employee.setWorkingDayStart(employeeForm.getWorkingDayStart());
        employee.setWorkingDayEnd(employeeForm.getWorkingDayEnd());
        employee.setLunchStart(employeeForm.getLunchStart());
        employee.setLunchEnd(employeeForm.getLunchEnd());
        employee.setHireDate(employeeForm.getHireDate());
        employee.setDismissDate(employeeForm.getDismissDate());
        employee.setFinalInvoiceUuid(employeeForm.getFinalInvoiceUuid());

        return employee;
    }

    public List<EmployeeForm> employeeFormListByEmployeeList (List <Employee> employeeList){
        List<EmployeeForm> employeeFormList = new ArrayList<>();
        for(Employee employee : employeeList){
            employeeFormList.add(employeeFormByEmployee(employee));
        }
        return employeeFormList;
    }


    public void updateVacationInfo (List<Invoice> invoiceList){
        for (Invoice invoice : invoiceList){
            int vacationDaysLeft = this.invoiceService.getUpdatedVacationDaysInfo(invoice);
            int employeeId = invoice.getEmployeeId();
            Employee employee = this.getEmployeeById(employeeId);
            employee.setVacationDaysLeft(vacationDaysLeft);
            this.updateEmployee(employee);
        }
    }

    /*
    private static void addCSVAbsencesToEmployee (Employee employee, List<CSVAbsence> csvAbsenceList){
        String xtrfUsername = employee.getXtrfName();
        for(CSVAbsence csvAbsence : csvAbsenceList) {
            if (csvAbsence.getEmployeeXtrfName().equals(xtrfUsername)) {
                employee.addCSVAbsence(csvAbsence);
            }
        }
    }

    private static void modifyCardPaymentInfo(Employee employee, Session session){
        int employeeId = employee.getId();
        List <Employee> cardPaymentInfo = session.getEmployeeCardPayments();
        for(Employee employeePaymentInfo : cardPaymentInfo){
            if (employeePaymentInfo.getId() == employeeId){
                employee.setPaymentToCard(employeePaymentInfo.getPaymentToCard());
                return;
            }
        }
    }
    */


}