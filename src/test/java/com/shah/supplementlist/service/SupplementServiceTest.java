package com.shah.supplementlist.service;

import com.opencsv.CSVReader;
import com.shah.supplementlist.exception.SupplementException;
import com.shah.supplementlist.model.Supplement;
import com.shah.supplementlist.model.SupplementCreate;
import com.shah.supplementlist.model.SupplementResponse;
import com.shah.supplementlist.model.SupplementUpdate;
import com.shah.supplementlist.repository.SupplementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.shah.supplementlist.model.ResponseStatus.SUCCESS;
import static com.shah.supplementlist.service.SupplementService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplementServiceTest {

    @Mock
    private SupplementRepository repository;

    @InjectMocks
    private SupplementService service;

    private MultipartFile mockCsvFile;
    private MultipartFile mockTextFile;
    SupplementException exception;

    private SupplementUpdate supplementUpdate;
    private Supplement supplement;
    private SupplementCreate supplementCreate;
    SupplementResponse supplementResponse;

    @BeforeEach
    void setUp() throws IOException {

        supplement = Supplement.builder()
                .productId(UUID.randomUUID())
                .productName("BOLDENONE 250MG/ML")
                .alias("Equipoise")
                .type("INJECTABLE")
                .brand("MEDITECH")
                .price(new BigDecimal(70.00))
                .dosage("250mg/ml")
                .quantity("10 ml")
                .packaging("vial")
                .remarks("AKA Eq")
                .build();

        supplementCreate = SupplementCreate.builder()
                .price("70.00")
                .build();

        supplementUpdate = SupplementUpdate.builder()
                .productId(UUID.randomUUID()).build();

        mockCsvFile = new MockMultipartFile("src/test/resources/data.csv",
                "src/test/resources/data.csv",
                "text/csv",
                "This is a dummy file content".getBytes(StandardCharsets.UTF_8));

        mockTextFile = new MockMultipartFile("src/test/resources/data.csv",
                "src/test/resources/data.csv",
                "text/plain",
                "This is a dummy file content".getBytes(StandardCharsets.UTF_8));

        lenient().when(repository.saveAll(any())).thenReturn(Arrays.asList(supplement, supplement, supplement));
    }

    @Test
    void uploadRealCsvSuccess() throws IOException {
        CSVReader scanner = new CSVReader(new FileReader("src/test/resources/data.csv"));
        supplementResponse = service.uploadCsv(mockCsvFile);
        System.out.println(supplementResponse);
    }

    @RepeatedTest(5)
    void uploadCsvSuccess() throws IOException {
        supplementResponse = service.uploadCsv(mockCsvFile);
        assertThat(supplementResponse).isNotNull();
        assertThat(supplementResponse.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void uploadCsvInvalidCsvType() {
        exception = assertThrows(
                SupplementException.class, () -> service.uploadCsv(mockTextFile));
        assertThat(exception.getErrorMessage())
                .isEqualTo(INVALID_CSV_FILE);
    }

    @Test
    void uploadCsvEmptyFile() throws IOException {

        mockCsvFile = new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        exception = assertThrows(
                SupplementException.class, () -> service.uploadCsv(mockCsvFile));
        assertThat(exception.getErrorMessage()).isEqualTo(EMPTY_CSV_FILE);
    }

    @Test
    void getAllSuccess() {

        when(repository.findAll()).thenReturn(List.of(supplement));
        supplementResponse = service.getAll();
        assertThat(supplementResponse.getStatus()).isEqualTo(SUCCESS);
        List<Supplement> data = (List<Supplement>) supplementResponse.getData();
        assertThat(data.size()).isEqualTo(1);

    }

    @Test
    void getAllEmptyList() {

        when(repository.findAll()).thenReturn(new ArrayList<>());
        exception = assertThrows(
                SupplementException.class, () -> service.getAll());
        assertThat(exception.getErrorMessage()).isEqualTo(SUPPLEMENT_NOT_FOUND);
    }

    @Test
    void updateSuccess() {
        Supplement supplementToBeUpdated = Supplement.builder().productId(UUID.randomUUID()).brand("change me").build();
        when(repository.findById(any())).thenReturn(Optional.of(supplement));
        when(repository.save(any())).thenReturn(supplementToBeUpdated);
        supplementResponse = service.update(supplementUpdate);
        assertThat(supplementResponse.getStatus()).isEqualTo(SUCCESS);

    }

    @Test
    void updateFail() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        exception = assertThrows(
                SupplementException.class, () -> service.update(supplementUpdate));
        assertThat(exception.getErrorMessage()).isEqualTo(SUPPLEMENT_NOT_FOUND);
        assertThat(exception.getClass()).isEqualTo(SupplementException.class);
    }

    @Test
    void deleteSuccess() {
        when(repository.findById(any())).thenReturn(Optional.of(supplement));
        supplementResponse = service.delete(UUID.randomUUID());
        assertThat(supplementResponse.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void deleteFail() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        exception = assertThrows(
                SupplementException.class, () -> service.delete(supplement.getProductId()));
        assertThat(exception.getErrorMessage()).isEqualTo(SUPPLEMENT_NOT_FOUND);
        assertThat(exception.getClass()).isEqualTo(SupplementException.class);
    }

    @Test
    void deleteMultipleSuccess() {
        when(repository.findAllById(any())).thenReturn(List.of(supplement, supplement));
        supplementResponse = service.deleteMultiple(List.of(supplement));
        assertThat(supplementResponse.getStatus()).isEqualTo(SUCCESS);
        assertThat(repository.findAllById(any())).hasSizeGreaterThan(0);
    }

    @Test
    void deleteMultipleFail() {
        when(repository.findAllById(any())).thenReturn(List.of());
        exception = assertThrows(
                SupplementException.class, () -> service.deleteMultiple(List.of(supplement)));
        assertThat(exception.getErrorMessage()).isEqualTo(SUPPLEMENT_NOT_FOUND);
        assertThat(repository.findAllById(any())).hasSize(0);
    }

    @Test
    void createSuccess() {
        when(repository.save(any())).thenReturn(supplement);
        supplementResponse = service.create(supplementCreate);
        assertThat(supplementResponse.getStatus()).isEqualTo(SUCCESS);

    }
}