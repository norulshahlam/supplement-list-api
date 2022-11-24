package com.shah.supplementlist.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplement {
    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type="org.hibernate.type.UUIDCharType")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID productId;
    @CsvBindByName
    private String brand;
    @CsvBindByName
    private String productName;
    @CsvBindByName
    private String alias;
    @CsvBindByName
    private String type;
    @CsvBindByName
    private BigDecimal price;
    @CsvBindByName
    private String dosage;
    @CsvBindByName
    private String quantity;
    @CsvBindByName
    private String packaging;
    @CsvBindByName
    private String available;
    @CsvBindByName
    private String remarks;
}
