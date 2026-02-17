package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;

    private static final String REDIRECT_LIST = "redirect:list";
    private Model model;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    void testCreateProductPage() {
        String viewName = productController.createProductPage(model);

        assertEquals("CreateProduct", viewName);
        assertTrue(model.containsAttribute("product"));
        assertInstanceOf(Product.class, model.getAttribute("product"));
    }

    @Test
    void testCreateProductPost() {
        Product product = new Product();
        String viewName = productController.createProductPost(product, model);

        assertEquals(REDIRECT_LIST, viewName);
    }

    @Test
    void testProductListPage() {
        when(productService.findAll()).thenReturn(mock(List.class));
        String viewName = productController.productListPage(model);

        assertEquals("ProductList", viewName);
        assertTrue(model.containsAttribute("products"));
    }

    @Test
    void testEditProductPage() {
        String productId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        Product product = new Product();
        product.setProductId(productId);
        when(productService.findById(productId)).thenReturn(product);
        String viewName = productController.editProductPage(productId, model);

        assertEquals("EditProduct", viewName);
        assertEquals(product, model.getAttribute("product"));
    }

    @Test
    void testEditProductPost() {
        Product product = new Product();
        String viewName = productController.editProductPost(product, model);

        assertEquals(REDIRECT_LIST, viewName);
    }

    @Test
    void testDeleteProduct() {
        String productId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        String viewName = productController.deleteProduct(productId);

        assertEquals(REDIRECT_LIST, viewName);
    }
}