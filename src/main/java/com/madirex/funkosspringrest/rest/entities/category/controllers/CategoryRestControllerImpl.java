package com.madirex.funkosspringrest.rest.entities.category.controllers;

import com.madirex.funkosspringrest.rest.entities.category.dto.CreateCategoryDTO;
import com.madirex.funkosspringrest.rest.entities.category.dto.PatchCategoryDTO;
import com.madirex.funkosspringrest.rest.entities.category.dto.UpdateCategoryDTO;
import com.madirex.funkosspringrest.rest.entities.category.exceptions.CategoryNotFoundException;
import com.madirex.funkosspringrest.rest.entities.category.exceptions.DeleteCategoryException;
import com.madirex.funkosspringrest.rest.entities.category.models.Category;
import com.madirex.funkosspringrest.rest.entities.category.services.CategoryService;
import com.madirex.funkosspringrest.rest.pagination.exceptions.PageNotValidException;
import com.madirex.funkosspringrest.rest.pagination.model.PageResponse;
import com.madirex.funkosspringrest.rest.pagination.util.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Clase CategoryRestControllerImpl
 */
@RestController
@RequestMapping("/api/category")
public class CategoryRestControllerImpl implements CategoryRestController {

    private final CategoryService service;
    private final PaginationLinksUtils paginationLinksUtils;

    /**
     * Constructor de la clase
     *
     * @param service              Servicio de Category
     * @param paginationLinksUtils Utilidad para la paginación
     */
    @Autowired
    public CategoryRestControllerImpl(CategoryService service, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Método para obtener todas las categorías
     *
     * @param type      tipo
     * @param isActive  ¿está activo?
     * @param page      página
     * @param size      tamaño de página
     * @param sortBy    ordenar por
     * @param direction dirección
     * @param request   petición
     * @return ResponseEntity con el código de estado
     */
    @GetMapping()
    public ResponseEntity<PageResponse<Category>> findAll(
            @RequestParam(required = false) Optional<String> type,
            @RequestParam(required = false) Optional<Boolean> isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        if (page < 0 || size < 1) {
            throw new PageNotValidException("La página no puede ser menor que 0 y su tamaño no debe de ser menor a 1.");
        }
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Category> pageResult = service.getAllCategory(type, isActive, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Método para obtener una categoría por su id
     *
     * @param id id de la categoría
     * @return ResponseEntity con el código de estado
     * @throws CategoryNotFoundException de la categoría
     */
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Category> findById(@Valid @PathVariable Long id) throws CategoryNotFoundException {

        Category category = service.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Método para crear un Category
     *
     * @param category Objeto CreateCategoryDTO con los campos a crear
     * @return ResponseEntity con el código de estado
     */
    @PostMapping()
    @Override
    public ResponseEntity<Category> post(@Valid @RequestBody CreateCategoryDTO category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.postCategory(category));
    }

    /**
     * Método para actualizar un Category
     *
     * @param id    id de la categoría
     * @param funko categoría a actualizar
     * @return ResponseEntity con el código de estado
     * @throws CategoryNotFoundException de la categoría
     */
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Category> put(@Valid @PathVariable Long id, @Valid @RequestBody UpdateCategoryDTO funko) throws CategoryNotFoundException {
        Category updatedCategory = service.putCategory(id, funko);
        return ResponseEntity.ok(updatedCategory);
    }


    /**
     * Método para actualizar parcialmente un Category
     *
     * @param id    id de la categoría
     * @param funko categoría a actualizar
     * @return ResponseEntity con el código de estado
     * @throws CategoryNotFoundException de la categoría
     */
    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<Category> patch(@Valid @PathVariable Long id, @Valid @RequestBody PatchCategoryDTO funko) throws CategoryNotFoundException {
        Category updatedCategory = service.patchCategory(id, funko);
        return ResponseEntity.ok(updatedCategory);
    }


    /**
     * Método para eliminar un Category
     *
     * @param id id de la categoría
     * @return ResponseEntity con el código de estado
     * @throws CategoryNotFoundException de la categoría
     */
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<String> delete(@Valid @PathVariable Long id) throws CategoryNotFoundException {
        try {
            service.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (DeleteCategoryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se ha eliminado el Category. Revisa que no existan Funkos asociados a él.");
        }
    }
}