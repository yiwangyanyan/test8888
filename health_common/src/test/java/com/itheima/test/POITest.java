package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description:
 * @Author: yp
 */
public class POITest {

    @Test
    //读取excel
    public void fun01() throws Exception {
        //1.创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("G:/hello.xlsx");
        //2.获得工作表对象
        XSSFSheet sheet = workbook.getSheetAt(0);
        //3.遍历工作表对象 获得行对象
        for (Row row : sheet) {
            //4.遍历行对象  获得列对象,
            for (Cell cell : row) {
                //获得内容
                System.out.println(cell.getStringCellValue());
            }
        }
        //5.关闭
        workbook.close();

    }

    @Test
    //生成Excel
    public void fun02() throws Exception {
        //1.创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        //2.创建工作表对象
        XSSFSheet sheet = workbook.createSheet("学生信息");
        //3.创建行对象
        XSSFRow row01 = sheet.createRow(0);
        XSSFRow row02 = sheet.createRow(1);
        XSSFRow row03 = sheet.createRow(2);
        //4.创建列对象, 设置内容
        row01.createCell(0).setCellValue("姓名");
        row01.createCell(1).setCellValue("性别");
        row01.createCell(2).setCellValue("住址");

        row02.createCell(0).setCellValue("张三");
        row02.createCell(1).setCellValue("男");
        row02.createCell(2).setCellValue("深圳");

        row03.createCell(0).setCellValue("李四");
        row03.createCell(1).setCellValue("男");
        row03.createCell(2).setCellValue("武汉");

        //5.通过流写到文件(磁盘)
        OutputStream os = new FileOutputStream("G:/students.xlsx");
        workbook.write(os);

        //6.关闭
        os.flush();
        os.close();
        workbook.close();
    }


}
