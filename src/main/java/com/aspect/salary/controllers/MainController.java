package com.aspect.salary.controllers;

import com.aspect.salary.config.Config;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.entity.Payment;
import com.aspect.salary.entity.Session;
import com.aspect.salary.service.CSVAbsenceService;
import com.aspect.salary.service.InvoiceService;
import com.aspect.salary.utils.CommonUtils;
import com.aspect.salary.utils.EmployeeAbsenceHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;


import com.aspect.salary.service.EmployeeService;
import com.aspect.salary.entity.Invoice;
import com.aspect.salary.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {
    private static Payment payment;
    private boolean paymentExist;
    private List <String> missingEmployees;
    private Session session;
    private String paymentDate;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoiceService invoiceService;

    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        session = new Session();
        paymentExist = paymentService.isPaymentForThisMonthExist();

        List <String> missingEmployees = employeeService.printMissingUsers();
        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("paymentExist", paymentExist);

        return "welcome";
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
        List <String> missingEmployees = this.employeeService.printMissingUsers();
        List<Employee> employeeList = this.employeeService.getAllEmployeeList();
        employeeList.sort(Comparator.comparing(Employee::getSurname));

        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("employeeList", employeeList);

        return "employeeList";
    }


    @RequestMapping(value = { "/createPayment" })

    public String addPayment(Model model, HttpServletResponse httpResponse) throws Exception {

        if(session == null){
            httpResponse.sendRedirect("/welcome");
            return null;
        }

        if(!session.isCsvUploaded()){
            return "csvUpload";
        }

        missingEmployees = employeeService.printMissingUsers();
        payment = this.paymentService.createPayment(session);

        List<Invoice> invoiceList = payment.getInvoices();
        invoiceList.sort(Comparator.comparing(Invoice::getUsername));
        paymentExist = paymentService.isPaymentForThisMonthExist();
        boolean isDataValid = paymentService.isDataValid(payment);
        paymentService.calculateTotalAmount(payment);
        String totalAmount = payment.getFormattedTotalAmount();
        String paidPeriod = payment.getPaidPeriod();

        paymentDate = EmployeeAbsenceHandler.getPayDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("isDataValid", isDataValid);
        model.addAttribute("invoices", invoiceList);
        model.addAttribute("paymentDate", paymentDate);
        model.addAttribute("paidPeriod", paidPeriod);
        model.addAttribute("paymentExist", paymentExist);
        model.addAttribute("totalAmount", totalAmount);


        return "payment";
    }


    @RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
    public String submit(@RequestParam("CSVFiles") MultipartFile[] files, HttpServletResponse httpResponse ) throws IOException {
        for (MultipartFile file: files) {
            if (!file.isEmpty()){
                session.addCSVAbsence(CSVAbsenceService.fileToCSVAbsenceList(file));
            }
        }
        session.setCsvUploaded(true);
        httpResponse.sendRedirect("/createPayment");
        return null;
    }


    @RequestMapping(value = { "/paymentSave" }, method = RequestMethod.POST)
    public String savePayment(Model model, HttpServletResponse httpResponse) throws IOException {
        paymentService.savePaymentToDB(payment);
        httpResponse.sendRedirect("/paymentList");
        return null;
    }

    @RequestMapping(value = { "/paymentList" })
    public String getPaymentList(Model model) {
        paymentExist = this.paymentService.isPaymentForThisMonthExist();
        List<Payment> paymentList = this.paymentService.getAllPayments();

        model.addAttribute("paymentList", paymentList);
        model.addAttribute("paymentExist", paymentExist);

        return "paymentList";
    }

    @RequestMapping(value = { "/payment" })
    public String getInvoiceList(@RequestParam ("id") int id,  Model model) {
        List<Invoice> invoiceList = this.invoiceService.getInvoicesByPaymentId(id);
        paymentExist = this.paymentService.isPaymentForThisMonthExist();
        List<Payment> paymentList = this.paymentService.getAllPayments();

        model.addAttribute("paymentExist", paymentExist);
        model.addAttribute("invoiceList", invoiceList);

        return "invoiceList";
    }

    @RequestMapping(value = { "/invoice" })
    public String getInvoiceEditor(@RequestParam ("id") int id,  Model model, HttpServletResponse httpResponse) throws IOException {
        Invoice invoice = this.invoiceService.getInvoiceByUuid(String.valueOf(id));
        paymentDate = EmployeeAbsenceHandler.getPayDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        model.addAttribute("invoice", invoice);
        model.addAttribute("paymentDate", paymentDate);

        return "invoiceEditor";
    }

    @RequestMapping(value = { "/invoiceConfirmation" })
    public String getInvoiceView(@RequestParam ("uuid") String uuid,  Model model, HttpServletResponse httpResponse) throws IOException {
        Invoice invoice = this.invoiceService.getInvoiceByUuid(uuid);
        if (invoice == null) httpResponse.sendRedirect("/404");
        paymentDate = EmployeeAbsenceHandler.getPayDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        model.addAttribute("invoice", invoice);
        model.addAttribute("paymentDate", paymentDate);

        return "invoiceView";
    }

    @RequestMapping(value = { "/employee" })
    public String getEmployeeEditor(@RequestParam ("id") int id,  Model model, HttpServletResponse httpResponse) throws IOException {
        Employee employee = this.employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);

        return "employeeEditor";
    }

    @RequestMapping(value = { "/createEmployee" })
    public String addEmployee(Model model, HttpServletResponse httpResponse) throws IOException {
        Employee employee = new Employee(null,0,true,0,0,0,null,null,null,null, CommonUtils.Position.Other,0, LocalTime.of(9,0),LocalTime.of(18,0),LocalTime.of(13,0),LocalTime.of(14,0));
        model.addAttribute("employee", employee);

        return "employeeEditor";
    }

    @RequestMapping(value = { "/employeeSave" })
    public String saveEmployee(@RequestParam ("name") String name, Model model, HttpServletResponse httpResponse) throws IOException {
        Employee employee = new Employee(null,0,true,0,0,0,null,null,null,null, CommonUtils.Position.Other,0, LocalTime.of(9,0),LocalTime.of(18,0),LocalTime.of(13,0),LocalTime.of(14,0));
        model.addAttribute("employee", employee);

        return "employeeEditor";
    }

}
