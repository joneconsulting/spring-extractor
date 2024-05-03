package com.example.springextractmethods;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.SourceRoot;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MethodExtractor {
    public static void main(String[] args) throws IOException {
        String projectDir = "C:\\work\\git\\sccoin_dev_admin\\03.seocho_coin_new\\src\\main\\java\\kr\\co\\ksbpartners\\seochocoin\\controller"; // Adjust this to the path of your project
        SourceRoot sourceRoot = new SourceRoot(Paths.get(projectDir));

        List<Object[]> data = new ArrayList<>();
        sourceRoot.tryToParseParallelized().forEach(parseResult -> processCompilationUnit(parseResult, data));

        writeToExcel(data);
    }

    private static void processCompilationUnit(ParseResult<CompilationUnit> parseResult, List<Object[]> data) {
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            CompilationUnit cu = parseResult.getResult().get();
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                String className = cls.getNameAsString();
                cls.getMethods().forEach(method -> {
                    String methodName = method.getNameAsString();
                    String returnType = method.getType().asString();
                    String parameters = method.getParameters().toString();
                    data.add(new Object[]{className, methodName, returnType, parameters});
                });
            });
        }
    }

    private static void writeToExcel(List<Object[]> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Methods");
        int rowNum = 0;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else {
                    cell.setCellValue(field.toString());
                }
            }
        }
        FileOutputStream outputStream = new FileOutputStream("Methods.xlsx");
        workbook.write(outputStream);
        workbook.close();
    }
}