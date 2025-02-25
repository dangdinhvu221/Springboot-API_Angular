package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products") //http://localhost:8088/api/v1/products
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // upload 1 file anh và kích thước file
    //    MULTIPART_FORM_DATA_VALUE : chuyển thành từng phần dung lượng
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct
            (
                    @Valid @RequestBody ProductDTO productDTO,
                    BindingResult result
            ){
        try {
            if (result.hasErrors()){
//            dùng stream duyệt qua 1 danh sách để ánh sạ đến  nào đó và biến đổi k
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError:: getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages
            (
                    @PathVariable("id") Long id,
                    @RequestParam(name = "files") List<MultipartFile> files
            ){
        try{
            Product existingProduct = productService.getProductById(id);
            files = files == null ? new ArrayList<>() : files;
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images");
            }
            List<ProductImage> listProductImages = new ArrayList<>();
            for(MultipartFile file: files){
                if(file.getSize() == 0){
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if(file.getSize() > 10 *1024 *2024){ // Kich thuoc > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large! Maxium size is 10MB");
                }
                String contentType = file.getContentType();
                // check co phai dinh dang file anh ko getContentType
                if(!contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                // Lưu file và cập nhật thumnail trong DTO
                String filename = storeFile(file);
                // Lưu vào đối tượng trên product vào database
                // luu bang product images
                ProductImage productImage = productService.createProductImage(existingProduct.getId(),
                        ProductImageDTO
                                .builder()
                                .imageUrl(filename)
                                .build());
                // add vao mang
                listProductImages.add(productImage);
            }
            return ResponseEntity.ok().body(listProductImages);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null){
            throw new IOException("Invalid image format");
        }
        // Doi ten file goc de tranh trung lap
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn dến thư mục lưu file
        Path uploadDir = Paths.get("uploads");
        // Kiểm tra tạo thư mục nếu có tồn tại
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        // Sao chep file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    // Hien thi tat ca cac danh muc
    @GetMapping("") //http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        // Tao Pageable tu thong tin trang va gioi han
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);
        //Lay tong so trang
        int totalPages = productPage.getTotalPages();
        // list danh sach cac product
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long id
    ){
        try{
            Product existingProduct = productService.getProductById(id);
            // tái sử dụng hàm đã được thay đổi trong fromProduct
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(
            @PathVariable("id") Long id
    ){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    Fake du lieu Product
    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts(){
        Faker faker = new Faker();
        for(int i = 0; i < 100; i++){
            String ProductFakeName = faker.commerce().productName();
            if(productService.existsByName(ProductFakeName)){
                continue;
            }

            ProductDTO productDTO = ProductDTO
                    .builder()
                    .name(ProductFakeName)
                    .price((float)faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .categoryId((long)faker.number().numberBetween(1,3))
                    .build();
            try {
                productService.createProduct(productDTO);
            }catch (Exception e){
//                throw new RuntimeException(e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake Products created sucessfully");
    }

//    update a product
    @PutMapping("/id")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO
    ){
        try {
            Product updateProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(ProductResponse.fromProduct(updateProduct));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
