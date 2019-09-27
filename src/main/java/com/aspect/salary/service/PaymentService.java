package com.aspect.salary.service;

import com.aspect.salary.dao.PaymentDAO;
import com.aspect.salary.entity.*;
import com.aspect.salary.form.InvoiceForm;
import com.aspect.salary.form.PaymentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AbsenceHandler absenceHandler;

    @Value("${app.payday}")
    private int PAYDAY;


    public Payment createPaymentForLastMonth(Session session){
        Payment payment = new Payment();
        //List<Employee> employeeList = this.employeeService.getEmployeeListForLastMonthInvoice(session);
        List<Employee> employeeList = this.employeeService.getAllEmployees(true);

        for(Employee employee : employeeList){
            Invoice invoice = new Invoice(employee);
            this.absenceHandler.addAbsencesToInvoice(invoice);
            addCSVAbsencesToInvoice(invoice, session.getCsvAbsenceList());
            modifyCardPaymentInfo(invoice, session);
            //invoice.setCSVAbsences(session.getCsvAbsenceList());
            payment.addInvoice(invoice);
        }
        return payment;
    }

    public boolean isPaymentForLastMonthExist(){
        LocalDate currentDate = LocalDate.now();
        LocalDateTime lastPaymentDate = this.paymentDAO.getLatestPaymentDate();

        if(lastPaymentDate == null) return false;
        else return (lastPaymentDate.getMonthValue() == currentDate.getMonthValue());
    }

    public void savePaymentToDB (Payment payment){
        int paymentId = this.paymentDAO.addPayment(payment);
        invoiceService.saveInvoiceListToDB(payment.getInvoices(), paymentId);
    }

    public void calculateTotalAmount(Payment payment){
        int paymentTotalAmount = 0;
        for (Invoice invoice: payment.getInvoices()){
            InvoiceForm invoiceForm = this.invoiceService.getInvoiceFormByInvoice(invoice);
            paymentTotalAmount += invoiceForm.getTotalAmount();
        }
        payment.setTotalAmount(paymentTotalAmount);
    }

    public boolean isDataValid(Payment payment){
        List<Invoice> invoices = payment.getInvoices();
        for (Invoice invoice : invoices){
            if (invoice.getAbsenceIntersection().size() > 0) return false;
        }
        return true;
    }

    public Payment getPaymentById(int id){
        Payment payment = this.paymentDAO.getRawPaymentById(id);
        List<Invoice> paymentInvoices = this.invoiceService.getInvoicesByPaymentId(id);
        payment.setInvoices(paymentInvoices);
        return payment;
    }

    public Payment getRawPaymentByInvoiceUuid(String uuid){
        return this.paymentDAO.getPaymentByInvoiceUuid(uuid);
    }

    public void updatePaymentTotalAmount(int paymentId){
        Payment payment = getPaymentById(paymentId);
        calculateTotalAmount(payment);
        this.paymentDAO.updatePayment(payment);
    }

    public List<Payment> getAllPayments(){
        return this.paymentDAO.getAllPayments();
    }

    public PaymentForm getPaymentFormByPayment (Payment payment){
        PaymentForm paymentForm = new PaymentForm();
        paymentForm.setId(payment.getId());
        paymentForm.setComplete(payment.isComplete());
        paymentForm.setNotificationSent(payment.isNotificationSent());
        paymentForm.setCreationDate(payment.getCreationDate());
        paymentForm.setTotalAmount(payment.getTotalAmount());

        return paymentForm;
    }

    public List<PaymentForm> getPaymentFormListByPaymentList (List<Payment> paymentList){
        List <PaymentForm> paymentFormList = new ArrayList<>();
        for(Payment payment : paymentList){
            paymentFormList.add(getPaymentFormByPayment(payment));
        }

        return paymentFormList;
    }

    public void deletePayment (PaymentForm paymentForm){
        int id = paymentForm.getId();
        this.paymentDAO.deletePaymentById(id);
    }

    public boolean isAllPaymentInvoicesConfirmed (Payment payment){
        List <Invoice> invoiceList = payment.getInvoices();
        boolean allInvoicesConfirmed = true;

        for (Invoice invoice : invoiceList){
            if(!invoice.isConfirmed()){
                allInvoicesConfirmed = false;
                break;
            }
        }
        return allInvoicesConfirmed;
    }

    public void setComplete(int id){
        Payment payment = this.paymentDAO.getRawPaymentById(id);
        payment.setComplete(true);
        this.paymentDAO.updatePayment(payment);
        this.employeeService.updateVacationInfo(payment.getInvoices());
    }

    public void updatePayment(Payment payment){
        this.paymentDAO.updatePayment(payment);
    }

    public LocalDate getPaymentDate(YearMonth month){
        int dayOfWeek = month.atDay(PAYDAY).getDayOfWeek().getValue();
        int payDay;

        if (dayOfWeek > 5) payDay = PAYDAY - (dayOfWeek - 5);
        else payDay = PAYDAY;

        return month.atDay(payDay);
    }

    private void addCSVAbsencesToInvoice (Invoice invoice, List<CSVAbsence> csvAbsenceList){
        Employee employee = this.employeeService.getEmployeeById(invoice.getEmployeeId());
        String xtrfUsername = employee.getXtrfName();
        for(CSVAbsence csvAbsence : csvAbsenceList) {
            if (csvAbsence.getEmployeeXtrfName().equals(xtrfUsername)) {
                invoice.addCSVAbsence(csvAbsence);
            }
        }
    }

    private static void modifyCardPaymentInfo(Invoice invoice, Session session){
        int employeeId = invoice.getEmployeeId();
        List <Employee> cardPaymentInfo = session.getEmployeeCardPayments();
        for(Employee employeePaymentInfo : cardPaymentInfo){
            if (employeePaymentInfo.getId() == employeeId){
                invoice.setPaymentToCard(employeePaymentInfo.getPaymentToCard());
                return;
            }
        }
    }


}
