package com.eventify.semana_5.service;

import com.eventify.semana_5.exception.ResourceNotFoundException;
import com.eventify.semana_5.model.Category;
import com.eventify.semana_5.repository.CategoryRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La categoría con id: '" + id + "' no fue encontrada."
                ));
    }
}

