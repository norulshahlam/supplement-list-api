package com.shah.supplementlist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
}
