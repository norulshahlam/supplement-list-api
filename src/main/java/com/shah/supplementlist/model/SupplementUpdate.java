package com.shah.supplementlist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupplementUpdate extends SupplementCreate {
    @NotNull(message = "productId cannot be empty")
    private UUID productId;

}
