package com.aspect.salary.service;

import com.aspect.salary.dao.InvoiceDAO;
import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceDAO invoiceDAO;

    @Autowired
    private AbsenceService absenceService;

    public void saveInvoiceListToDB (List<Invoice> invoiceList, int paymentId){
        for(Invoice invoice : invoiceList){
            Integer invoiceId = this.invoiceDAO.addInvoice(invoice, paymentId);

            List <Absence> absenceList = new ArrayList<>();
            absenceList.addAll(invoice.getSickLeave());
            absenceList.addAll(invoice.getOvertime());
            absenceList.addAll(invoice.getUnpaidLeave());
            absenceList.addAll(invoice.getVacation());
            absenceList.addAll(invoice.getFreelance());

            this.absenceService.saveAbsenceListToDB(absenceList, invoiceId);

        }
    }

    public List<Invoice> getInvoicesByPaymentId (int paymentId){
        List <Invoice> paymentInvoices = invoiceDAO.getRawInvoicesByPaymentId(paymentId);
        for (Invoice invoice : paymentInvoices){
            List <Absence> invoiceAbsences = this.absenceService.getAbsencesByInvoiceId(invoice.getId());
            invoice.setAbsences(invoiceAbsences);
        }
        return paymentInvoices;
    }

    public Invoice getInvoiceByUuid (String uuid){
        Invoice invoice = invoiceDAO.getRawInvoiceByUuid(uuid);
        if (invoice == null) return null;
        List <Absence> invoiceAbsences = this.absenceService.getAbsencesByInvoiceId(invoice.getId());
        invoice.setAbsences(invoiceAbsences);

        return invoice;
    }

}
