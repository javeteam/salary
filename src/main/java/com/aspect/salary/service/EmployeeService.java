package com.aspect.salary.service;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.dao.EmployeeDAO;
import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.entity.Invoice;
import com.aspect.salary.entity.Payment;
import com.aspect.salary.utils.EmployeeAbsenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeDAO employeeDAO;

    private BitrixDAO bitrixDAO = new BitrixDAO();

/*
    public Payment preparePayment(Map<Integer,Employee> employees){

        Set<Integer> keys = employees.keySet();
        Payment payment = new Payment();

        for (Integer key : keys){
            Employee employee = employees.get(key);
            Invoice invoice = new Invoice(employee);
            payment.addInvoice(invoice);
        }

        return payment;
    }
*/
    public List<Employee> getEmployeeList() {
        Map<Integer, Employee>  employeeMap = this.employeeDAO.getRawEmployeeMap();
        List<Absence> allAbsences = bitrixDAO.getAbsenceList();

        //Puts all absences into Employee objects which they connected with
        for (Absence absence: allAbsences){
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
        }
        return employeeList;
    }

    public List<String> printMissingUsers (){
        List<Employee> missingEmployees = this.employeeDAO.getMissingUsers();
        List <String> missingEmployeesInfo = new ArrayList<>();
        for(Employee employee : missingEmployees){
            String employeeInfo = employee.getName() + " " + employee.getSurname() + " { bitrix_id: " + employee.getBitrixUserId() + " }";
            missingEmployeesInfo.add(employeeInfo);
        }
        return missingEmployeesInfo;
    }

    private static void handleEmployeeAbsences(Employee employee){
        EmployeeAbsenceHandler handler = new EmployeeAbsenceHandler(employee);
        handler.removeInappropriateItems();
        handler.splitIntoDays();
        handler.shrinkUnpaidLeavesToWorkingHours();
        handler.checkForIntersection();
        handler.prepareInvoiceData();
    }


}