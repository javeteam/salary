package com.aspect.salary.service;

import com.aspect.salary.dao.InvoiceItemDAO;
import com.aspect.salary.entity.InvoiceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceItemService {

    @Autowired
    InvoiceItemDAO invoiceItemDAO;

    public void saveInvoiceItemsListToDB (List<InvoiceItem> itemList, int invoiceId){
        for (InvoiceItem item : itemList){
            this.invoiceItemDAO.addInvoiceItem(item, invoiceId);
        }
    }

    public void updateInvoiceItemsFromList (List<InvoiceItem> itemList, int invoiceId){

        List<Integer> itemsIdToDelete = getIdItemListToDelete(itemList,invoiceId);

        for(Integer id: itemsIdToDelete){
            this.invoiceItemDAO.deleteInvoiceItem(id);
        }

        for (InvoiceItem item : itemList){
            if(item.getId() != null){
                this.invoiceItemDAO.updateInvoiceItem(item);

            }
            else this.invoiceItemDAO.addInvoiceItem(item, invoiceId);
        }
    }

    public List<InvoiceItem> getInvoiceItemListByInvoiceId (int invoiceId){
        return this.invoiceItemDAO.getInvoiceItemList(invoiceId);
    }

    private List<Integer> getIdItemListToDelete (List<InvoiceItem> itemList, int invoiceId){
        List<Integer> indexesToDelete = new ArrayList<>();
        List<InvoiceItem> existingInvoiceItems = this.invoiceItemDAO.getInvoiceItemList(invoiceId);

        for(InvoiceItem existingItem : existingInvoiceItems){
            boolean existingItemNotPresentInNewList = true;
            for(InvoiceItem newItem : itemList){
                if(existingItem.getId() == newItem.getId()) {
                    existingItemNotPresentInNewList = false;
                    break;
                }
            }
            if(existingItemNotPresentInNewList) indexesToDelete.add(existingItem.getId());
        }

        return  indexesToDelete;
    }

}
