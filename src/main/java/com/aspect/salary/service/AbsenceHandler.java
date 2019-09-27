package com.aspect.salary.service;

import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.entity.Invoice;
import com.aspect.salary.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspect.salary.utils.CommonUtils.InvoiceType;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.aspect.salary.utils.CommonUtils.roundValue;

@Service
public class AbsenceHandler {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BitrixService bitrixService;


    private Employee employee;
    private Invoice invoice;

    private LocalDateTime invoiceFromDT;
    private LocalDateTime invoiceUntilDT;
    private int invoiceMonthValue;
    private InvoiceType invoiceType;
    private List<List<Absence>> intersectionsList;



    public void addAbsencesToInvoice (Invoice invoice){
        this.invoice = invoice;
        setInitialVariables();

        LocalDate initial = invoice.getPaidFrom();
        List<Absence> absenceList = this.bitrixService.getBitrixAbsences(initial, this.employee.getBitrixUserId());
        handleAbsences(absenceList);
        invoice.setAbsences(absenceList);
        invoice.setAbsenceIntersection(this.intersectionsList);
    }

    private void setInitialVariables (){
        this.invoiceFromDT = this.invoice.getPaidFrom().atTime(0,0,0);
        this.invoiceUntilDT = this.invoice.getPaidUntil().atTime(23,59,59);
        this.invoiceMonthValue = this.invoice.getPaidFrom().getMonthValue();
        this.employee = this.employeeService.getEmployeeById(invoice.getEmployeeId());
        this.intersectionsList = new ArrayList<>();
        this.invoiceType = defineInvoiceType();

    }


    private void handleAbsences (List<Absence> absenceList){
        removeInappropriateItems(absenceList);
        splitIntoDays(absenceList);
        shrinkUnpaidLeavesToWorkingHours(absenceList);
        checkForIntersection(absenceList);
        prepareInvoiceData(absenceList);

    }

    private InvoiceType defineInvoiceType(){
        if (this.employee.getDismissDate() != null) return InvoiceType.DISMISS;
        else return InvoiceType.REGULAR;
    }


    /**
     *  This method removes from list all absences (except vacations) which both dates (from and to) aren't from last month. Then it truncates absences which is left (except vacations as well) to last month bounds.
     *  Also it removes vacation absences which start date is before or equals last month Payday, or after next month Payday.
     *  PayDay is usually on the sixth day of each month. If this day falls on the weekend, the Payday will be the last working day of the week.
     */
    private void removeInappropriateItems(List<Absence> absenceList){
        if (absenceList.size() == 0) return;
        Absence absence;
        Iterator<Absence> it = absenceList.iterator();

        for ( ;it.hasNext(); ){
            absence = it.next();

            if ( ! isAbsenceVacation(absence) ){
                if(! isAbsenceFromInvoicePeriod(absence) ){
                    it.remove();
                } else truncateAbsence(absence);
            } else if(! vacationAppropriate(absence)){
                it.remove();
            }
        }
    }

    private boolean isAbsenceVacation(Absence absence){
        return absence.getAbsenceType().equals("VACATION");
    }

    private boolean isAbsenceOvertime(Absence absence){
        return absence.getAbsenceType().equals("OVERTIME");
    }

    private boolean isAbsenceUnpaidLeave(Absence absence){
        return absence.getAbsenceType().equals("LEAVEUNPAYED");
    }

    private boolean isAbsenceFromInvoicePeriod(Absence absence){

        LocalDateTime dateFrom = absence.getDateFrom();
        LocalDateTime dateTo = absence.getDateTo();


        boolean dateFromOk = ! invoiceFromDT.isAfter(dateFrom) && ! invoiceUntilDT.isBefore(dateFrom);
        boolean dateToOk = ! invoiceFromDT.isAfter(dateTo) && ! invoiceUntilDT.isBefore(dateTo);

        return  dateFromOk || dateToOk;
    }

    private void truncateAbsence(Absence absence){
        LocalDateTime dt = absence.getDateFrom();
        if(isNeedTruncation(dt)){
            dt = truncate(dt);
            absence.setDateFrom(dt);
        }
        dt = absence.getDateTo();
        if(isNeedTruncation(dt)){
            dt = truncate(dt);
            absence.setDateTo(dt);
        }
    }

    private boolean isNeedTruncation(LocalDateTime dt){
        return (dt.getMonthValue() != invoiceMonthValue);
    }

    private LocalDateTime truncate(LocalDateTime dt) {
        if (dt.getMonthValue() > invoiceMonthValue){
            dt = invoiceUntilDT;
        } else if (dt.getMonthValue() < invoiceMonthValue){
            dt = invoiceFromDT;
        }
        return dt;
    }

    /**
     *
     * @param absence
     * @return
     */


    private boolean vacationAppropriate (Absence absence){
        if (!absence.getAbsenceType().equals("VACATION")) return false;
        LocalDate dateFrom = absence.getDateFrom().toLocalDate();
        LocalDate dateTo = absence.getDateTo().toLocalDate();
        LocalDate nextPaymentDay = this.paymentService.getPaymentDate(YearMonth.from(invoice.getPaidFrom().plusMonths(2)));

        if(invoiceType.equals(InvoiceType.REGULAR)){
            return (!dateFrom.isBefore(invoice.getPaidFrom()) && !dateFrom.isAfter(nextPaymentDay));
        } else {    // Dismiss
            return (!dateFrom.isBefore(invoice.getPaidFrom()) && !dateTo.isAfter(invoice.getPaidUntil()));
        }
    }

    /**
     * This method splits overtimes and unpaid leaves that cross more than one day into shorter ones (separate for each day)
     * example. Overtime which start date is 2019-01-01 22:00 and end date is 2019-01-02 01:30 would be splited to two -
     * one is from 2019-01-01 22:00 to 2019-01-01 23:59:59 and second - from 2019-01-02 00:00 to 2019-01-02 01:30
     */

    private void splitIntoDays (List<Absence> absenceList){
        if (absenceList.size() == 0) return;
        Absence absence;
        Iterator<Absence> it = absenceList.iterator();
        List<Absence> tmpList = new ArrayList<>();

        while (it.hasNext()){
            absence = it.next();
            if ( !isAbsenceOvertime(absence) && !isAbsenceUnpaidLeave(absence)) {
                continue;
            }
            tmpList.addAll(split(absence));
            it.remove();
        }
        absenceList.addAll(tmpList);
    }

    private static List<Absence> split(Absence absence){
        List<Absence> list = new ArrayList<>();
        if ( isDayTheSame(absence) ){
            list.add(absence);
            return list;
        } else {
            LocalDateTime from = absence.getDateFrom();
            LocalDateTime to = from.toLocalDate().atTime(23,59,59);
            LocalDateTime newFrom = to.toLocalDate().plusDays(1).atTime(0,0,0);
            LocalDateTime newTo = absence.getDateTo();
            LocalDateTime creationDate = absence.getCreationDate();
            absence.setDateTo(to);
            Absence newAbsence = new Absence(newFrom, newTo, creationDate, absence.getBitrixUserId(), absence.getAbsenceType());
            list.add(absence);
            list.addAll(split(newAbsence));

            return list;
        }
    }

    private static boolean isDayTheSame(Absence absence){
        return absence.getDateFrom().getDayOfYear() == absence.getDateTo().getDayOfYear();
    }

    private void shrinkUnpaidLeavesToWorkingHours(List<Absence> absenceList){
        if (absenceList.size() == 0) return;

        Absence absence;
        Iterator<Absence> it = absenceList.iterator();
        while (it.hasNext()){
            absence = it.next();
            int dayOfWeek = absence.getDateFrom().getDayOfWeek().getValue();

            if (isAbsenceUnpaidLeave(absence)) {
                if (dayOfWeek <= 5) {
                    LocalDateTime dateFrom = absence.getDateFrom();
                    LocalDateTime dateTo = absence.getDateTo();
                    LocalTime workingDayStart = this.employee.getWorkingDayStart();
                    LocalTime workingDayEnd = this.employee.getWorkingDayEnd();

                    if(dateFrom.toLocalTime().isBefore(workingDayStart)) {
                        absence.setDateFrom(dateFrom.toLocalDate().atTime(workingDayStart));
                    }
                    if(dateTo.toLocalTime().isAfter(workingDayEnd) || dateTo.toLocalTime() == LocalTime.MIN){
                        absence.setDateTo(dateTo.toLocalDate().atTime(workingDayEnd));
                    }
                } else it.remove();
            }
        }
    }

    /**
     * This method firstly sort the list of absences by start date. Then it looks for items intersections (when one item contain part of whole another, or even few).
     * It puts all found intersections to List<List<Absence>>
     */

    private void checkForIntersection(List<Absence> absenceList) {
        List<Absence> intersectionList = new ArrayList<>();
        Absence previousAbsence = null;
        LocalDateTime maxEnd = null;

        absenceList.sort(Comparator.comparing(Absence::getDateFrom));
        boolean isPreviousAdded = false;
        for (Absence absence : absenceList) {
            if (previousAbsence == null) {
                previousAbsence = absence;
                maxEnd = absence.getDateTo();
            } else {
                if (maxEnd.isAfter(absence.getDateFrom())) {
                    if(!isPreviousAdded){
                        intersectionList.add(previousAbsence);
                        isPreviousAdded = true;
                    }
                    intersectionList.add(absence);
                } else {
                    previousAbsence = absence;
                    if (intersectionList.size() > 0) {
                        this.intersectionsList.add(intersectionList);
                        intersectionList = new ArrayList<>();
                        isPreviousAdded = false;
                    }
                }

                maxEnd = max(maxEnd, absence.getDateTo());
            }
        }

        if (intersectionList.size() > 0) {
            this.intersectionsList.add(intersectionList);
        }
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    public void prepareInvoiceData(List<Absence> absenceList){
        if (absenceList.size() == 0) return;

        Absence absence;
        Iterator<Absence> it = absenceList.iterator();
        while (it.hasNext()) {
            absence = it.next();
            LocalDateTime dateFrom = absence.getDateFrom();
            LocalDateTime dateTo = absence.getDateTo();
            LocalDateTime creationDate = absence.getCreationDate();
            Duration duration = Duration.between(dateFrom, dateTo);

            float durationInHours = (float) duration.getSeconds() / 3600;
            int durationInDays = (int)duration.toDays() + 1;
            int weekendsAmount = getWeekendsAmount(absence.getDateFrom().toLocalDate(),durationInDays);

            /**
             * If the start date of the vacation is later than PAYDAY this month and less or equal to the PAYDAY of the next month. That vacation should be paid this time.
             * If the start date of the vacation is later or equal to the first day of previous month and before first this month, it means that it has already been paid and the money should be deducted (except weekends).
             * But if the creation date of the entry in the calendar is later or equals to 1st day of the last month, this vacation was not paid and therefore we should pay only for weekends during this vacation.
             */

            switch (absence.getAbsenceType()){
                case "VACATION":
                    absence.setDurationDays(durationInDays);
                    if(isBetweenInvoiceAndNextPayDay(dateFrom.toLocalDate())){
                        if (!creationDate.toLocalDate().isAfter(invoice.getPaidUntil())){
                            absence.setWeight(CommonUtils.Weight.POSITIVE);
                        } else absence.setWeight(CommonUtils.Weight.NEUTRAL);
                        absence.setDurationDays(durationInDays);
                    } else if (isInInvoicePeriod(dateFrom.toLocalDate())){
                        if(wasPaid(dateFrom.toLocalDate(), creationDate.toLocalDate())){
                            absence.setWeight(CommonUtils.Weight.NEGATIVE);
                            absence.setDurationDays(durationInDays - weekendsAmount);
                        } else {
                            absence.setWeight(CommonUtils.Weight.POSITIVE);
                            absence.setDurationDays(weekendsAmount);
                        }
                        /*
                        if(creationDate.toLocalDate().isBefore(this.invoice.getPaidFrom())){
                            absence.setWeight(CommonUtils.Weight.NEGATIVE);
                            absence.setDurationDays(durationInDays - weekendsAmount);
                        } else {
                            absence.setWeight(CommonUtils.Weight.POSITIVE);
                            absence.setDurationDays(weekendsAmount);
                        }
                        */
                    }
                    break;
                case "OVERTIME":
                    float durationValue = roundValue(durationInHours,2);
                    absence.setDurationHours(durationValue);
                    absence.setWeight(CommonUtils.Weight.POSITIVE);
                    break;
                case "LEAVEUNPAYED":
                    float lunchIntersectionTime = getDurationOfIntersection(absence.getDateFrom().toLocalTime(), absence.getDateTo().toLocalTime(),this.employee.getLunchStart(),this.employee.getLunchEnd());
                    durationValue = roundValue((durationInHours - lunchIntersectionTime),2);
                    absence.setDurationHours(durationValue);
                    // If employee is Project manager or Vendor manager skip not whole day unpaid leaves
                    CommonUtils.Position position = this.employee.getPosition();
                    if((position.equals(CommonUtils.Position.PM) || position.equals(CommonUtils.Position.VM)) && absence.getDuration() < this.employee.getWorkingDayDuration()){
                        absence.setWeight(CommonUtils.Weight.NEUTRAL);
                    } else absence.setWeight(CommonUtils.Weight.NEGATIVE);
                    break;
                case "LEAVESICK":
                    absence.setDurationDays(durationInDays);
                    absence.setWeight(CommonUtils.Weight.NEUTRAL);
                    break;
            }
        }
    }

    private int getWeekendsAmount (LocalDate from, int duration){
        int daysAmount = duration;
        int weekendsAmount = 0;
        while (daysAmount > 0){
            if (from.getDayOfWeek().getValue() > 5) weekendsAmount++;
            from = from.plusDays(1);
            daysAmount--;
        }
        return weekendsAmount;
    }

    private boolean isInInvoicePeriod(LocalDate date){
        return ! date.isBefore(this.invoice.getPaidFrom()) && ! date.isAfter(this.invoice.getPaidUntil());
    }

    private boolean isBetweenInvoiceAndNextPayDay(LocalDate date){
        LocalDate invoicePayDate = this.paymentService.getPaymentDate( YearMonth.from(invoice.getPaidFrom().plusMonths(1)));
        LocalDate nextPayDate = this.paymentService.getPaymentDate(YearMonth.from(invoicePayDate.plusMonths(1)));

        return date.isAfter(invoicePayDate) && date.isBefore(nextPayDate.plusDays(1));
    }

    private boolean wasPaid (LocalDate dateFrom, LocalDate creationDate){
        LocalDate invoiceFrom = this.invoice.getPaidFrom();
        LocalDate prevPayDate = this.paymentService.getPaymentDate(YearMonth.from(invoiceFrom));
        boolean paidLastMonth = dateFrom.isAfter(prevPayDate) && creationDate.isBefore(invoiceFrom);
        boolean paidTwoMonthAgo = !dateFrom.isBefore(invoiceFrom) && !dateFrom.isAfter(prevPayDate) && creationDate.isBefore(invoiceFrom.minusMonths(1));

        return paidLastMonth || paidTwoMonthAgo;
    }

    private boolean isBetweenInvoiceFromAndPrevPayDay (LocalDate date){
        LocalDate invoiceFrom = this.invoice.getPaidFrom();
        LocalDate prevPayDate = this.paymentService.getPaymentDate(YearMonth.from(invoiceFrom));
        return !date.isBefore(invoiceFrom) && !date.isAfter(prevPayDate);
    }

    private float getDurationOfIntersection(LocalTime item1_from, LocalTime item1_to, LocalTime item2_from, LocalTime item2_to){

        int a = item1_from.getHour() *60*60 + item1_from.getMinute() * 60 + item1_from.getSecond();
        int b = item1_to.getHour() *60*60 + item1_to.getMinute() * 60 + item1_to.getSecond();
        int c = item2_from.getHour() *60*60 + item2_from.getMinute() * 60 + item2_from.getSecond();
        int d = item2_to.getHour() *60*60 + item2_to.getMinute() * 60 + item2_to.getSecond();
        float len;

        if(a <= c && b >= d) len = d - c;
        else if(a > c && b < d) len = b - a;
        else if(a <= c && b < d) len = b - c;
        else if(a > c && b >= d) len = d - a;
        else len = 0;

        if (len > 0) return len / 3600;
        else return 0;
    }



}
