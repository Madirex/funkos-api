package com.madirex.funkosspringrest.services.category;

import com.madirex.funkosspringrest.dto.category.CreateCategoryDTO;
import com.madirex.funkosspringrest.dto.category.PatchCategoryDTO;
import com.madirex.funkosspringrest.dto.category.UpdateCategoryDTO;
import com.madirex.funkosspringrest.exceptions.category.CategoryNotFoundException;
import com.madirex.funkosspringrest.exceptions.category.DeleteCategoryException;
import com.madirex.funkosspringrest.mappers.category.CategoryMapperImpl;
import com.madirex.funkosspringrest.models.Category;
import com.madirex.funkosspringrest.repositories.CategoryRepository;
import com.madirex.funkosspringrest.utils.Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase CategoryServiceImpl
 */
@Service
@CacheConfig(cacheNames = "category")
public class CategoryServiceImpl implements CategoryService {

    public static final String FUNKO_NOT_FOUND_MSG = "No se ha encontrado el Category con el ID indicado";
    private final CategoryRepository categoryRepository;
    private final CategoryMapperImpl categoryMapperImpl;

    /**
     * Constructor CategoryServiceImpl
     *
     * @param categoryRepository CategoryRepositoryImpl
     * @param categoryMapperImpl CategoryMapper
     */
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapperImpl categoryMapperImpl) {
        this.categoryRepository = categoryRepository;
        this.categoryMapperImpl = categoryMapperImpl;
    }

    /**
     * Obtiene todas las categorías
     *
     * @param type     Tipo de categoría por la que filtrar
     * @param isActive Si la categoría está activa o no
     * @param pageable Paginación
     * @return Lista de categorías
     */
    @Cacheable
    @Override
    public Page<Category> getAllCategory(Optional<String> type, Optional<Boolean> isActive, Pageable pageable) {
        Specification<Category> specType = (root, query, criteriaBuilder) ->
                type.map(m -> {
                    try {
                        return criteriaBuilder.equal(root.get("type"), Category.Type.valueOf(m.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        return criteriaBuilder.isTrue(criteriaBuilder.literal(false));
                    }
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Category> specIsActive = (root, query, criteriaBuilder) ->
                isActive.map(d -> criteriaBuilder.equal(root.get("active"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Category> criterion = Specification.where(specType)
                .and(specIsActive);
        return categoryRepository.findAll(criterion, pageable);
    }

    /**
     * Obtiene un Category por su ID
     *
     * @param id ID del Category a obtener
     * @return Category con el ID indicado
     * @throws CategoryNotFoundException Si no se ha encontrado el Category con el ID indicado
     */
    @Cacheable(key = "#result.id")
    @Override
    public Category getCategoryById(Long id) throws CategoryNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(FUNKO_NOT_FOUND_MSG));
    }

    /**
     * Actualiza un Category
     *
     * @param id    ID del Category a actualizar
     * @param funko Category con los datos a actualizar
     * @return Category actualizado
     * @throws CategoryNotFoundException Si no se ha encontrado el Category con el ID indicado
     */
    @CachePut(key = "#result.id")
    @Override
    public Category patchCategory(Long id, PatchCategoryDTO funko) throws CategoryNotFoundException {
        var opt = categoryRepository.findById(id);
        if (opt.isPresent()) {
            BeanUtils.copyProperties(funko, opt.get(), Util.getNullPropertyNames(funko));
            opt.get().setId(id);
            opt.get().setUpdatedAt(LocalDateTime.now());
            return categoryRepository.save(opt.get());
        }
        throw new CategoryNotFoundException(FUNKO_NOT_FOUND_MSG);
    }

    /**
     * Crea un Category
     *
     * @param funko CreateCategoryDTO con los datos del Category a crear
     * @return Category creado
     */
    @CachePut(key = "#result.id")
    @Override
    public Category postCategory(CreateCategoryDTO funko) {
        return categoryRepository.save(categoryMapperImpl.toCategory(funko));
    }

    /**
     * Actualiza un Category
     *
     * @param id    ID del Category a actualizar
     * @param funko UpdateCategoryDTO con los datos a actualizar
     * @return Category actualizado
     * @throws CategoryNotFoundException Si no se ha encontrado el Category con el ID indicado
     */
    @CachePut(key = "#result.id")
    @Override
    public Category putCategory(Long id, UpdateCategoryDTO funko) throws CategoryNotFoundException {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada"));
        Category f = categoryMapperImpl.toCategory(existingCategory, funko);
        f.setId(id);
        return categoryRepository.save(f);
    }

    /**
     * Elimina un Category
     *
     * @param id ID del Category a eliminar
     * @throws CategoryNotFoundException Si no se ha encontrado el Category con el ID indicado
     */
    @CacheEvict(key = "#id")
    @Override
    public void deleteCategory(Long id) throws CategoryNotFoundException, DeleteCategoryException {
        var opt = categoryRepository.findById(id);
        if (opt.isEmpty()) {
            throw new CategoryNotFoundException(FUNKO_NOT_FOUND_MSG);
        }
        patchCategory(id, PatchCategoryDTO.builder().active(false).build());
    }
}