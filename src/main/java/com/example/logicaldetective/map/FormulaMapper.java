package com.example.logicaldetective.map;

import com.example.logicaldetective.entity.Formula;
import com.example.logicaldetective.entity.FormulaDto;
import com.example.logicaldetective.entity.Plot;
import com.example.logicaldetective.entity.PlotDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FormulaMapper {
    Formula dtoToModel(FormulaDto formulaDto);

    FormulaDto modelToDto(Formula formula);

    List<FormulaDto> toListDto(List<Formula> formulas);
}
