package com.shah.supplementlist.util;

import com.shah.supplementlist.exception.SupplementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class SupplementHelperTest {

    private SupplementHelper helper;

    private MultipartFile multipartFile;
    private File file;
    private FileInputStream input;

    @BeforeEach
    void setUp() throws IOException {
        file = new File("src/test/resources/data.csv");
        input = new FileInputStream(file);
    }

    @Test
    void checkFileEmpty_NoFileUploaded() {
        Assertions.assertThrows(SupplementException.class, () -> SupplementHelper.checkFileEmpty(null));
    }

    @Test
    void checkFileEmpty_EmptyFile() {
        multipartFile = new MockMultipartFile("file",
                file.getName(), "text/csv", (byte[]) null);
        Assertions.assertThrows(SupplementException.class, () -> SupplementHelper.checkFileEmpty(multipartFile));
    }

    @Test
    void checkFileEmpty_InvalidCsvType() throws IOException {
        multipartFile = new MockMultipartFile("file",
                file.getName(), "text/test", input);
        Assertions.assertThrows(SupplementException.class, () -> SupplementHelper.checkFileEmpty(multipartFile));
    }
}