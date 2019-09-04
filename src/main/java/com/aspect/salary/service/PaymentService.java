package com.aspect.salary.service;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.dao.PaymentDAO;
import com.aspect.salary.entity.*;
import com.aspect.salary.form.InvoiceForm;
import com.aspect.salary.form.PaymentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private BitrixDAO bitrixDAO = new BitrixDAO();


    public Payment createPayment(Session session){
        Payment payment = new Payment();
        List<Absence> bitrixAbsenceList = bitrixDAO.getAbsenceList();
        List<Employee> employeeList = this.employeeService.getEmployeeList(bitrixAbsenceList,session);

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
        List<Invoice> paymentInvoices = invoiceService.getInvoicesByPaymentId(id);
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
}
