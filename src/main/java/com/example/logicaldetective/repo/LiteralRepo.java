package com.example.logicaldetective.repo;

import com.example.logicaldetective.entity.Formula;
import com.example.logicaldetective.entity.FormulaDto;
import com.example.logicaldetective.entity.Literal;
import com.example.logicaldetective.entity.LiteralDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LiteralRepo extends JpaRepository<Literal, Long> {
}
