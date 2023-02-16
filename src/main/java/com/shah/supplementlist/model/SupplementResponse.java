package com.shah.supplementlist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author NORUL
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplementResponse<T> {

    @NonNull
    ResponseStatus status;
    T data;
    String errorMessage;

    public static <T> SupplementResponse<T> success(T data) {
        return new SupplementResponse<>(ResponseStatus.SUCCESS, data, null);
    }
}
