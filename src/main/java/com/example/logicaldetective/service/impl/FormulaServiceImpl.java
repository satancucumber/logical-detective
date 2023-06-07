package com.example.logicaldetective.service.impl;

import com.example.logicaldetective.entity.Formula;
import com.example.logicaldetective.entity.FormulaDto;
import com.example.logicaldetective.map.FormulaMapper;
import com.example.logicaldetective.repo.FormulaRepo;
import com.example.logicaldetective.service.FormulaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class FormulaServiceImpl implements FormulaService {
    @Autowired
    private final FormulaRepo formulaRepo;
    @Autowired
    private final FormulaMapper formulaMapper;

    public FormulaServiceImpl(FormulaRepo formulaRepo, FormulaMapper formulaMapper) {
        this.formulaRepo = formulaRepo;
        this.formulaMapper = formulaMapper;
    }

    @Override
    public List<FormulaDto> findAll() {
        return formulaMapper.toListDto(formulaRepo.findAll());
    }

    @Override
    public FormulaDto findById(Long id) {
        return Optional.of(getById(id)).map(formulaMapper::modelToDto).get();
    }

    @Override
    @Transactional
    public FormulaDto save(FormulaDto formula) {
        return formulaMapper.modelToDto(formulaRepo.save(
                formulaMapper.dtoToModel(formula)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var formula = getById(id);
        formulaRepo.delete(formula);
    }

    private Formula getById(Long id) {
        return formulaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Formula with id: " + id + " not found"));
    }
}
