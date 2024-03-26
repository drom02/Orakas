package graphics.sorter;

import graphics.sorter.AssistantAvailability.AssistantAvailability;
import graphics.sorter.Structs.AvailableAssistants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ExcelOutput {

    public ExcelOutput(){

    }
    public static ArrayList<Assistant> parseCell(ArrayList<String> acceptableInputs, Cell c, HashMap<String,Assistant> map){
        ArrayList<Assistant> out= new ArrayList<>(25);
       String inp = c.getStringCellValue();
       String[] ar = inp.split(",");
       for(String s : ar){
          s = s.replaceAll(" ","");
           s = s.replaceAll("\n","");
          if(acceptableInputs.contains(s)){
            out.add(map.get(s));
          }
       }
       return out;
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
