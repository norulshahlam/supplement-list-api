package com.shah.supplementlist.helper;

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

import static org.junit.jupiter.api.Assertions.*;

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
        Assertions.assertThrows(SupplementException.class, () -> helper.checkFileEmpty(null));
    }

    @Test
    void checkFileEmpty_EmptyFile() {
        multipartFile = new MockMultipartFile("file",
                file.getName(), "text/csv", (byte[]) null);
        Assertions.assertThrows(SupplementException.class, () -> helper.checkFileEmpty(multipartFile));
    }

    @Test
    void checkFileEmpty_InvalidCsvType() throws IOException {
        multipartFile = new MockMultipartFile("file",
                file.getName(), "text/test", input);
        Assertions.assertThrows(SupplementException.class, () -> helper.checkFileEmpty(multipartFile));
    }
}