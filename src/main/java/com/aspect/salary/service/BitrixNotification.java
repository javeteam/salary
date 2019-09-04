package com.aspect.salary.service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BitrixNotification {

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

    public static void sendNotification (String message, int bitrixUserId) throws IOException {
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

        /*DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(getParamsString(parameters));
        System.out.println(getParamsString(parameters));
        */
        StringBuffer content = new StringBuffer();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            //StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                content.append(inputLine);
            }
            in.close();
        } catch (FileNotFoundException fnfe){
            fnfe.getMessage();
        } finally {
            connection.disconnect();
        }


        int status = connection.getResponseCode();
        System.out.println(status);
        System.out.println(content);

    }

    /*public static void main(String[] args) throws IOException {
        String url = "http://178.165.87.70:8080/invoiceConfirmation?uuid=" + "0b0ce263-0023-4943-97b9-42e4bc13fa3b";
        sendNotification(url, 3);
    }
    */

}
