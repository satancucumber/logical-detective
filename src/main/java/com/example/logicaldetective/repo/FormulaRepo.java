package com.example.logicaldetective.repo;

import com.example.logicaldetective.entity.Formula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormulaRepo extends JpaRepository<Formula, Long> {
}
