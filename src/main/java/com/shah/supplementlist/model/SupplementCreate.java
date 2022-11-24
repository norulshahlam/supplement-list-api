package com.shah.supplementlist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupplementCreate {

    @NotBlank(message = "productName cannot be empty")
    @Size(min = 3, message = "productName must be more than 3 character!")
    private String productName;
    private String alias;
    private String type;
    private String brand;
    @NotBlank(message = "Price cannot be blank")
    @Digits(integer = 8, fraction = 2, message = "price only up to 6 digits and in 2 decimal points!")
    private String price;
    private String dosage;
    private String quantity;
    private String packaging;
    private String available;
    private String remarks;
}
