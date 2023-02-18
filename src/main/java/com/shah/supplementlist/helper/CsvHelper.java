package com.shah.supplementlist.helper;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.shah.supplementlist.model.Supplement;
import com.shah.supplementlist.repository.SupplementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
@Slf4j
public class CsvHelper {

    private static SupplementRepository repository;

    public CsvHelper(SupplementRepository repository) {
        CsvHelper.repository = repository;
    }

    public static List<Supplement> csvParser(MultipartFile file) throws IOException {
        log.info("inside CsvHelper::csvParser");

        // parse CSV file to create a list of `Supplement` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        // create csv bean reader
        List<Supplement> supplementList = new CsvToBeanBuilder<Supplement>(reader)
                .withType(Supplement.class)
                .withIgnoreQuotations(true)
                .withThrowExceptions(true)
                .build()
                .parse();
        reader.close();
        return supplementList;
    }

    public static void csvExporter(HttpServletResponse response) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("inside CsvHelper::csvExporter");

        // set file name and content type
        String filename = "data1.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        log.info("create a csv writer...");
        StatefulBeanToCsv<Supplement> writer =
                new StatefulBeanToCsvBuilder<Supplement>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false).build();

        log.info("write data into csv");
        writer.write(repository.findAll());

    }
}
