package com.aspect.salary.service;

import com.aspect.salary.dao.InvoiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    @Autowired
    InvoiceDAO invoiceDAO;
}
