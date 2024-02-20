package br.com.erudio.integrationstests.controller;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.config.TestConfig;
import br.com.erudio.integrationstests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@Order(5)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PersonControllerIntegrationTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static Person person;
    private static Person person1;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/person")
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        person = new Person("Edielson", "Assis", "edielson@email.com", "Rua dos sonhos, 1000", "Male");
        person1 = new Person("Rodrigo", "Carvalho", "rodrigo@email.com", "Rua dos doces, 0", "Male");
    }

    @Test
    @Order(1)
    @DisplayName("JUnit integration test given person object when create one person should return person object")
    void integrationTestGivenPersonObject_when_createOnePerson_shouldReturnPersonObject()
            throws JsonMappingException, JsonProcessingException {

        var content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();        

                given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(person1)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        Person createdPerson = objectMapper.readValue(content, Person.class);
        person = createdPerson;

        assertNotNull(createdPerson);
        assertTrue(createdPerson.getId() > 0);
        assertEquals("Edielson", createdPerson.getFirstName());
        assertEquals("Assis", createdPerson.getLastName());
        assertEquals("Male", createdPerson.getGender());
        assertEquals("Rua dos sonhos, 1000", createdPerson.getAddress());
        assertEquals("edielson@email.com", createdPerson.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("JUnit integration test given update person object when create one person should return updated person object")
    void integrationTestGivenPersonObject_when_updateOnePerson_shouldReturnUpdatedPersonObject()
            throws JsonMappingException, JsonProcessingException {

        person.setFirstName("Ivanice");
        person.setEmail("ivanice@email.com");
        person.setGender("Female");
        
        var content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(person)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        Person updatedPerson = objectMapper.readValue(content, Person.class);
        person = updatedPerson;

        assertNotNull(updatedPerson);
        assertTrue(updatedPerson.getId() > 0);
        assertEquals("Ivanice", updatedPerson.getFirstName());
        assertEquals("Assis", updatedPerson.getLastName());
        assertEquals("Female", updatedPerson.getGender());
        assertEquals("Rua dos sonhos, 1000", updatedPerson.getAddress());
        assertEquals("ivanice@email.com", updatedPerson.getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("JUnit integration test given update person object when findById should return person object")
    void integrationTestGivenPersonObject_when_findById_shouldReturnPersonObject()
            throws JsonMappingException, JsonProcessingException {
        
        var content = given().spec(specification)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        Person foundPerson = objectMapper.readValue(content, Person.class);

        assertNotNull(foundPerson);
        assertTrue(foundPerson.getId() > 0);
        assertEquals("Ivanice", foundPerson.getFirstName());
        assertEquals("Assis", foundPerson.getLastName());
        assertEquals("Female", foundPerson.getGender());
        assertEquals("Rua dos sonhos, 1000", foundPerson.getAddress());
        assertEquals("ivanice@email.com", foundPerson.getEmail());
    }

    @Test
    @Order(4)
    @DisplayName("JUnit integration test when findAll should return people list")
    void integrationTest_when_findAll_shouldReturnPeopleList()
            throws JsonMappingException, JsonProcessingException {
        
        var content = given().spec(specification)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        List<Person> list = Arrays.asList(objectMapper.readValue(content, Person[].class));

        Person foundPersonOne = list.get(0);

        assertNotNull(foundPersonOne);
        assertEquals(2, list.size());

        assertTrue(foundPersonOne.getId() > 0);
        assertEquals("Ivanice", foundPersonOne.getFirstName());
        assertEquals("Assis", foundPersonOne.getLastName());
        assertEquals("Female", foundPersonOne.getGender());
        assertEquals("Rua dos sonhos, 1000", foundPersonOne.getAddress());
        assertEquals("ivanice@email.com", foundPersonOne.getEmail());
        
        Person foundPersonTwo = list.get(1);

        assertNotNull(foundPersonTwo);
        assertEquals(2, list.size());

        assertTrue(foundPersonTwo.getId() > 0);
        assertEquals("Rodrigo", foundPersonTwo.getFirstName());
        assertEquals("Carvalho", foundPersonTwo.getLastName());
        assertEquals("Male", foundPersonTwo.getGender());
        assertEquals("Rua dos doces, 0", foundPersonTwo.getAddress());
        assertEquals("rodrigo@email.com", foundPersonTwo.getEmail());
    }

    @Test
    @Order(5)
    @DisplayName("JUnit integration test given person object when delete should return noContent")
    void integrationTestGivenPersonObject_when_delete_shouldReturnNoContent()
            throws JsonMappingException, JsonProcessingException {
        
        given().spec(specification)
        .pathParam("id", person.getId())
        .when()
        .delete("{id}")
        .then()
        .statusCode(204);
    }
}