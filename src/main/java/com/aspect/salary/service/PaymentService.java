package com.aspect.salary.service;

import com.aspect.salary.dao.PaymentDAO;
import com.aspect.salary.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    PaymentDAO paymentDAO;

}
