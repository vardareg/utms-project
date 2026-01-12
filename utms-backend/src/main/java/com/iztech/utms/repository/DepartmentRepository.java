package main.java.com.iztech.utms.repository;

import com.iztech.utms.model.User;
import com.iztech.utms.model.UniversityStructure.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 2. Department Repository
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    // Required for Application Routing (UC-STU-01)
    Optional<Department> findByName(String name);
}