package com.example.logicaldetective.map;

import com.example.logicaldetective.entity.Formula;
import com.example.logicaldetective.entity.FormulaDto;
import com.example.logicaldetective.entity.Literal;
import com.example.logicaldetective.entity.LiteralDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LiteralMapper {
    Literal dtoToModel(LiteralDto literalDto);

    LiteralDto modelToDto(Literal literal);

    List<LiteralDto> toListDto(List<Literal> literals);
}
