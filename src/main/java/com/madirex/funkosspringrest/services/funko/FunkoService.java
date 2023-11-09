package com.madirex.funkosspringrest.services.funko;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.madirex.funkosspringrest.dto.funko.CreateFunkoDTO;
import com.madirex.funkosspringrest.dto.funko.GetFunkoDTO;
import com.madirex.funkosspringrest.dto.funko.PatchFunkoDTO;
import com.madirex.funkosspringrest.dto.funko.UpdateFunkoDTO;
import com.madirex.funkosspringrest.exceptions.category.CategoryNotFoundException;
import com.madirex.funkosspringrest.exceptions.category.CategoryNotValidIDException;
import com.madirex.funkosspringrest.exceptions.funko.FunkoNotFoundException;
import com.madirex.funkosspringrest.exceptions.funko.FunkoNotValidUUIDException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface FunkoService
 */
public interface FunkoService {
    List<GetFunkoDTO> getAllFunko();

    GetFunkoDTO getFunkoById(String id) throws FunkoNotValidUUIDException, FunkoNotFoundException;

    GetFunkoDTO postFunko(CreateFunkoDTO funko) throws CategoryNotFoundException, CategoryNotValidIDException, JsonProcessingException;

    GetFunkoDTO putFunko(String id, UpdateFunkoDTO funko) throws FunkoNotValidUUIDException, FunkoNotFoundException, CategoryNotFoundException, CategoryNotValidIDException, JsonProcessingException;

    GetFunkoDTO patchFunko(String id, PatchFunkoDTO funko) throws FunkoNotValidUUIDException, FunkoNotFoundException, CategoryNotFoundException, CategoryNotValidIDException, JsonProcessingException;

    void deleteFunko(String id) throws FunkoNotFoundException, FunkoNotValidUUIDException, JsonProcessingException;

    GetFunkoDTO updateImage(String id, MultipartFile image, Boolean withUrl) throws FunkoNotFoundException, FunkoNotValidUUIDException, CategoryNotFoundException, CategoryNotValidIDException, IOException;
}
