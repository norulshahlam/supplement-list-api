package com.shah.supplementlist.util;

import com.shah.supplementlist.exception.SupplementException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
/**
 * @author NORUL
 */
@Slf4j
public class SupplementHelper {

    public static final String INVALID_CSV_FILE = "Invalid CSV type. Please upload valid CSV file";
    public static final String EMPTY_CSV_FILE = "Empty file uploaded. Please upload CSV file";
    public static final String NO_FILE_UPLOADED = "No file uploaded. Please upload file";
    public static final String TEXT_CSV = "text/csv";


    public static void checkFileEmpty(MultipartFile file) {
        if (ObjectUtils.anyNull(file)) {
            log.error("No file uploaded");
            throw new SupplementException(NO_FILE_UPLOADED);
        } else if (file.isEmpty()) {
            log.error("Empty file");
            throw new SupplementException(EMPTY_CSV_FILE);
        } else if (!Objects.requireNonNull(file.getContentType()).equals(TEXT_CSV)) {
            log.error("Invalid csv type");
            throw new SupplementException(INVALID_CSV_FILE);
        }
    }
}
