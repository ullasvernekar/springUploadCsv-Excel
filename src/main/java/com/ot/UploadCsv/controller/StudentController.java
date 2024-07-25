package com.ot.UploadCsv.controller;

import com.ot.UploadCsv.dto.ResponseStructure;
import com.ot.UploadCsv.dto.Student;
import com.ot.UploadCsv.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/uploadData")
@RequiredArgsConstructor
public class StudentController {

    @Autowired
    private StudentService service;

    @PostMapping(value = "/csv", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseStructure<Integer>> uploadStudentsFromCsv(@RequestPart("file") MultipartFile file) {
        try {
            ResponseStructure<Integer> response = service.uploadStudentsFromCsv(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ResponseStructure<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload students: " + e.getMessage(), 0));
        }
    }


    @PostMapping(value = "/excel", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseStructure<Integer>> uploadStudentsFromExcel(@RequestPart("file") MultipartFile file) {
        try {
            ResponseStructure<Integer> response = service.uploadStudentsFromExcel(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseStructure<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload students: " + e.getMessage(), 0));
        }
    }
}