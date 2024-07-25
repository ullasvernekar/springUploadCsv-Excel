package com.ot.UploadCsv.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.ot.UploadCsv.dao.StudentCsvRepresentation;
import com.ot.UploadCsv.dto.ResponseStructure;
import com.ot.UploadCsv.dto.Student;
import com.ot.UploadCsv.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    @Autowired
    private StudentRepository repository;

    public ResponseStructure<Integer> uploadStudentsFromCsv(MultipartFile file) throws IOException {
        Set<Student> students = parseCsv(file);
        repository.saveAll(students);
        ResponseStructure<Integer> response = new ResponseStructure<>();
        response.setMessage("Data successfully saved ");
        response.setData(students.size());
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    private Set<Student> parseCsv(MultipartFile file) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<StudentCsvRepresentation> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(StudentCsvRepresentation.class);
            CsvToBean<StudentCsvRepresentation> csvToBean =
                    new CsvToBeanBuilder<StudentCsvRepresentation>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            return csvToBean.parse()
                    .stream()
                    .map(csvLine -> Student.builder()
                            .firstname(csvLine.getFname())
                            .lastname(csvLine.getLname())
                            .age(csvLine.getAge())
                            .build()
                    )
                    .collect(Collectors.toSet());
        }
    }

    public ResponseStructure<Integer> uploadStudentsFromExcel(MultipartFile file) throws IOException {
        Set<Student> students = parseExcel(file);
        repository.saveAll(students);
        ResponseStructure<Integer> response = new ResponseStructure<>();
        response.setMessage("Data successfully saved ");
        response.setData(students.size());
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    private Set<Student> parseExcel(MultipartFile file) throws IOException {
        Set<Student> students = new HashSet<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();

                try {
                    int id = (int) getNumericCellValue(row.getCell(0));
                    int age = (int) getNumericCellValue(row.getCell(1));
                    String fname = getStringCellValue(row.getCell(2));
                    String lname = getStringCellValue(row.getCell(3));

                    System.out.println("Row " + row.getRowNum() + ": id=" + id + ", age=" + age + ", firstname=" + fname + ", lastname=" + lname);

                    Student student = new Student();
                    student.setId(id);
                    student.setAge(age);
                    student.setFirstname(fname);
                    student.setLastname(lname);

                    students.add(student);

                } catch (Exception e) {
                    System.err.println("Error processing row: " + row.getRowNum() + " - " + e.getMessage());
                }
            }
        }
        return students;
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            return Double.parseDouble(cell.getStringCellValue().trim());
        } else {
            return 0;
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return "";
        }
    }

}