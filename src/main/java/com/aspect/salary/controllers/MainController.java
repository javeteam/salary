package com.aspect.salary.controllers;

import com.aspect.salary.entity.*;
import com.aspect.salary.form.EmployeeForm;
import com.aspect.salary.form.InvoiceForm;
import com.aspect.salary.form.PaymentForm;
import com.aspect.salary.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {
    private static Payment payment;
    private Session session = new Session();

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private BitrixService bitrixService;

    @Value("${app.baseUrl}")
    private String BASE_URL;

    private void setCommonAttributes (Model model){
        List <String> missingEmployees = employeeService.printMissingUsers();
        boolean paymentExist = paymentService.isPaymentForLastMonthExist();

        model.addAttribute("missingEmployees", missingEmployees);
        model.addAttribute("paymentExist", paymentExist);
    }

    private LocalDate getPaymentDate (Invoice invoice) {
        return invoice.getPaidUntil().isBefore(invoice.getPaidUntil().withDayOfMonth(invoice.getPaidUntil().lengthOfMonth()))
                ? invoice.getPaidUntil()
                : this.paymentService.getPaymentDate(YearMonth.from(invoice.getPaidFrom().plusMonths(1)));
    }


    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        session = new Session();
        setCommonAttributes(model);

        return "welcome";
    }

    @RequestMapping(value = { "/error" }, method = RequestMethod.GET)
    public String getError(Model model) {
        model.addAttribute("hello","hello");
        return "error";
    }

    @RequestMapping(value = {"/","/login","/logoutSuccessful"})
    public String loginPage(@RequestParam (value = "error", required = false) String error, Model model) {
        if (error != null && error.equals("true")){
            String message = "В авторизації відмовлено. Хибні дані.";
            model.addAttribute("message", message);
        }
        return "login";
    }

    @RequestMapping(value = { "/employeeList" }, method = RequestMethod.GET)
    public String getEmployees(@RequestParam(value = "showActiveOnly", required = false, defaultValue = "true") String activeOnly, Model model) {
        setCommonAttributes (model);

        boolean showActiveOnly = activeOnly.equals("true");
        List<Employee> employeeList = this.employeeService.getAllEmployees(showActiveOnly);
        employeeList.sort(Comparator.comparing(Employee::getSurname));

        model.addAttribute("showActiveOnly", showActiveOnly);
        model.addAttribute("employeeList", this.employeeService.employeeFormListByEmployeeList(employeeList));

        return "employeeList";
    }


    @RequestMapping(value = { "/createPayment" })

    public String addPayment(Model model, HttpServletResponse httpResponse) throws Exception {
        setCommonAttributes (model);
        if(session == null) session = new Session();

        if(!session.isCsvUploaded()){
            return "csvUpload";
        } else if(!session.isPaymentToCardSpecified()){
            session.setEmployeeCardPayments(this.employeeService.getAllEmployees(true));
            model.addAttribute("session", session);
            return "cardPaymentEditor";
        }


        payment = this.paymentService.createPaymentForLastMonth(session);
        List<Invoice> invoiceList = payment.getInvoices();
        invoiceList.sort(Comparator.comparing(Invoice::getUsername));
        boolean isDataValid = paymentService.isDataValid(payment);
        paymentService.calculateTotalAmount(payment);
        String totalAmount = payment.getFormattedTotalAmount();
        String paidPeriod = payment.getPaidPeriod();
        LocalDate paymentDate = invoiceList.isEmpty()
                        ? this.paymentService.getPaymentDate( YearMonth.from(LocalDate.now()))
                        : getPaymentDate(invoiceList.get(0));

        model.addAttribute("isDataValid", isDataValid);
        model.addAttribute("invoices", this.invoiceService.getInvoiceFormsByInvoices(invoiceList));
        model.addAttribute("paymentDate", paymentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        model.addAttribute("paidPeriod", paidPeriod);
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

    @RequestMapping(value = "/updateCardPayment")
    public String updateCardPayment (@ModelAttribute("session")Session session, HttpServletResponse httpResponse) throws IOException{

        this.session.setEmployeeCardPayments(session.getEmployeeCardPayments());
        this.session.setPaymentToCardSpecified(true);

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
        setCommonAttributes (model);
        List<Payment> paymentList = this.paymentService.getAllPayments();
        model.addAttribute("paymentList", this.paymentService.getPaymentFormListByPaymentList(paymentList));

        return "paymentList";
    }

    @RequestMapping(value = { "/payment" })
    public String getInvoiceList(@RequestParam ("id") int id,  Model model) throws IOException {
        Payment payment = this.paymentService.getPaymentById(id);
        List<InvoiceForm> invoiceFormList = this.invoiceService.getInvoiceFormsByInvoices(payment.getInvoices());
        setCommonAttributes (model);

        model.addAttribute("invoiceList", invoiceFormList);
        model.addAttribute("payment", this.paymentService.getPaymentFormByPayment(payment));
        model.addAttribute("allInvoicesConfirmed", this.paymentService.isAllPaymentInvoicesConfirmed(payment));

        return "invoiceList";
    }

    @RequestMapping(value = {"/deletePayment"})
    public String deletePayment (@ModelAttribute("payment")PaymentForm paymentForm, Model model, HttpServletResponse httpResponse) throws IOException{
        this.paymentService.deletePayment(paymentForm);

        httpResponse.sendRedirect("/paymentList");
        return null;
    }

    @RequestMapping(value = {"/paymentComplete"})
    public String paymentComplete(@ModelAttribute("payment") PaymentForm paymentForm, Model model, HttpServletResponse httpResponse) throws IOException{
        this.paymentService.setComplete(paymentForm.getId());

        httpResponse.sendRedirect("/paymentList");
        return null;
    }

    @RequestMapping(value = { "/invoice" })
    public String getInvoiceEditor(@RequestParam ("uuid") String uuid, @RequestParam (value = "absencesUpdated", required = false, defaultValue = "false") String absencesUpdated, Model model) throws IOException {
        Invoice invoice = invoiceService.getInvoiceByUuid(uuid);

        if(absencesUpdated.equals("true")){
            this.invoiceService.synchronizeBitrixInfo(invoice);
        }

        if(invoice.getItems().size() == 0 ) invoice.addItem(new InvoiceItem());
        boolean isPaymentComplete = this.paymentService.getRawPaymentByInvoiceUuid(uuid).isComplete();

        session.addInvoice(invoice);
        InvoiceForm invoiceForm = this.invoiceService.getInvoiceFormByInvoice(invoice);

        model.addAttribute("paymentIncomplete", !isPaymentComplete);
        model.addAttribute("invoice", invoiceForm);
        model.addAttribute("paymentDate", getPaymentDate(invoice).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        return "invoiceEditor";
    }

    @RequestMapping(value = { "/invoiceConfirmation" })
    public String getInvoiceView(@RequestParam ("uuid") String uuid,  Model model, HttpServletResponse httpResponse) throws IOException {
        Invoice invoice = this.invoiceService.getInvoiceByUuid(uuid);
        InvoiceForm invoiceForm = this.invoiceService.getInvoiceFormByInvoice(invoice);

        if (invoice != null){
            Boolean invoicePaymentConfirmed = this.invoiceService.isInvoicePaymentCompleted(invoice);
            if(!invoicePaymentConfirmed){
                model.addAttribute("invoice", invoiceForm);
                model.addAttribute("paymentDate", getPaymentDate(invoice).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                return "invoiceView";
            }
        }

        httpResponse.sendRedirect("/error");
        return null;
    }

    @RequestMapping(value = { "/invoiceStatusUpdate" }, method = RequestMethod.POST)
    public String UpdateInvoiceStatus(@ModelAttribute("invoice") InvoiceForm invoiceForm, Model model) {

        this.invoiceService.updateInvoiceStatus(invoiceForm);
        model.addAttribute("confirmed", invoiceForm.isConfirmed());
        model.addAttribute("notes", invoiceForm.getNotes());

        return "confirmationStatus";
    }

    @RequestMapping(value = { "/invoiceUpdate" }, method = RequestMethod.POST)
    public void UpdateInvoice(@ModelAttribute("invoiceTemplate") InvoiceForm invoiceForm, Model model, HttpServletResponse httpResponse) throws IOException {

        Invoice invoice = session.getInvoice(invoiceForm.getUuid());
        if (invoice == null) httpResponse.sendRedirect("/error");
        invoice.setSalary(invoiceForm.getSalary());
        invoice.setBonus(invoiceForm.getBonus());
        invoice.setPaymentToCard(invoiceForm.getPaymentToCard());
        invoice.setConfirmed(invoiceForm.isConfirmed());
        invoice.setItems(new ArrayList<>());

        for(InvoiceItem item : invoiceForm.getItems()){
            if(item.isNotEmpty()) invoice.addItem(item);
        }

        this.invoiceService.updateInvoice(invoice);

        int paymentId = this.paymentService.getRawPaymentByInvoiceUuid(invoice.getUuid()).getId();
        httpResponse.sendRedirect("payment?id=" + paymentId);
    }

    @RequestMapping(value = { "/employee" })
    public String getEmployeeEditor(@RequestParam (value = "id", required = false) Integer id,  Model model) {
        Employee employee;

        if (id == null){
            employee = new Employee();
        } else {
            employee = this.employeeService.getEmployeeById(id);
        }

        EmployeeForm employeeForm = this.employeeService.employeeFormByEmployee(employee);
        model.addAttribute("employee", employeeForm);

        return "employeeEditor";
    }


    @RequestMapping(value = { "/employeeSave" }, method = RequestMethod.POST)
    public void saveEmployee(@ModelAttribute("employeeForm") EmployeeForm employeeForm, Model model, HttpServletResponse httpResponse) throws IOException {
        Employee employee = this.employeeService.employeeByEmployeeForm(employeeForm);
        this.employeeService.saveEmployeeToDB(employee);

        httpResponse.sendRedirect("/employeeList");
    }

    @RequestMapping(value = { "/dismissEmployee" }, method = RequestMethod.POST)
    public void dismisEmployee(@ModelAttribute("employee") EmployeeForm employeeForm, Model model, HttpServletResponse httpResponse) throws IOException {
        Employee employee = this.employeeService.getEmployeeById(employeeForm.getId());
        employee.setDismissDate(employeeForm.getDismissDate());
        employee.setActive(false);

        //Invoice previousEmploeeInvoice = this.invoiceService.getLatestInvoiceByEmployeeId(employee.getId());
        //LocalDate initial = previousEmploeeInvoice.getPaidUntil();
        //List<Absence> absenceList = this.bitrixService.getBitrixAbsences(initial, employee.getBitrixUserId());



    }

    @RequestMapping(value = {"/setHolidays"}, method = RequestMethod.GET)
    public String setHolidays (Model model){
        LocalDate ld = LocalDate.now();
        int [] daysArray = new int[ld.lengthOfMonth()];
        for (int i = 0; i< daysArray.length; i++){
            daysArray[i] = i+1;
        }

        model.addAttribute("monthLength", ld.lengthOfMonth());
        model.addAttribute("daysArray", daysArray);

        return "holidays";
    }

    @RequestMapping(value = { "/test" })
    public void test(@RequestParam (value = "id", required = false) Integer id, Model model){
        Employee employee = this.employeeService.getEmployeeById(id);

        /*

        EmployeeService.handleEmployeeAbsences(employee);
        List<Absence> updatedAbsences = employee.getAbsences();
        for (Absence absence : updatedAbsences){
            System.out.println(absence.getBitrixUserId() + " --" +absence.getDateFrom() + " - " + absence.getDateTo() + " : " + absence.getAbsenceType());
        }
        */
    }

    @RequestMapping(value = { "/sendNotification" }, method = RequestMethod.POST)
    public void sendNotification(@ModelAttribute("payment") PaymentForm paymentForm, Model model, HttpServletResponse httpResponse) throws IOException {
        Payment payment = this.paymentService.getPaymentById(paymentForm.getId());

        for (Invoice invoice : payment.getInvoices()) {
            String url = this.BASE_URL + "invoiceConfirmation?uuid=" + invoice.getUuid();
            int bitrixUserId = employeeService.getEmployeeById(invoice.getEmployeeId()).getBitrixUserId();
            this.bitrixService.sendNotificationToUser(url, bitrixUserId);
            System.out.println(bitrixUserId);
        }

        payment.setNotificationSent(true);
        this.paymentService.updatePayment(payment);
        httpResponse.sendRedirect("/payment?id=" + payment.getId());

    }


}
