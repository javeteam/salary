package com.aspect.salary.service;

import com.aspect.salary.dao.AbsenceDAO;
import com.aspect.salary.entity.Absence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbsenceService {

    @Autowired
    private AbsenceDAO absenceDAO;

    public void saveAbsenceListToDB (List<Absence> absenceList, int invoiceId){
        for(Absence absence : absenceList){
            this.absenceDAO.addAbsence(absence, invoiceId);
        }
    }

    public List<Absence> getAbsencesByInvoiceId(int invoiceId){
        return this.absenceDAO.getAbsencesByInvoiceId(invoiceId);
    }

    public void updateAbsenceList(List<Absence> absenceList, int invoiceId){
        this.absenceDAO.deleteAbsencesByInvoiceId(invoiceId);
        for(Absence absence : absenceList){
            this.absenceDAO.addAbsence(absence, invoiceId);
        }
    }
}
