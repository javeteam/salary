package com.aspect.salary;

import com.aspect.salary.entity.Invoice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;


@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        //Invoice invoice = new Invoice();
        //System.out.println(invoice.hashCode());
        //System.out.println(invoice.getSomething());
        //System.out.println(invoice.getCreationDate());
        //System.out.println(EmployeeAbsenceHandler.getPayDate(LocalDate.now()));
        //System.out.println(EmployeeAbsenceHandler.getPayDate(LocalDate.now().minusMonths(1)));
        //System.out.println(EmployeeAbsenceHandler.getPayDate(LocalDate.now().plusMonths(6)));


    }
}

// SELECT ID,NAME,LAST_NAME FROM `b_user` WHERE ACTIVE = 'Y'