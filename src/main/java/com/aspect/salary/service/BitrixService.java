package com.aspect.salary.service;

import com.aspect.salary.dao.BitrixDAO;
import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.Employee;
import com.aspect.salary.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BitrixService {

    @Autowired
    BitrixDAO bitrixDAO;

    @Autowired
    PaymentService paymentService;


    /**
     *
     * @return
     * Returns list of all absences for the period between initial's month first day and payment day of month after next month.
     */

    public List<Absence> getBitrixAbsences(LocalDate initial, int bitrixUserId){
        LocalDate dateFrom = initial.withDayOfMonth(1);
        LocalDate dateTo = this.paymentService.getPaymentDate(YearMonth.from(initial.plusMonths(2)));
        return this.bitrixDAO.getAbsenceList(dateFrom, dateTo, bitrixUserId);
    }

    public List<Employee> getBitrixUserList(){
        return this.bitrixDAO.getBitrixUserList();
    }


    public void sendNotificationToUser (String message, int bitrixUserId) throws IOException {
        if (bitrixUserId < 1) return;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("message", message);
        parameters.put("DIALOG_ID",String.valueOf(bitrixUserId));

        String url_str = "https://aspect-ua.com/rest/3/nhgrz7go7wk400ed/im.message.add.json" + getParamsString(parameters);
        URL url = new URL(url_str);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        connection.setSSLSocketFactory(socketFactory);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();

        StringBuffer content = new StringBuffer();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                content.append(inputLine);
            }
            in.close();
        } catch (FileNotFoundException fnfe){
            fnfe.getMessage();
        } finally {
            connection.disconnect();
        }


        /*int status = connection.getResponseCode();
        System.out.println(status);
        System.out.println(content);
        */

    }

    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        result.append(("?"));

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }

}
