package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Spy
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("12345");
        product.setProductName("Barang Test");
        product.setProductQuantity(10);
    }

    @Test
    void testCreateAndFind() {
        productService.create(product);

        List<Product> allProducts = productService.findAll();
        assertFalse(allProducts.isEmpty());

        Product foundProduct = productService.findById("12345");
        assertEquals("Barang Test", foundProduct.getProductName());
    }

    @Test
    void testUpdate() {
        productService.create(product);

        Product editProduct = new Product();
        editProduct.setProductId("12345");
        editProduct.setProductName("Barang Edit");
        editProduct.setProductQuantity(20);

        productService.update(editProduct);

        Product result = productService.findById("12345");
        assertEquals("Barang Edit", result.getProductName());
        assertEquals(20, result.getProductQuantity());
    }

    @Test
    void testDelete() {
        productService.create(product);
        productService.delete("12345");

        List<Product> allProducts = productService.findAll();
        assertTrue(allProducts.isEmpty());
    }
}