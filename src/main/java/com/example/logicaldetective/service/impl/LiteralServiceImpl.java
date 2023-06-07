package com.example.logicaldetective.service.impl;

import com.example.logicaldetective.entity.LiteralDto;
import com.example.logicaldetective.entity.Literal;
import com.example.logicaldetective.map.LiteralMapper;
import com.example.logicaldetective.repo.LiteralRepo;
import com.example.logicaldetective.service.LiteralService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class LiteralServiceImpl implements LiteralService {
    @Autowired
    private final LiteralRepo literalRepo;
    @Autowired
    private final LiteralMapper literalMapper;

    public LiteralServiceImpl(LiteralRepo literalRepo, LiteralMapper literalMapper) {
        this.literalRepo = literalRepo;
        this.literalMapper = literalMapper;
    }

    @Override
    public List<LiteralDto> findAll() {
        return literalMapper.toListDto(literalRepo.findAll());
    }

    @Override
    public LiteralDto findById(Long id) {
        return Optional.of(getById(id)).map(literalMapper::modelToDto).get();
    }

    @Override
    @Transactional
    public LiteralDto save(LiteralDto literal) {
        return literalMapper.modelToDto(literalRepo.save(
                literalMapper.dtoToModel(literal)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var literal = getById(id);
        literalRepo.delete(literal);
    }

    private Literal getById(Long id) {
        return literalRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Literal with id: " + id + " not found"));
    }
}
