package gift;

import gift.domain.Product;
import gift.dto.CreateProductDto;
import gift.dto.ProductDto;
import gift.dto.UpdateProductDto;
import gift.exception.NotFoundException;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerApiTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testCreateProduct() {
        // given
        String name = "테스트 상품";
        Integer price = 1000;
        String description = "테스트 상품 설명";
        String imageUrl = "http://test.com";

        CreateProductDto createProductDto = new CreateProductDto(name, price, description, imageUrl);
        String url = "http://localhost:"+port+"/products";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, createProductDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Long id = responseEntity.getBody();

        Product product = productRepository.getProductById(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getUrl()).isEqualTo(imageUrl);

        productRepository.deleteProductById(id);
    }

    @Test
    void testGetProduct(){
        // given
        String name = "테스트 상품";
        Integer price = 1000;
        String description = "테스트 상품 설명";
        String imageUrl = "http://test.com";

        CreateProductDto createProductDto = new CreateProductDto(name, price, description, imageUrl);
        String url = "http://localhost:"+port+"/products";
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, createProductDto, Long.class);
        Long id = responseEntity.getBody();

        // when
        String getUrl = "http://localhost:"+port+"/products?id="+id;
        ResponseEntity<ProductDto> getResponseEntity = restTemplate.getForEntity(getUrl, ProductDto.class);

        // then
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).isNotNull();
        ProductDto productDto = getResponseEntity.getBody();
        assertThat(productDto.name()).isEqualTo(name);
        assertThat(productDto.price()).isEqualTo(price);
        assertThat(productDto.description()).isEqualTo(description);
        assertThat(productDto.imageUrl()).isEqualTo(imageUrl);

        productRepository.deleteProductById(id);
    }

    @Test
    void testUpdateProduct(){
        // given
        String name = "테스트 상품";
        Integer price = 1000;
        String description = "테스트 상품 설명";
        String imageUrl = "http://test.com";

        CreateProductDto createProductDto = new CreateProductDto(name, price, description, imageUrl);
        String url = "http://localhost:"+port+"/products";
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, createProductDto, Long.class);
        Long id = responseEntity.getBody();

        String updatedName = "수정된 상품";
        Integer updatedPrice = 2000;
        String updatedDescription = "수정된 상품 설명";
        String updatedImageUrl = "http://updated.com";

        UpdateProductDto updateProductDto = new UpdateProductDto(id, updatedName, updatedPrice, updatedDescription, updatedImageUrl);
        String updateUrl = "http://localhost:"+port+"/products?id="+id;

        HttpEntity<UpdateProductDto> requestEntity = new HttpEntity<>(updateProductDto);

        // when
        ResponseEntity<Long> updateResponseEntity = restTemplate.exchange(updateUrl, HttpMethod.PUT, requestEntity, Long.class);


        // then
        assertThat(updateResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponseEntity.getBody()).isNotNull();
        Long updatedId = updateResponseEntity.getBody();

        Product product = productRepository.getProductById(updatedId);
        assertThat(product.getName()).isEqualTo(updatedName);
        assertThat(product.getPrice()).isEqualTo(updatedPrice);
        assertThat(product.getDescription()).isEqualTo(updatedDescription);
        assertThat(product.getUrl()).isEqualTo(updatedImageUrl);

        productRepository.deleteProductById(updatedId);
    }

    @Test
    void testDeleteProduct() {
        // given
        String name = "테스트 상품";
        Integer price = 1000;
        String description = "테스트 상품 설명";
        String imageUrl = "http://test.com";

        CreateProductDto createProductDto = new CreateProductDto(name, price, description, imageUrl);
        String url = "http://localhost:" + port + "/products";
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, createProductDto, Long.class);
        Long id = responseEntity.getBody();

        String deleteUrl = "http://localhost:" + port + "/products?id=" + id;

        // when
        restTemplate.delete(deleteUrl);

        // then
        assertThrows(NotFoundException.class, () -> productRepository.getProductById(id));
    }
}