package com.aspect.salary.service;

import com.aspect.salary.entity.CSVAbsence;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CSVAbsenceService {
    public static List<CSVAbsence> fileToCSVAbsenceList (MultipartFile multipartFile) throws IllegalArgumentException{
        List<CSVAbsence> csvAbsences = new ArrayList<>();

        try{
            File file = multipartToFile(multipartFile);
            if(file == null){
                return csvAbsences;
            }

            CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.TDF);
            List<CSVRecord> csvLines = parser.getRecords();
            int usernameIndex = -1;
            int jobValueIndex = -1;
            int grossTotalIndex = -1;

            for(CSVRecord line : csvLines){

                if (usernameIndex < 0 && jobValueIndex < 0 && grossTotalIndex < 0){
                    for(int i = 0; i < line.size(); i++){
                        if(line.get(i).contains("Provider > Name")){
                            usernameIndex = i;
                        } else if (line.get(i).contains("Jobs Value")){
                            jobValueIndex = i;
                        } else if (line.get(i).contains("Gross Total")){
                            grossTotalIndex = i;
                        }
                    }
                } else {
                    String username = line.get(usernameIndex);
                    String jobValue = line.get(jobValueIndex);
                    String grossTotal = line.get(grossTotalIndex);

                    CSVAbsence item = getCSVAbsence(username,jobValue,grossTotal);
                    csvAbsences.add(item);
                }
            }
            if(usernameIndex < 0 && csvAbsences.size() == 0) throw new IllegalArgumentException( "wrong file structure");

        } catch (IOException e ){
            System.out.println(e.getLocalizedMessage());
        }
        return csvAbsences;
    }

    private static CSVAbsence getCSVAbsence(String username, String jobValue, String totalGross){
        String absenceType;
        int prise = 0;
        String employeeXtrfName = "";

        if (username.contains("Overtime")){
            absenceType = "OVERTIME";
        } else if(username.contains("Freelance")){
            absenceType = "FREELANCE";
        } else return null;

        int intJobValue = Math.round(Float.parseFloat(jobValue));
        int intTotalGross = Math.round(Float.parseFloat(totalGross));
        prise = ( intJobValue != 0 ? intJobValue : intTotalGross );

        String arr[] = username.split(" ");
        for (int i = 0; i < arr.length -1; i++){
            employeeXtrfName = (employeeXtrfName.isEmpty() ? arr[i] : employeeXtrfName + " " + arr[i]);
        }
        return new CSVAbsence(employeeXtrfName, absenceType, prise);
    }

    /*
    private static int getIntFromString(String str){
        Pattern currencyPattern = Pattern.compile("[+-]?([0-9]*[.])?[0-9]+");
        Matcher matcher = currencyPattern.matcher(str);
        if(matcher.find()){
            String floatString = matcher.group();
            float value = Float.parseFloat(floatString);
            return Math.roundValue(value);
        } else return 0;
    }
    */

    private static File multipartToFile(MultipartFile file) throws IOException {
        if(file == null || file.getOriginalFilename() == null) return null;

        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
