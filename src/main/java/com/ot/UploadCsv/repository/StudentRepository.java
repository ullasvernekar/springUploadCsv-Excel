package com.ot.UploadCsv.repository;

import com.ot.UploadCsv.dto.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
