package com.aspect.salary.service;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.dao.PaymentDAO;
import com.aspect.salary.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EmployeeService employeeService;

    private BitrixDAO bitrixDAO = new BitrixDAO();


    public Payment createPayment(Session session){
        Payment payment = new Payment();
        List<Absence> bitrixAbsenceList = bitrixDAO.getAbsenceList();
        List<CSVAbsence> csvAbsenceList = session.getCsvAbsenceList();
        List<Employee> employeeList = this.employeeService.getEmployeeList(bitrixAbsenceList,csvAbsenceList);

        for(Employee employee : employeeList){
            Invoice invoice = new Invoice(employee);
            payment.addInvoice(invoice);
        }
        return payment;
    }

    public boolean isPaymentForThisMonthExist(){
        LocalDate currentDate = LocalDate.now();
        LocalDateTime lastPaymentDate = paymentDAO.getLatestPaymentDate();

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
            paymentTotalAmount += invoice.getTotalAmount();
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

    public List<Payment> getPayments(){
        return this.paymentDAO.getAllPayments();
    }
}
