package com.aspect.salary.service;

import com.aspect.salary.dao.InvoiceDAO;
import com.aspect.salary.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceDAO invoiceDAO;

    public void saveInvoiceListToDB (List<Invoice> invoiceList, int paymentId){
        for(Invoice invoice : invoiceList){
            this.invoiceDAO.addInvoice(invoice, paymentId);
        }
    }
}
