package com.aspect.salary.controllers;

import com.aspect.salary.service.AbsenceHandler;
import com.aspect.salary.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.aspect.salary.service.MainHandler;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
    private static List<Person> persons = new ArrayList<>();

    @Autowired
    private MainHandler test;

    static {
        persons.add(new Person("Bill", "Gates"));
        persons.add(new Person("Steve", "Jobs"));
    }

    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    public String admin(Model model) {

        List <String> missingEmployees = test.printMissingUsers();
        model.addAttribute("missingEmployees", missingEmployees);

        return "welcome";
    }

    @RequestMapping(value = { "/personList" }, method = RequestMethod.GET)
    public String viewPersonList(Model model) {

        model.addAttribute("persons", persons);

        return "personList";
    }

    @RequestMapping(value = {"/","/login","/logoutSuccessful"})
    public String loginPage(Model model) {
        return "login";
    }

    @RequestMapping(value = "/loginFailed")
    public String loginFailed(Model model) {

        String message = "В авторизації відмовлено. Хибні дані.";
        model.addAttribute("message", message);

        return "login";
    }

    @RequestMapping(value = { "/employeeList" }, method = RequestMethod.GET)
    public String getEmployees(Model model) {
        List <String> missingEmployees = test.printMissingUsers();

        model.addAttribute("missingEmployees", missingEmployees);

        return "employeeList";
    }

    @RequestMapping(value = { "/new_payment" }, method = RequestMethod.GET)
    public String getNewPayment(Model model) {
        List <String> missingEmployees = test.printMissingUsers();
        Map<Integer, Employee> employeeMap = test.getEmployees();
        List<Invoice> invoiceList = test.preparePayment(employeeMap);

        String paymentDate = AbsenceHandler.getPayDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("invoices", invoiceList);
        model.addAttribute("paymentDate", paymentDate);


        return "payment";
    }
}
