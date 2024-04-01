package Orakas;

import Orakas.AssistantAvailability.AssistantAvailability;
import Orakas.AssistantAvailability.Availability;
import Orakas.Database.Database;
import Orakas.Humans.Assistant;
import Orakas.Structs.Availability.AvailableAssistants;
import Orakas.Structs.ListOfAssistants;
import Orakas.controllers.ShiftPickerController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class ExcelOutput {

    public ExcelOutput(){

    }
    public static ArrayList<AssistantAvailability> parseCell(ArrayList<String> acceptableInputs, Cell c, HashMap<String, Assistant> map){
        ArrayList<AssistantAvailability> out= new ArrayList<>(25);
       String inp = c.getStringCellValue();
       String[] ar = inp.split(",");
       for(String s : ar){
          s = s.replaceAll(" ","");
           s = s.replaceAll("\n","");
          if(acceptableInputs.contains(s)){
              try{
                  out.add(new AssistantAvailability(map.get(s).getID(),getTime(s)));
              }catch(DateTimeException e){
                  out.add(null);
              }

          }
          out.add(null);
       }
       return out;
    }
    private static Availability getTime(String s)throws DateTimeException{
        if(s.split("-").length >= 2){
            try {
                stringToTimes(s.split("-")[1]);
            }catch (DateTimeException e){
                throw e;
            }
        }
        throw new DateTimeException("No time specified");
    }
    private static Availability stringToTimes(String input) throws DateTimeException {
        if(input.split("::").length >= 2){
            String[] parsed = input.split("::");
            LocalTime start;
            LocalTime end;
            try {
                 start = stringToLocalDateTIme(parsed[0].split(":"));
                 end = stringToLocalDateTIme(parsed[1].split(":"));
            }catch (NumberFormatException e){
                return null;
            }
            return new Availability(start.getHour(),start.getMinute(),end.getHour(),end.getMinute());
        }else{
            throw new DateTimeException("Wrong format for time");
        }
    }
    private static LocalTime stringToLocalDateTIme(String[] input) throws DateTimeException {
        if(input.length >= 2 ){
            int hours = -1;
            int minutes = -1;
            try {
                hours =  Integer.parseInt(input[0]);
                minutes =  Integer.parseInt(input[1]);
            }catch (NumberFormatException e){
                throw new DateTimeException("Wrong format for time");
            }
            if(validateHours(hours) && validateMinutes(minutes)){
                return  LocalTime.of(hours, minutes);
            }else{
                throw new DateTimeException("Wrong format for time");
            }
        }else{
            throw new DateTimeException("Wrong format for time");
        }
    }
    private static boolean validateHours(int hours){
        if(hours> 0 && hours < 23){
            return true;
        }
        return false;
    }
    private static boolean validateMinutes(int minutes){
        if(minutes> 0 && minutes < 59){
            return true;
        }
        return false;
    }
    public static XSSFWorkbook generateXSSFTemplate(String name, int year, int month){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("availableAssistants");
        ArrayList<XSSFRow> rows = new ArrayList<>();
        for(int i =0; i <4;i++){
            rows.add(sheet.createRow(i));
        }

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("@"));
        cellStyle.setWrapText(true);
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint hours = dvHelper.createExplicitListConstraint(generateHours());
        DataValidationConstraint minutes = dvHelper.createExplicitListConstraint(generateMinutes());
        CellRangeAddressList addressListHours = new CellRangeAddressList();
        CellRangeAddressList addressListMinutes = new CellRangeAddressList();
        int maxWidth = 256 * 50;
        createCellWithContent(0,0,"Denní směna "+"-" + name,cellStyle,rows,2);
        createCellWithContent(0,0,"Noční směna "+"-" + name ,cellStyle,rows,3);
        ArrayList<String> startedValues = new ArrayList<>();
        startedValues.add(String.valueOf(Settings.getSettings().getDefStart()[0]));
        startedValues.add(String.valueOf(Settings.getSettings().getDefStart()[1]));
        startedValues.add(String.valueOf(Settings.getSettings().getDefEnd()[0]));
        startedValues.add(String.valueOf(Settings.getSettings().getDefEnd()[1]));
        for(int i = 0; i< Month.of(month).length(Year.isLeap(year)); i++){
            CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, i*4+1, i*4+4);
            sheet.addMergedRegion(cellRangeAddress);
            createCellWithContent(i,1,year+"."+month+ "."+ (i+1) ,cellStyle,rows,0);
            createCellWithContent(i,1,"Hodiny začátku" ,cellStyle,rows,1);
            createCellWithContent(i,2,"Minuty začátku" ,cellStyle,rows,1);
            createCellWithContent(i,3,"Hodiny konce" ,cellStyle,rows,1);
            createCellWithContent(i,4,"Minuty konce" ,cellStyle,rows,1);
            for(int iInter =0; iInter <4;iInter++){
                createCellWithContent(i,iInter+1,startedValues.get(iInter),cellStyle,rows,2);
                createCellWithContent(i,iInter+1,startedValues.get(((iInter>1)? (iInter-2):(iInter+2))),cellStyle,rows,3);
            }
            addressListHours.addCellRangeAddress(new CellRangeAddress(2, 3, i*4+1, i*4+1));
            addressListMinutes.addCellRangeAddress(new CellRangeAddress(2, 3, i*4+2, i*4+2));
            addressListHours.addCellRangeAddress(new CellRangeAddress(2, 3, i*4+1, i*4+3));
            addressListMinutes.addCellRangeAddress(new CellRangeAddress(2, 3, i*4+2, i*4+4));
            /*
            XSSFCell cellDay = day.createCell(i);
            cellDay.setCellStyle(cellStyle);
            XSSFCell cellNight = night.createCell(i);
            cellNight .setCellStyle(cellStyle);
            cellSetup(cellDay, avs.getAvailableAssistantsAtDays().get(i));
            cellSetup(cellNight, avs.getAvailableAssistantsAtNights().get(i));
            cellTitle.setCellValue((i+1)+"."+month+"."+year);
             */
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }

        }
        DataValidation hourValidation = dvHelper.createValidation(hours, addressListHours);
        DataValidation minuteValidation = dvHelper.createValidation(minutes, addressListMinutes);
        hourValidation.setShowErrorBox(true);
        hourValidation.setSuppressDropDownArrow(true);
        minuteValidation.setShowErrorBox(true);
        minuteValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(hourValidation);
        sheet.addValidationData(minuteValidation);
        return workbook;
    }
    public static AvailableAssistants  parseIndividual(HashMap<String, Assistant> mapOfAssistants, String path, int year, int month){
        ArrayList<String> acceptableInputs = ShiftPickerController.prepareAcceptableInputs(mapOfAssistants);
        AvailableAssistants out = new AvailableAssistants(year,month);
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<ArrayList<AssistantAvailability>>();
        ArrayList<ArrayList<AssistantAvailability>>  nightList = new ArrayList<ArrayList<AssistantAvailability>>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(path));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheet("availableAssistants");
            UUID assistant = attemptIdentification(sheet.getRow(2).getCell(0).getStringCellValue(),acceptableInputs,mapOfAssistants);
            if(assistant != null){
                for(int i =0; i < Month.of(month).length(Year.isLeap(year)); i++){
                    Row row1 = sheet.getRow(2);
                    Row row2 = sheet.getRow(3);
                    dayList.add(new ArrayList<AssistantAvailability>(Arrays.asList(parseToAvailability(row1,i,assistant))));
                    nightList.add(new ArrayList<AssistantAvailability>(Arrays.asList(parseToAvailability(row2,i,assistant))));
                }
            }
            out.setAvailableAssistantsAtDays(dayList);
            out.setAvailableAssistantsAtNights(nightList);
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static AssistantAvailability parseToAvailability(Row row1, int i, UUID assistantID){
        Cell startCell1 = row1.getCell(i*4+1);
        Cell startCell2 = row1.getCell(i*4+2);
        Cell endCell1 = row1.getCell(i*4+3);
        Cell endCell2 = row1.getCell(i*4+4);
        if(startCell1.getStringCellValue() !=null && startCell2.getStringCellValue() !=null && endCell1.getStringCellValue() !=null &&
                endCell2.getStringCellValue() !=null ){
            return new AssistantAvailability(assistantID, new Availability(Integer.parseInt(startCell1.getStringCellValue()),
                    Integer.parseInt(startCell2.getStringCellValue()),
                            Integer.parseInt(endCell1.getStringCellValue()),
                                    Integer.parseInt(endCell2.getStringCellValue())));
        }else{
            return null;
        }
    }
    private static UUID attemptIdentification(String st, ArrayList<String> acceptable, HashMap<String, Assistant> mapOfAssistants){
        String[] split = st.split("-");
        if(split.length == 2){
            String s =split[1];
            s = s.replaceAll(" ","");
            s = s.replaceAll("\n","");
            if(acceptable.contains(s)){
                return mapOfAssistants.get(s).getID();
            }else{
                return null;
            }
        }else{
            return null;

        }
    }
    public static ArrayList<String> prepareAcceptableInputs(HashMap<String, Assistant> mapOfAssistants) {
        mapOfAssistants.clear();
        ArrayList<String> acceptable = new ArrayList<>();
        ListOfAssistants lisfa = Database.loadAssistants();
        for(Assistant a: lisfa.getAssistantList()){
            String nameSurname = a.getName()+a.getSurname();
            nameSurname = nameSurname.replaceAll(" ","");
            String surname = a.getSurname();
            surname = surname.replaceAll(" ","");
            mapOfAssistants.put(nameSurname,a);
            mapOfAssistants.put(surname,a);
            acceptable.add(nameSurname);
            acceptable.add(surname);
        }
        return  acceptable;
    }
    private static  void createCellWithContent(int i, int offset, String content, CellStyle cellStyle, ArrayList<XSSFRow> rows, int rowIter){
        Cell cellTitle = rows.get(rowIter).createCell(i*4+offset); // Create a cell in the first column of the current row
       // cellTitle.setCellValue(year+"."+month+ "."+ i+1 );
        cellTitle.setCellValue(content);
        cellTitle.setCellStyle(cellStyle);
    }
    public static void writeXSLX(){
        XSSFWorkbook workbook = generateXSSFTemplate("a a",2024,12);
        try (FileOutputStream fos = new FileOutputStream("C:\\Users\\matej\\VSE\\delete\\"+"individual assistant test"+".xlsx")) {
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static String[] generateMinutes(){
        String[] output = new String[61];
        for(int i = 0; i <60; i++){
            output[i] = String.valueOf(i);
        }
        return output;
    }
    private static  String[] generateHours(){
        String[] output = new String[25];
        for(int i = 0; 24 > i; i++){
            output[i] = String.valueOf(i);
        }
        return output;
    }
    public static XSSFWorkbook convert(AvailableAssistants avs ){
        int year = avs.getYear();
        int month = avs.getMonth();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("availableAssistants");
        XSSFRow title = sheet.createRow(0);
        XSSFRow day = sheet.createRow(1);
        XSSFRow night = sheet.createRow(2);
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        int maxWidth = 256 * 50;
        for(int i = 0; avs.getAvailableAssistantsAtDays().size() > i;i++){
            XSSFCell cellTitle = title.createCell(i);
            cellTitle .setCellStyle(cellStyle);
            XSSFCell cellDay = day.createCell(i);
            cellDay.setCellStyle(cellStyle);
            XSSFCell cellNight = night.createCell(i);
            cellNight .setCellStyle(cellStyle);
            cellSetup(cellDay, avs.getAvailableAssistantsAtDays().get(i));
            cellSetup(cellNight, avs.getAvailableAssistantsAtNights().get(i));
            cellTitle.setCellValue((i+1)+"."+month+"."+year);
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }

        }
        return workbook;
    }
    private static void cellSetup(XSSFCell cell, ArrayList<AssistantAvailability> input){
        StringBuilder mainString = new StringBuilder();
        for(AssistantAvailability o : input){
           // cell.setCellValue(o.getName()+" "+ o.getSurname()+ "\n");
            mainString.append(o.getAssistant());
            mainString.append( ","+"\n");
        }
            cell.setCellValue(mainString.toString());
        }
}
