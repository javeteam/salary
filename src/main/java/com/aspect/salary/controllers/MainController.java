package com.aspect.salary.controllers;

import com.aspect.salary.entity.Payment;
import com.aspect.salary.entity.Session;
import com.aspect.salary.service.CSVAbsenceService;
import com.aspect.salary.utils.EmployeeAbsenceHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PaymentService paymentService;

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
        List <String> missingEmployees = employeeService.printMissingUsers();

        model.addAttribute("missingEmployees", missingEmployees);

        return "employeeList";
    }

    @RequestMapping(value = { "/new_payment" }, method = RequestMethod.GET)
    public String getNewPayment(Model model) {
        missingEmployees = employeeService.printMissingUsers();
        payment = this.paymentService.createPayment(session);

        List<Invoice> invoiceList = payment.getInvoices();
        paymentExist = paymentService.isPaymentForThisMonthExist();
        boolean isDataValid = paymentService.isDataValid(payment);

        String paymentDate = EmployeeAbsenceHandler.getPayDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("isDataValid", isDataValid);
        model.addAttribute("invoices", invoiceList);
        model.addAttribute("paymentDate", paymentDate);
        model.addAttribute("paymentExist", paymentExist);


        return "test";
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
        paymentExist = paymentService.isPaymentForThisMonthExist();
        boolean isDataValid = paymentService.isDataValid(payment);

        String paymentDate = EmployeeAbsenceHandler.getPayDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("isDataValid", isDataValid);
        model.addAttribute("invoices", invoiceList);
        model.addAttribute("paymentDate", paymentDate);
        model.addAttribute("paymentExist", paymentExist);


        return "test";
    }


    @RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
    public String submit(@RequestParam("CSVFiles") MultipartFile[] files,HttpServletResponse httpResponse ) throws IOException {
        for (MultipartFile file: files) {
            if (!file.isEmpty()){
                session.addCSVAbsence(CSVAbsenceService.fileToCSVAbsenceList(file));
            }
        }
        session.setCsvUploaded(true);
        httpResponse.sendRedirect("/create_payment");
        return null;
    }


    @RequestMapping(value = { "/new_payment/save" }, method = RequestMethod.POST)
    public String savePayment(Model model) {
        paymentExist = paymentService.isPaymentForThisMonthExist();
        paymentService.calculateTotalAmount(payment);
        paymentService.savePaymentToDB(payment);

        model.addAttribute("paymentExist", paymentExist);
        return "welcome";
    }

    @RequestMapping(value = { "/paymentList" })
    public String getPaymentList(Model model) {
        paymentExist = paymentService.isPaymentForThisMonthExist();
        List<Payment> paymentList = paymentService.getPayments();

        model.addAttribute("paymentList", paymentList);
        return "paymentList";
    }
}
