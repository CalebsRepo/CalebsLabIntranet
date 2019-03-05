package calebslab.calebslabintranet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class holidayConditionExcel {

    public Workbook makeExcel(final String result){
        int idx = result.indexOf("@");
        String result1 =  "";
        String result2 =  "";
        result1 = result.substring(0, idx);
        result2 = result.substring(idx+1);

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("연차사용내역");
        HSSFRow titleRow = sheet.createRow(0);
        HSSFRow headerRow = sheet.createRow(1);
        HSSFCell cell;

        /* 셀스타일*/
        sheet.setColumnWidth((short)0, (short)1000);
        sheet.setColumnWidth((short)1, (short)4000);
        sheet.setColumnWidth((short)4, (short)3000);
        sheet.setColumnWidth((short)5, (short)3000);

        titleRow.setHeight((short)512);

        Font titleFont;
        titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(IndexedColors.BLACK.getIndex());

        Font headerFont;
        headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        Font dataFont;
        dataFont = workbook.createFont();
        dataFont.setFontHeightInPoints((short) 10);
        dataFont.setColor(IndexedColors.BLACK.getIndex());

        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setBorderTop(BorderStyle.DOUBLE);
        titleStyle.setBorderLeft (BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFCellStyle titleStyle2 = workbook.createCellStyle();
        titleStyle2.setFont(headerFont);
        titleStyle2.setBorderTop(BorderStyle.DOUBLE);
        titleStyle2.setBorderLeft (BorderStyle.THIN);
        titleStyle2.setBorderRight(BorderStyle.THIN);
        titleStyle2.setBorderBottom(BorderStyle.THIN);
        titleStyle2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        titleStyle2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        titleStyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft (BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft (BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        /* title 로우*/
        cell = titleRow.createCell(0);
        cell.setCellValue("갈렙스랩 연차사용내역");
        cell.setCellStyle(titleStyle);
        cell = titleRow.createCell(1);
        cell.setCellStyle(titleStyle);
        cell = titleRow.createCell(2);
        cell.setCellStyle(titleStyle);
        cell = titleRow.createCell(3);
        cell.setCellStyle(titleStyle);
        cell = titleRow.createCell(4);
        cell.setCellStyle(titleStyle);
        cell = titleRow.createCell(5);
        cell.setCellStyle(titleStyle);

        cell = titleRow.createCell(6);
        cell.setCellValue("Date : " + sdf.format(today));
        cell.setCellStyle(titleStyle2);

        // 셀병합
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,4));

        /* 헤더로우 */
        cell = headerRow.createCell(0);
        cell.setCellValue("No.");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("아이디");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("이름");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("직급");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("입사일");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("근속기간");
        cell.setCellStyle(headerStyle);

        if (result != null) {
            try {
                JSONArray arr = new JSONArray(result1);     // 사원정보
                JSONArray arr2 = new JSONArray(result2);    // 연차사용내역

                int userIdx = 0;        // 안쪽 for문 대상멤버 선택을 위한 변수
                int MaxWorkYear = 0;

                /* 전체사원정보 */
                for(int memberIdx=0; memberIdx < arr.length(); memberIdx++) {
                    JSONObject jObject = arr.getJSONObject(memberIdx);

                    String id =jObject.getString("id");
                    String name = jObject.getString("name");
                    String grade = jObject.getString("grade");
                    String joinYmd = jObject.getString("join_ymd");
                    String joinYear = jObject.getString("join_year");
                    int joinMon = jObject.getInt("join_mon");
                    int workMon = joinMon - (Integer.parseInt(joinYear)*12);
                    int joinDay = jObject.getInt("join_day");
                    int workYear = Integer.parseInt(joinYear)+1;
                    String workingDate = "";
                    double wd80 = 0;

                    int cellIdx = 6;            // 연차내역 시작위치 초기화

                    /* 최대 근무년차 계산*/
                    if(Integer.parseInt(joinYear) > MaxWorkYear) {
                        MaxWorkYear = Integer.parseInt(joinYear);
                    }
                    /* 근속년월 Format */
                    if (Integer.parseInt(joinYear) > 0) {
                        workingDate = joinYear +"년" +workMon + "개월";
                    }else {
                        workingDate = workMon + "개월";
                    }

                    GregorianCalendar cal = new GregorianCalendar();
                    if (cal.isLeapYear(cal.get(Calendar.YEAR))) {
                        wd80 = 366 * 0.8;       // 윤년
                    }
                    else {
                        wd80 = 365 * 0.8;       // 평년
                    }

                    /* 사원정보 입력 */
                    HSSFRow dataRow = sheet.createRow(memberIdx+2);

                    cell = dataRow.createCell(0);     // no
                    cell.setCellValue(memberIdx+1);
                    cell.setCellStyle(dataStyle);

                    cell = dataRow.createCell(1);     // 아이디
                    cell.setCellValue(id);
                    cell.setCellStyle(dataStyle);

                    cell = dataRow.createCell(2);     // 이름
                    cell.setCellValue(name);
                    cell.setCellStyle(dataStyle);

                    cell = dataRow.createCell(3);     // 직급
                    cell.setCellValue(grade);
                    cell.setCellStyle(dataStyle);

                    cell = dataRow.createCell(4);     // 입사일
                    cell.setCellValue(joinYmd);
                    cell.setCellStyle(dataStyle);

                    cell = dataRow.createCell(5);     // 근속기간
                    cell.setCellValue(workingDate);
                    cell.setCellStyle(dataStyle);

                    for(int i=0; i <workYear; i++){
                        double holidayCal = 0;
                        double holidayCnt = 15;     // 기본 연차 발생일

                        JSONObject jObject2 = arr2.getJSONObject(userIdx++);

                        String hsYmd = jObject2.getString("hs_ymd");
                        String heYmd = jObject2.getString("he_ymd");
                        String holidayUsed = jObject2.getString("holiday_used");

                        /* 연차발생일 계산 */
                        int workingYear = 0;
                        workingYear = (Integer.parseInt(hsYmd.substring(0,4))  -  Integer.parseInt(joinYmd.substring(0,4))) +1;
                        if (workingYear == 3) {
                            holidayCnt = holidayCnt + 1;
                        }else if (workingYear > 3) {
                            holidayCal = holidayCnt + 1 + Math.floor((workingYear - 3)/2);
                            holidayCnt = (holidayCal > 25) ? 25 : holidayCal;

                        }else if (joinDay < wd80 ){
                            holidayCnt = joinMon *1;
                        }
                        Double holidayLeft = holidayCnt - Float.parseFloat(holidayUsed);

                        /* 연차정보 입력*/
                        cell = dataRow.createCell(cellIdx++);     // 연차발생기간
                        cell.setCellValue(hsYmd+" ~ "+ heYmd);
                        cell.setCellStyle(dataStyle);

                        cell = dataRow.createCell(cellIdx++);     // 연차발생일
                        cell.setCellValue(holidayCnt);
                        cell.setCellStyle(dataStyle);

                        cell = dataRow.createCell(cellIdx++);     // 연차사용일
                        cell.setCellValue(holidayUsed);
                        cell.setCellStyle(dataStyle);

                        cell = dataRow.createCell(cellIdx++);     // 연차잔여일
                        cell.setCellValue(holidayLeft);
                        cell.setCellStyle(dataStyle);
                    }
                }

                /* 최대 근무년차만큼 헤더로우 추가 */
                int headerIdx = 0;
                for(int i=0; i<=MaxWorkYear; i++) {

                    sheet.setColumnWidth(MaxWorkYear + headerIdx, (short)5000);
                    cell = headerRow.createCell(MaxWorkYear + headerIdx++);
                    cell.setCellValue("연차발생기간");
                    cell.setCellStyle(headerStyle);

                    sheet.setColumnWidth(MaxWorkYear + headerIdx, (short)3000);
                    cell = headerRow.createCell(MaxWorkYear + headerIdx++);
                    cell.setCellValue("발생일");
                    cell.setCellStyle(headerStyle);

                    sheet.setColumnWidth(MaxWorkYear + headerIdx, (short)3000);
                    cell = headerRow.createCell(MaxWorkYear + headerIdx++);
                    cell.setCellValue("사용일");
                    cell.setCellStyle(headerStyle);

                    sheet.setColumnWidth(MaxWorkYear + headerIdx, (short)3000);
                    cell = headerRow.createCell(MaxWorkYear + headerIdx++);
                    cell.setCellValue("잔여일");
                    cell.setCellStyle(headerStyle);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return workbook;
    }
}
