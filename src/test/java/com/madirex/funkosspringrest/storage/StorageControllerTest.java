package com.madirex.funkosspringrest.storage;

import com.madirex.funkosspringrest.storage.services.FileSystemStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Clase StorageControllerTest
 */
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "spring.profiles.active=test")
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileSystemStorageService storageService;

    /**
     * Test para comprobar que se sirve un archivo correctamente
     *
     * @throws Exception excepción
     */
    @Test
    void testServerFile() throws Exception {
        Resource file = new UrlResource(Paths.get("src/test/resources/funko.png").toUri());
        when(storageService.loadAsResource(anyString())).thenReturn(file);
        mockMvc.perform(get("/storage/funko.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("image/png")));
    }


    /**
     * Test para comprobar que no haya contenido al servir un archivo
     *
     * @throws Exception excepción
     */
    @Test
    void testServeFileNotContentType() throws Exception {
        byte[] content = "Contenido de prueba".getBytes();
        new MockMultipartFile("file", "test.txt", null, content);
        when(storageService.loadAsResource(anyString())).thenReturn(new ByteArrayResource(content));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/storage/test.txt");
        request.setMethod("GET");
        mockMvc.perform(get("/storage/test.txt"))
                .andExpect(status().isBadRequest());
    }
}