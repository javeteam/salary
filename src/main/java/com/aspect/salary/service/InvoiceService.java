package com.aspect.salary.service;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.dao.InvoiceDAO;
import com.aspect.salary.entity.*;
import com.aspect.salary.form.InvoiceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceDAO invoiceDAO;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoiceItemService invoiceItemService;

    @Autowired
    private EmployeeService employeeService;

    private int VACATION_DAYS_PER_MONTH = 2;


    public void saveInvoiceListToDB (List<Invoice> invoiceList, int paymentId){
        for(Invoice invoice : invoiceList){
            Integer invoiceId = this.invoiceDAO.addInvoice(invoice, paymentId);
            this.absenceService.saveAbsenceListToDB(invoice.getAbsences(), invoiceId);
            this.invoiceItemService.saveInvoiceItemsListToDB(invoice.getItems(), invoiceId);
        }
    }

    public List<Invoice> getInvoicesByPaymentId (int paymentId){
        List <Invoice> paymentInvoices = invoiceDAO.getRawInvoicesByPaymentId(paymentId);
        for (Invoice invoice : paymentInvoices){
            setInvoiceUnits(invoice);
        }
        return paymentInvoices;
    }

    public Invoice getInvoiceByUuid (String uuid){
        Invoice invoice = invoiceDAO.getRawInvoiceByUuid(uuid);
        if (invoice == null) return null;
        setInvoiceUnits(invoice);
        return invoice;
    }

    public void updateInvoice(Invoice invoice){
        invoice.setModificationDate(LocalDateTime.now());
        this.invoiceDAO.updateInvoice(invoice);
        List<Absence> absenceList = invoice.getAbsences();
        int invoiceId = invoice.getId();
        this.absenceService.updateAbsenceList(absenceList, invoiceId);
        this.invoiceItemService.updateInvoiceItemsFromList(invoice.getItems(),invoiceId);
        Payment payment = this.paymentService.getRawPaymentByInvoiceUuid(invoice.getUuid());
        int paymentId = payment.getId();
        this.paymentService.updatePaymentTotalAmount(paymentId);
}

    public void updateInvoiceStatus(InvoiceForm invoiceForm){
        if(invoiceForm == null) return;
        Invoice invoice = getInvoiceByUuid(invoiceForm.getUuid());
        invoice.setConfirmed(invoiceForm.isConfirmed());
        invoice.setNotes( invoiceForm.isConfirmed() ? "" : invoiceForm.getNotes());
        updateInvoice(invoice);
    }

    public Invoice getBitrixInfo(Employee employee){
        BitrixDAO bitrixDAO = new BitrixDAO();
        List <Absence> absenceList = bitrixDAO.getAbsenceList();
        for(Absence absence : absenceList){
            if(absence.getBitrixUserId() == employee.getBitrixUserId()){
                employee.addAbsence(absence);
            }
        }

        EmployeeService.handleEmployeeAbsences(employee);
        return new Invoice(employee);
    }

    public Boolean isInvoicePaymentCompleted(Invoice invoice){
        String uuid = invoice.getUuid();
        Payment payment = this.paymentService.getRawPaymentByInvoiceUuid(uuid);
        return payment.isComplete();
    }

    private int getVacationDaysIncrement (Invoice invoice){
        Integer monthLeft = this.invoiceDAO.getMonthsAmountBetweenThisAndLastInvoice(invoice);
        if (monthLeft == null) return 0;
        else return monthLeft * VACATION_DAYS_PER_MONTH;
    }


    public InvoiceForm getInvoiceFormByInvoice (Invoice invoice){
        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.setId(invoice.getId());
        invoiceForm.setEmployeeId(invoice.getEmployeeId());
        invoiceForm.setUsername(invoice.getUsername());
        invoiceForm.setConfirmed(invoice.isConfirmed());
        invoiceForm.setCreationDate(invoice.getCreationDate());
        invoiceForm.setModificationDate(invoice.getModificationDate());
        invoiceForm.setSalary(invoice.getSalary());
        invoiceForm.setPaymentToCard(invoice.getPaymentToCard());
        invoiceForm.setBonus(invoice.getBonus());
        invoiceForm.setManagementBonus(invoice.getManagementBonus());
        invoiceForm.setUuid(invoice.getUuid());
        invoiceForm.setNotes(invoice.getNotes());
        invoiceForm.setVacationDaysLeft(invoice.getVacationDaysLeft());
        invoiceForm.setAbsenceIntersection(invoice.getAbsenceIntersection());
        invoiceForm.addAbsences(invoice.getAbsences());
        invoiceForm.setItems(invoice.getItems());
        invoiceForm.setFreelanceHourPrise(invoice.getFreelanceHourPrise());
        invoiceForm.setOvertimeHourPrise(invoice.getOvertimeHourPrise());
        invoiceForm.setWorkingDayPrise(invoice.getWorkingDayPrise());

        return invoiceForm;
    }


    public List<InvoiceForm> getInvoiceFormsByInvoices (List<Invoice> invoiceList){
        List<InvoiceForm> invoiceFormList = new ArrayList<>();
        for(Invoice invoice : invoiceList){
            invoiceFormList.add(this.getInvoiceFormByInvoice(invoice));
        }

        return  invoiceFormList;
    }


    private void setInvoiceUnits (Invoice invoice){
        List <Absence> invoiceAbsences = this.absenceService.getAbsencesByInvoiceId(invoice.getId());
        invoice.setAbsences(invoiceAbsences);
        List<InvoiceItem> invoiceItems = this.invoiceItemService.getInvoiceItemListByInvoiceId(invoice.getId());
        invoice.setItems(invoiceItems);
    }

    private int calculateVacationDays (Invoice invoice){
        int vacationDaysUsed = 0;

        for (Absence absence : invoice.getAbsences()){
            if(absence.getAbsenceType().equals("VACATION") && isStartedLastMonth(absence)){
                Duration duration = Duration.between(absence.getDateFrom(), absence.getDateTo());
                int durationInDays = (int)duration.toDays() + 1;
                vacationDaysUsed += durationInDays;
            }
        }
        return vacationDaysUsed;

    }

    public int getUpdatedVacationDaysInfo(Invoice invoice){
        return invoice.getVacationDaysLeft() - this.calculateVacationDays(invoice) + this.getVacationDaysIncrement(invoice);
    }

    private boolean isStartedLastMonth (Absence absence){
        LocalDate date = absence.getDateFrom().toLocalDate();
        LocalDate lastMonthFirstDay = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate currentMonthFirstDay = LocalDate.now().withDayOfMonth(1);

        return (date.isAfter(lastMonthFirstDay) || date.isEqual(lastMonthFirstDay)) && date.isBefore(currentMonthFirstDay);
    }

}
