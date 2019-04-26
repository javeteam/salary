package com.aspect.salary.service;

import com.aspect.salary.dao.EmployeeDAO;
import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MainHandler {

    @Autowired
    private EmployeeDAO employeeDAO;

    public List<Invoice> preparePayment(Map<Integer,Employee> employees){

        Set<Integer> keys = employees.keySet();
        //Payment payment = new Payment();
        List<Invoice> invoiceList = new ArrayList<>();

        for (Integer key : keys){
            Employee employee = employees.get(key);
            Invoice invoice = new Invoice(employee);
            invoiceList.add(invoice);

        }

        return invoiceList;
    }

    public Map<Integer,Employee> getEmployees() {
        Map<Integer, Employee>  employeeMap = this.employeeDAO.getEmployeeMap();
        List<Absence> allAbsences = BitrixDB.getAbsence();

        //AbsenceHandler absenceHandler = new AbsenceHandler(allAbsences);
        //absenceHandler.removeInappropriateItems();

        //Puts all absences into Employee objects which they connected with

        for (Absence absence: allAbsences){
            int bitrixUserId = absence.getBitrixUserId();
            if(employeeMap.containsKey(bitrixUserId)){
                Employee employee = employeeMap.get(bitrixUserId);
                employee.addAbsence(absence);
            }
        }

        // work with absences for each Employee
        Set<Integer> keys = employeeMap.keySet();
        for(Integer key:keys){
            Employee employee = employeeMap.get(key);
            LocalTime dayStart = employee.getWorkingDayStart();
            //List <Absence> employeeAbsences = employee.getAbsences();
            AbsenceHandler employeeAbsenceHandler = new AbsenceHandler(employee);
            employeeAbsenceHandler.removeInappropriateItems();
            employeeAbsenceHandler.setWorkingDayStart(dayStart);

            employeeAbsenceHandler.splitIntoDays();
            employeeAbsenceHandler.shrinkUnpaidLeavesToWorkingHours();
            employeeAbsenceHandler.checkForIntersection();
            employeeAbsenceHandler.prepareInvoiceData();
        }
        return employeeMap;
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

}