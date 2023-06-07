package com.example.logicaldetective.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlotDto {
    private Long id;
    private String name;
    private List<String> text;
    @JsonManagedReference
    private List<FormulaDto> formulas;

}
