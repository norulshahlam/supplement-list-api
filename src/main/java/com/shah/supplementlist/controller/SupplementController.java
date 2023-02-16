package com.shah.supplementlist.controller;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.shah.supplementlist.model.Supplement;
import com.shah.supplementlist.model.SupplementCreate;
import com.shah.supplementlist.model.SupplementResponse;
import com.shah.supplementlist.model.SupplementUpdate;
import com.shah.supplementlist.service.SupplementService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@RestController
@CrossOrigin(origins = {"http://localhost:3000/", "https://supplement-list-ui.herokuapp.com"})
@Slf4j
public class SupplementController {

    private final SupplementService service;

    public SupplementController(SupplementService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<SupplementResponse<List<Supplement>>> uploadCsv(
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {

        log.info("in SupplementController::uploadCsv");
        SupplementResponse<List<Supplement>> response = service.uploadCsv(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/download")
    public void downloadCsv(HttpServletResponse response
    ) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {

        log.info("in SupplementController::downloadCsv");
        service.downloadCsv(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<SupplementResponse<List<Supplement>>> getAll() {

        log.info("in SupplementController::getAll");
        SupplementResponse<List<Supplement>> response = service.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<SupplementResponse<Supplement>> create(@RequestBody @Valid SupplementCreate supplement) {

        log.info("in SupplementController::create");
        SupplementResponse<Supplement> response = service.create(supplement);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PutMapping("/update")
    public ResponseEntity<SupplementResponse<Supplement>> update(@RequestBody @Valid SupplementUpdate supplement) {

        log.info("in SupplementController::update");
        SupplementResponse<Supplement> response = service.update(supplement);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SupplementResponse<UUID>> delete(@PathVariable UUID id) {

        log.info("in SupplementController::delete");
        SupplementResponse<UUID> response = service.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<SupplementResponse<String>> deleteMultiple(@RequestBody @Valid List<Supplement> supplements) {

        log.info("in SupplementController::deleteMultiple");
        SupplementResponse<String> response = service.deleteMultiple(supplements);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
