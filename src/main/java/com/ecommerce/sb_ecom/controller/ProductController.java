package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.config.AppConfig;
import com.ecommerce.sb_ecom.config.AppConstants;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.service.CategoryService;
import com.ecommerce.sb_ecom.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;



    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO product, @PathVariable Long categoryId){

        ProductDTO productDto = productService.addProduct(product, categoryId);

        return new ResponseEntity<>(productDto, HttpStatus.CREATED);

    }


    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                          @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                          @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortById,
                                                          @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortOrder){

        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortById, sortOrder);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }


    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                 @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                                 @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortById,
                                                                 @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortOrder){

        ProductResponse productsByCategory = productService.searchByCategory(categoryId,pageNumber, pageSize, sortById, sortOrder);

        return new ResponseEntity<>(productsByCategory,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")

    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword){

        ProductResponse productsByKeyword = productService.searchProductByKeword(keyword);

        return new ResponseEntity<>(productsByKeyword,HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productsId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO product, @PathVariable Long productId){

        ProductDTO productdto = productService.updateProduct(product, productId);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")

    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {

        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
