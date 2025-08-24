package com.ecommerce.sb_ecom.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    @NotBlank
    private String productName;
    private String Image;
    @NotBlank
    @Size(min = 6, message = "Product description must at least have 3 characters")
    private String description;
    private Integer quantiy;
    private double price;
    private double discount;
    private double specialPrice;
}
