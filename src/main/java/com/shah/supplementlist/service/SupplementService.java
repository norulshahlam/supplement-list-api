package com.shah.supplementlist.service;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.shah.supplementlist.exception.SupplementException;
import com.shah.supplementlist.model.*;
import com.shah.supplementlist.repository.SupplementRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.shah.supplementlist.helper.CsvHelper.csvExporter;
import static com.shah.supplementlist.helper.CsvHelper.csvParser;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Slf4j
public class SupplementService {

    public static final String SUPPLEMENT_NOT_FOUND = "No supplement found";
    public static final String INVALID_CSV_FILE = "Invalid CSV type. Please upload valid CSV file";
    public static final String EMPTY_CSV_FILE = "Empty file uploaded. Please upload CSV file";
    private final SupplementRepository repository;

    public SupplementService(SupplementRepository repository) {
        this.repository = repository;
    }

    public SupplementResponse uploadCsv(MultipartFile file) throws IOException {
        log.info("in SupplementService::uploadCsv");

        if (ObjectUtils.anyNull(file)) {
            log.error("No file uploaded");
            throw new SupplementException("No file uploaded. Please upload file");
        } else if (file.isEmpty()) {
            log.error("Empty file");
            throw new SupplementException("Empty file uploaded. Please upload CSV file");
        } else if (!Objects.requireNonNull(file.getContentType()).equals("text/csv")) {
            log.error("Invalid csv type");
            throw new SupplementException(INVALID_CSV_FILE);
        }

        List<Supplement> supplementList = csvParser(file);

        // delete all data into db else will contain duplicates
        log.info("Resetting DB for new batch of list");
        repository.deleteAllInBatch();

        // save into db
        List<Supplement> savedSupplements = repository.saveAll(supplementList);
        log.info("{} supplements saved", savedSupplements.size());

        return SupplementResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(savedSupplements)
                .build();
    }

    public void downloadCsv(HttpServletResponse response) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("in SupplementService::downloadCsv");
        csvExporter(response);
    }

    public SupplementResponse getAll() {
        log.info("in SupplementService::getAll");
        List<Supplement> allSupplements = repository.findAll();

        if (allSupplements.isEmpty()) {
            throw new SupplementException(SUPPLEMENT_NOT_FOUND);
        }
        return SupplementResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(allSupplements)
                .build();
    }

    public SupplementResponse update(SupplementUpdate supplement) {
        log.info("in SupplementService::update");
        // Fetch existing supplement
        Optional<Supplement> item = repository.findById(supplement.getProductId());
        if (item.isEmpty())
            throw new SupplementException(SUPPLEMENT_NOT_FOUND);

        Supplement fetchedSupplement = item.get();
        copyProperties(supplement, fetchedSupplement);
        if (ObjectUtils.isNotEmpty(supplement.getPrice()))
            fetchedSupplement.setPrice(new BigDecimal(supplement.getPrice()));
        Supplement savedSupplement = repository.save(fetchedSupplement);

        log.info("Supplement update success: {}", savedSupplement);

        return SupplementResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(savedSupplement)
                .build();
    }

    public SupplementResponse delete(UUID id) {
        log.info("in SupplementService::delete");

        // Check if existing supplement exists
        Optional<Supplement> item = repository.findById(id);
        if (item.isEmpty())
            throw new SupplementException(SUPPLEMENT_NOT_FOUND);

        repository.deleteById(id);
        log.info("Supplement with id: {} delete success!", id);

        return SupplementResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(id)
                .build();
    }

    public SupplementResponse create(SupplementCreate supplement) {

        Supplement entity = new Supplement();
        copyProperties(supplement, entity);
        if (ObjectUtils.isNotEmpty(supplement.getPrice()))
            entity.setPrice(new BigDecimal(supplement.getPrice()));
        Supplement savedSupplement = repository.save(entity);

        return SupplementResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(savedSupplement)
                .build();
    }

    public SupplementResponse deleteMultiple(List<Supplement> supplements) {

        // find supplement if exists
        Iterable<UUID> ids = supplements.stream().map(Supplement::getProductId).collect(Collectors.toList());
        List<Supplement> supplementList = repository.findAllById(ids);

        if (supplementList.isEmpty()) {
            throw new SupplementException(SUPPLEMENT_NOT_FOUND);
        }

        repository.deleteAllInBatch(supplementList);
        return SupplementResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .data(supplementList.size() + " supplement delete success out of " + supplements.size())
                .build();
    }
}
