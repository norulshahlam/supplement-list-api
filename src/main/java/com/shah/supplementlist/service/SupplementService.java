package com.shah.supplementlist.service;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.shah.supplementlist.exception.SupplementException;
import com.shah.supplementlist.helper.SupplementHelper;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.shah.supplementlist.helper.CsvHelper.csvExporter;
import static com.shah.supplementlist.helper.CsvHelper.csvParser;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Slf4j
public class SupplementService {

    public static final String SUPPLEMENT_NOT_FOUND = "No supplement found";
    private final SupplementRepository repository;

    public SupplementService(SupplementRepository repository) {
        this.repository = repository;
    }

    public SupplementResponse<List<Supplement>> uploadCsv(MultipartFile file) throws IOException {
        log.info("in SupplementService::uploadCsv");

        SupplementHelper.checkFileEmpty(file);

        List<Supplement> supplementList = csvParser(file);

        // delete all data into db else will contain duplicates
        log.info("Resetting DB for new batch of list");
        repository.deleteAllInBatch();

        // save into db
        List<Supplement> savedSupplements = repository.saveAll(supplementList);
        log.info("{} supplements saved", savedSupplements.size());

        return SupplementResponse.success(savedSupplements);
    }

    public void downloadCsv(HttpServletResponse response) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("in SupplementService::downloadCsv");
        csvExporter(response);
    }

    public SupplementResponse<List<Supplement>> getAll() {
        log.info("in SupplementService::getAll");
        List<Supplement> allSupplements = repository.findAll();

        if (allSupplements.isEmpty()) {
            throw new SupplementException(SUPPLEMENT_NOT_FOUND);
        }
        return SupplementResponse.success(allSupplements);
    }

    public SupplementResponse<Supplement> create(SupplementCreate supplement) {

        Supplement entity = new Supplement();
        copyProperties(supplement, entity);
        if (ObjectUtils.isNotEmpty(supplement.getPrice()))
            entity.setPrice(new BigDecimal(supplement.getPrice()));
        Supplement savedSupplement = repository.save(entity);

        return SupplementResponse.success(savedSupplement);
    }

    public SupplementResponse<Supplement> update(SupplementUpdate supplement) {
        log.info("in SupplementService::update");
        // Fetch existing supplement
        Supplement item = repository.findById(supplement.getProductId()).orElseThrow(() -> new SupplementException(SUPPLEMENT_NOT_FOUND));

        copyProperties(supplement, item);
        if (ObjectUtils.isNotEmpty(supplement.getPrice()))
            item.setPrice(new BigDecimal(supplement.getPrice()));
        Supplement savedSupplement = repository.save(item);

        log.info("Supplement update success: {}", savedSupplement);

        return SupplementResponse.success(savedSupplement);
    }

    public SupplementResponse<UUID> deleteById(UUID id) {
        log.info("in SupplementService::delete");

        // Check if existing supplement exists
        Supplement supplement = repository.findById(id).orElseThrow(() -> new SupplementException(SUPPLEMENT_NOT_FOUND));

        repository.deleteById(supplement.getProductId());
        log.info("Supplement with id: {} delete success!", id);

        return SupplementResponse.success(id);
    }


    public SupplementResponse<String> deleteMultiple(List<Supplement> supplements) {

        // find supplement if exists
        Iterable<UUID> ids = supplements.stream().map(Supplement::getProductId).collect(Collectors.toList());
        List<Supplement> supplementList = repository.findAllById(ids);

        if (supplementList.isEmpty()) {
            throw new SupplementException(SUPPLEMENT_NOT_FOUND);
        }

        repository.deleteAllInBatch(supplementList);
        return SupplementResponse.success(supplementList.size() + " supplement delete success out of " + supplements.size());
    }
}
