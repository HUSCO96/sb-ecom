package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptiona.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.repositories.CategoryRepository;
import com.ecommerce.sb_ecom.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;


    @Override
    public ProductDTO addProduct(Product product, Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);




    }

    @Override
    public ProductResponse getAllProducts() {

        List<Product> productList= productRepository.findAll();

        List<ProductDTO> productResponseList = productList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productResponseList);

        return productResponse;


    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        List<Product> productListByCat = productRepository.findByCategoryOrderByPrice(category);
        List<ProductDTO> productDTOList = productListByCat.stream().map(Product -> modelMapper.map(Product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeword(String keyword) {

        List<Product> productListByKeyword = productRepository.findByProductNameContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOList = productListByKeyword.stream().map(Product -> modelMapper.map(Product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO product, Long productId) {

        Product productDb = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId ));

        Product productUpdate = modelMapper.map(product, Product.class);

        productDb.setProductName(productUpdate.getProductName());
        productDb.setDescription(product.getDescription());
        productDb.setQuantiy(product.getQuantiy());
        productDb.setPrice(product.getPrice());
        productDb.setDiscount(product.getDiscount());
        productDb.setSpecialPrice(product.getSpecialPrice());

        Product productSaved = productRepository.save(productDb);

        return modelMapper.map(productSaved, ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productDb = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId ));

        productRepository.deleteById(productId);

        return modelMapper.map(productDb, ProductDTO.class);


    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product productFromDb = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId ));

        String fileName = fileService.uploadImage(path, image);

        productFromDb.setImage(fileName);

        Product updatedProduct = productRepository.save(productFromDb);

        return modelMapper.map(productFromDb, ProductDTO.class);


    }






}
