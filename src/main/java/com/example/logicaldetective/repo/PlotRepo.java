package com.example.logicaldetective.repo;

import com.example.logicaldetective.entity.Plot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlotRepo extends JpaRepository<Plot, Long> {
}
