package com.madirex.funkosspringrest.storage;


import com.madirex.funkosspringrest.exceptions.storage.StorageBadRequest;
import com.madirex.funkosspringrest.exceptions.storage.StorageInternal;
import com.madirex.funkosspringrest.exceptions.storage.StorageNotFound;
import com.madirex.funkosspringrest.services.storage.FileSystemStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileSystemStorageServiceTest {

    @Mock
    private Path rootLocation;

    @InjectMocks
    private final FileSystemStorageService fileSystemStorageService = new FileSystemStorageService("funkos-images");

    byte[] bytesPNG = { (byte) 137, (byte) 80, (byte) 78, (byte) 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
            0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, 21, (byte) -60, (byte) -60,
            (byte) 137, (byte) 80, (byte) 78, (byte) 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
            0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, 21, (byte) -60, (byte) -60 };

    @BeforeEach
    public void setUp() throws IOException {
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();
        fileSystemStorageService.store(new MockMultipartFile("funko", "funko.png",
                "image/png", bytesPNG), List.of("jpg", "jpeg", "png"), UUID.randomUUID().toString());
        fileSystemStorageService.store(new MockMultipartFile("funko", "funko2.png",
                "image/png", bytesPNG), List.of("jpg", "jpeg", "png"), UUID.randomUUID().toString());
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @Test
    void testStore() throws IOException {
        var id = UUID.randomUUID().toString();
        String file = fileSystemStorageService.store(new MockMultipartFile("funko", "funko.png",
                "image/png", bytesPNG), List.of("jpg", "jpeg", "png"), id);

        assertAll(
                () -> assertNotNull(file),
                () -> assertTrue(file.contains(id + ".png"))
        );
    }

    @Test
    void testStoreWithEmptyFile() {
        var res = assertThrows(StorageBadRequest.class, () -> fileSystemStorageService.store(
                new MockMultipartFile("funko", "funko.png",
                        "", "".getBytes()),
                List.of("jpg", "jpeg", "png"), UUID.randomUUID().toString()));
        assertEquals("Fichero vacío funko.png", res.getMessage());
    }


    @Test
    void testStoreWith2puntos() {
        var res = assertThrows(StorageBadRequest.class, () -> fileSystemStorageService.store(
                new MockMultipartFile("funko", "../funko.png",
                        "image/png", bytesPNG),
                List.of("jpg", "jpeg", "png"), UUID.randomUUID().toString()));
        assertEquals("No se puede almacenar un fichero con una ruta relativa fuera del " +
                "directorio actual ../funko.png", res.getMessage());
    }

    @Test
    void testLoadAll() {
        var res = fileSystemStorageService.loadAll();
        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.toList().isEmpty())
        );
    }

    @Test
    void testLoad() {
        var res = fileSystemStorageService.load("funko.png");
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.getFileName().toString().contains("funko.png"))
        );
    }

    @Test
    void testLoadAsResource() throws IOException {
        var id = UUID.randomUUID().toString();
        var file = fileSystemStorageService.store(
                new MockMultipartFile("funko", "funko.png",
                        "image/png", bytesPNG),
                List.of("jpg", "jpeg", "png"), id);
        var res = fileSystemStorageService.loadAsResource(file);
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(Objects.requireNonNull(res.getFilename()).contains(id + ".png"))
        );
    }

    @Test
    void testLoadAsResoureNotFound() {
        var res = assertThrows(StorageNotFound.class, () -> fileSystemStorageService.loadAsResource("funko.png"));
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals("No se puede leer el fichero: funko.png", res.getMessage())
        );
    }

    @Test
    void testLoadAsResoureMalformedUrl() {
        var res = assertThrows(StorageNotFound.class, () -> fileSystemStorageService.loadAsResource("funko.png"));

        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals("No se puede leer el fichero: funko.png", res.getMessage())
        );
    }

    @Test
    void testDeleteAll() throws IOException {
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();
        var res = fileSystemStorageService.loadAll();

        assertAll(
                () -> assertTrue(res.toList().isEmpty())
        );
    }

    @Test
    void testDeleteAllInternalError() {
        fileSystemStorageService.deleteAll();
        var res = assertThrows(StorageInternal.class, fileSystemStorageService::loadAll);
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.getMessage().contains("Fallo al leer ficheros almacenados "))
        );
    }

    @Test
    void testInit() throws IOException {
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();
        var res = fileSystemStorageService.loadAll();
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.toList().isEmpty())
        );
    }

    @Test
    void testDelete() throws IOException {
        var file = fileSystemStorageService.store(
                new MockMultipartFile("funko", "funko.png",
                        "image/png", bytesPNG),
                List.of("jpg", "jpeg", "png"), UUID.randomUUID().toString());
        fileSystemStorageService.delete(file);
        var res = fileSystemStorageService.loadAll().toList();
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(2, res.size())
        );
    }

    @Test
    void testGetUrl() {
        String filename = "testFile.txt";
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String expectedUrl = baseUrl + "/storage/" + filename;
        String actualUrl = fileSystemStorageService.getUrl(filename);
        assertEquals(expectedUrl, actualUrl);
    }

}
