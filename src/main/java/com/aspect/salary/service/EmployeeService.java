package com.aspect.salary.service;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.dao.EmployeeDAO;
import com.aspect.salary.entity.*;
import com.aspect.salary.utils.EmployeeAbsenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeDAO employeeDAO;


    public List<Employee> getEmployeeList(List<Absence> bitrixAbsenceList,  List<CSVAbsence> csvAbsenceList) {
        Map<Integer, Employee>  employeeMap = this.employeeDAO.getRawEmployeeMap();

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

    private static void addCSVAbsencesToEmployee (Employee employee, List<CSVAbsence> csvAbsenceList){
        String xtrfUsername = employee.getXtrfName();
        for(CSVAbsence csvAbsence : csvAbsenceList) {
            if (csvAbsence.getEmployeeXtrfName().equals(xtrfUsername)) {
                employee.addCSVAbsence(csvAbsence);
            }
        }
    }


}