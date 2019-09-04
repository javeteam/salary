package com.aspect.salary.utils;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.utils.CommonUtils.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.aspect.salary.utils.CommonUtils.roundValue;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

public class EmployeeAbsenceHandler {

    private List<Absence> absencesList;
    private List<List<Absence>> intersectionsList;

    private static LocalDate lastMonthInitial = LocalDate.now().minusMonths(1);
    private static LocalDateTime lastMonthFirstDayDT = lastMonthInitial.with(firstDayOfMonth()).atTime(LocalTime.MIN);
    private static LocalDateTime lastMonthLastDayDT = lastMonthInitial.with(lastDayOfMonth()).atTime(LocalTime.MAX);

    private LocalTime workingDayStart;
    private LocalTime lunchStart;
    private LocalTime lunchEnd;
    private LocalTime workingDayEnd;
    private Position position;
    private float workingDayDuration;


    public EmployeeAbsenceHandler(Employee employee){
        if(employee == null) throw new IllegalArgumentException("Null id not valid argument");
        this.absencesList = employee.getAbsences();
        this.intersectionsList = employee.getIntersectionsList();
        this.workingDayStart = employee.getWorkingDayStart();
        this.workingDayEnd = employee.getWorkingDayEnd();
        this.lunchStart = employee.getLunchStart();
        this.lunchEnd = employee.getLunchEnd();
        this.position = employee.getPosition();
        this.workingDayDuration = employee.getWorkingDayDuration();
    }

    public List<List<Absence>> getIntersectionsList(){
        return intersectionsList;
    }

    public List<Absence> getAbsencesList(){
        return absencesList;
    }

    private static boolean isAbsenceVacation(Absence absence){
        return absence.getAbsenceType().equals("VACATION");
    }

    private static boolean isAbsenceOvertime(Absence absence){
        return absence.getAbsenceType().equals("OVERTIME");
    }

    private static boolean isAbsenceUnpaidLeave(Absence absence){
        return absence.getAbsenceType().equals("LEAVEUNPAYED");
    }

    private static boolean isDayTheSame(Absence absence){
        return absence.getDateFrom().getDayOfYear() == absence.getDateTo().getDayOfYear();
    }

    private static boolean isAbsenceDurationNotNull (Absence absence){
        return !absence.getDateFrom().equals(absence.getDateTo());
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static boolean isAbsenceFromLastMonth(Absence absence){

        LocalDateTime dateFrom = absence.getDateFrom();
        LocalDateTime dateTo = absence.getDateTo();

        boolean dateFromOk = ! lastMonthFirstDayDT.isAfter(dateFrom) && ! lastMonthLastDayDT.isBefore(dateFrom);
        boolean dateToOk = ! lastMonthFirstDayDT.isAfter(dateTo) && ! lastMonthLastDayDT.isBefore(dateTo);

        return  dateFromOk || dateToOk;
    }

    public static LocalDate getPayDate(LocalDate month){
        int dayOfWeek = month.withDayOfMonth(BitrixDAO.PAYDAY).getDayOfWeek().getValue();
        int payDay;

        if (dayOfWeek > 5) payDay = BitrixDAO.PAYDAY - (dayOfWeek - 5);
        else payDay = BitrixDAO.PAYDAY;

        return month.withDayOfMonth(payDay);
    }

    private boolean isBetweenLastAndNextPayDay(LocalDate date){
        LocalDate lastPayDate = getPayDate(LocalDate.now().minusMonths(1));
        LocalDate nextPayDate = getPayDate(LocalDate.now().plusMonths(1));
        if( date.isAfter(lastPayDate) && date.isBefore(nextPayDate.plusDays(1) )){
            return true;
        }
        return false;
    }

    private boolean isBetweenLastAndCurrentPayDay(LocalDate date){
        LocalDate lastPayDate = getPayDate(LocalDate.now().minusMonths(1));
        LocalDate currentPayDate = getPayDate(LocalDate.now());
        if( date.isAfter(lastPayDate) && date.isBefore(currentPayDate.plusDays(1) )){
            return true;
        }
        return false;
    }

    private boolean isInLastMonth (LocalDate date){
        LocalDate lastMonthFirstDay = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate currentMonthFirstDay = LocalDate.now().withDayOfMonth(1);

        return (date.isAfter(lastMonthFirstDay) || date.isEqual(lastMonthFirstDay)) && date.isBefore(currentMonthFirstDay);
    }

    private boolean isBetweenCurrentAndNextPayDay(LocalDate date){
        LocalDate currentPayDate = getPayDate(LocalDate.now());
        LocalDate nextPayDate = getPayDate(LocalDate.now().plusMonths(1));
        if( date.isAfter(currentPayDate) && date.isBefore(nextPayDate.plusDays(1) )){
            return true;
        }
        return false;
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

    /**
     *  This method removes from list all absences (except vacations) which both dates (from and to) aren't from last month. Then it truncates absences which is left (except vacations as well) to last month bounds.
     *  Also it removes vacation absences which start date is before or equals last month Payday, or after next month Payday.
     *  PayDay is usually on the sixth day of each month. If this day falls on the weekend, the Payday will be the last working day of the week.
     */
    public void removeInappropriateItems(){
        if (absencesList.size() == 0) return;
        Absence absence;
        Iterator<Absence> it = absencesList.iterator();

        for ( ;it.hasNext(); ){
            absence = it.next();

            if ( ! isAbsenceVacation(absence) ){
                if(! isAbsenceFromLastMonth(absence) ){
                    it.remove();
                } else truncateDatesOfAbsence(absence);
            } else if(! isBetweenLastAndNextPayDay(absence.getDateFrom().toLocalDate())){
                it.remove();
            }
        }
    }

    /**
     * This method splits overtimes and unpaid leaves that cross more than one day into shorter ones (separate for each day)
     * example. Overtime which start date is 2019-01-01 22:00 and end date is 2019-01-02 01:30 would be splited to two -
     * one is from 2019-01-01 22:00 to 2019-01-01 23:59:59 and second - from 2019-01-02 00:00 to 2019-01-02 01:30
     */

    public void splitIntoDays (){
        if (absencesList.size() == 0) return;
        Absence absence;
        Iterator<Absence> it = absencesList.iterator();
        List<Absence> tmpList = new ArrayList<>();

        while (it.hasNext()){
            absence = it.next();
            if ( !isAbsenceOvertime(absence) && !isAbsenceUnpaidLeave(absence)) {
                continue;
            }
            tmpList.addAll(split(absence));
            it.remove();
        }
        absencesList.addAll(tmpList);
    }

    public void shrinkUnpaidLeavesToWorkingHours(){
        if (absencesList.size() == 0) return;

        Absence absence;
        Iterator<Absence> it = absencesList.iterator();
        while (it.hasNext()){
            absence = it.next();
            int dayOfWeek = absence.getDateFrom().getDayOfWeek().getValue();

            if (isAbsenceUnpaidLeave(absence)) {
                if (dayOfWeek <= 5) {
                    LocalDateTime dateFrom = absence.getDateFrom();
                    LocalDateTime dateTo = absence.getDateTo();
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

    public void checkForIntersection() {
        List<Absence> intersectionList = new ArrayList<>();
        Absence previousAbsence = null;
        LocalDateTime maxEnd = null;

        absencesList.sort(Comparator.comparing(Absence::getDateFrom));
        boolean isPreviousAdded = false;
        for (Absence absence : absencesList) {
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
                        intersectionsList.add(intersectionList);
                        intersectionList = new ArrayList<>();
                        isPreviousAdded = false;
                    }
                }

                maxEnd = max(maxEnd, absence.getDateTo());
            }
        }

        if (intersectionList.size() > 0) {
            intersectionsList.add(intersectionList);
        }
    }

    public void prepareInvoiceData(){
        if (absencesList.size() == 0) return;

        Absence absence;
        Iterator<Absence> it = absencesList.iterator();
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
             * If the start date of the vacation is later than payday this month and less or equal to the payday of the next month. That vacation should be paid this time.
             * If the start date of the vacation is later or equal to the first day of previous month and before first this month, it means that it has already been paid and the money should be deducted (except weekends).
             * But if the creation date of the entry in the calendar is later or equals to 1st day of the last month, this vacation was not paid and therefore we should pay only for weekends during this vacation.
             */

            switch (absence.getAbsenceType()){
                case "VACATION":
                    absence.setDurationDays(durationInDays);
                    if(isBetweenCurrentAndNextPayDay(dateFrom.toLocalDate())){
                        absence.setWeight(Weight.POSITIVE);
                        absence.setDurationDays(durationInDays);
                    } else if (isInLastMonth(dateFrom.toLocalDate())){
                        if(creationDate.toLocalDate().isBefore(LocalDate.now().minusMonths(1).withDayOfMonth(1))){
                            absence.setWeight(Weight.NEGATIVE);
                            absence.setDurationDays(durationInDays - weekendsAmount);
                        } else {
                            absence.setWeight(Weight.POSITIVE);
                            absence.setDurationDays(weekendsAmount);
                        }
                    }
                    break;
                case "OVERTIME":
                    float durationValue = roundValue(durationInHours,2);
                    absence.setDurationHours(durationValue);
                    absence.setWeight(Weight.POSITIVE);
                    break;
                case "LEAVEUNPAYED":
                    float lunchIntersectionTime = getDurationOfIntersection(absence.getDateFrom().toLocalTime(), absence.getDateTo().toLocalTime(),lunchStart,lunchEnd);
                    durationValue = roundValue((durationInHours - lunchIntersectionTime),2);
                    absence.setDurationHours(durationValue);
                    // If employee is Project manager or Vendor manager skip not whole day unpaid leaves
                    if((position.equals(Position.PM) || position.equals(Position.VM)) && absence.getDuration() < workingDayDuration){
                        absence.setWeight(Weight.NEUTRAL);
                    } else absence.setWeight(Weight.NEGATIVE);
                    break;
                case "LEAVESICK":
                    absence.setDurationDays(durationInDays);
                    absence.setWeight(Weight.NEUTRAL);
                    break;
            }
        }
    }

    private static void truncateDatesOfAbsence(Absence absence){
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

    private static boolean isNeedTruncation(LocalDateTime dt){
        return (dt.getMonthValue() != lastMonthInitial.getMonthValue());
    }

    private static LocalDateTime truncate(LocalDateTime dt) {
        if (dt.getMonthValue() > lastMonthInitial.getMonthValue()){
            dt = lastMonthLastDayDT;
        } else if (dt.getMonthValue() < lastMonthInitial.getMonthValue()){
            dt = lastMonthFirstDayDT;
        }
        return dt;
    }

    private static List<Absence> split(Absence absence){
        List<Absence> list = new ArrayList<>();
        if ( isDayTheSame(absence) ){
            list.add(absence);
            return list;
        } else {
            LocalDateTime from = absence.getDateFrom();
            LocalDateTime to = from.toLocalDate().atTime(LocalTime.MAX);
            LocalDateTime newFrom = to.toLocalDate().plusDays(1).atTime(LocalTime.MIN);
            LocalDateTime newTo = absence.getDateTo();
            LocalDateTime creationDate = absence.getCreationDate();
            absence.setDateTo(to);
            Absence newAbsence = new Absence(newFrom, newTo, creationDate, absence.getBitrixUserId(), absence.getAbsenceType());
            list.add(absence);
            list.addAll(split(newAbsence));

            return list;
        }
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
