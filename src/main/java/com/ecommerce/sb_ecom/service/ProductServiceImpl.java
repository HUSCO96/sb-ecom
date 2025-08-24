package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptiona.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.repositories.CategoryRepository;
import com.ecommerce.sb_ecom.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ProductDTO addProduct(@Valid ProductDTO product, Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product addProduct = modelMapper.map(product, Product.class);
        addProduct.setImage("default.png");
        addProduct.setCategory(category);
        double specialPrice = addProduct.getPrice() - ((addProduct.getDiscount() * 0.01) * addProduct.getPrice());
        addProduct.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(addProduct);

        return modelMapper.map(savedProduct, ProductDTO.class);




    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortById, String sortOrder) {

        Sort sortOrderBy = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortById).ascending() : Sort.by(sortById).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortOrderBy);

        Page<Product> productList = productRepository.findAll(pageable);

        List<ProductDTO> productResponseList = productList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productResponseList);
        productResponse.setPageNumber(productList.getNumber());
        productResponse.setPageSize(productList.getSize());
        productResponse.setTotalElements(productList.getTotalElements());
        productResponse.setTotalPages(productList.getTotalPages());
        productResponse.setLastPage(productList.isLast());
        return productResponse;


    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortById, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Sort sortOrderBy = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortById).ascending() : Sort.by(sortById).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortOrderBy);

        Page<Product> productList = productRepository.findAll(pageable);

        List<Product> productListByCat = productRepository.findByCategoryOrderByPrice(category);
        List<ProductDTO> productDTOList = productListByCat.stream().map(Product -> modelMapper.map(Product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(productList.getNumber());
        productResponse.setPageSize(productList.getSize());
        productResponse.setTotalElements(productList.getTotalElements());
        productResponse.setTotalPages(productList.getTotalPages());
        productResponse.setLastPage(productList.isLast());
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
