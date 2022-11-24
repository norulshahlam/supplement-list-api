package com.shah.supplementlist.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplementException extends RuntimeException {

    private final String errorMessage;
}
