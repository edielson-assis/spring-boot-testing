package br.com.erudio.unitstests;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.services.PersonServices;

@Order(3)
@WebMvcTest
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonServices service;

    private Person person;

    private static final Long PERSON_ID = 1L;

    @BeforeEach
    void setup() {
        person = new Person("Edielson", "Assis", "edielson@email.com", "Rua dos sonhos, 1000", "Male");
    }

    @Test
    @DisplayName("JUnit test for Given person object when create person then return saved person")
    void testGivenPersonObject_whenCreatePerson_thenReturnSavedPerson() throws JsonProcessingException, Exception {

        // Given / Arrange
        given(service.create(any(Person.class))).willAnswer((invocation) -> invocation.getArgument(0));

        // When / Act
        ResultActions response = mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)));

        // Then / Assert
        response.andDo(print()).andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(person.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(person.getLastName())))
                .andExpect(jsonPath("$.email", is(person.getEmail())));
    }

    @Test
    @DisplayName("JUnit test for Given list of people when findAll then return people list")
    void testGivenListOfPeople_whenFindAllPeople_thenReturnPeopleList() throws JsonProcessingException, Exception {

        // Given / Arrange
        Person p1 = new Person("Carlos", "Oliveira", "carlos@email.com", "Rua dos sonhos, 1000", "Male");
        Person p2 = new Person("Rodrigo", "Carvalho", "rodrigo@email.com", "Rua dos doces, 0", "Male");

        List<Person> list = new ArrayList<>();
        list.addAll(Arrays.asList(p1, p2));

        given(service.findAll()).willReturn(list);

        // When / Act
        ResultActions response = mockMvc.perform(get("/person"));

        // Then / Assert
        response.andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.size()", is(list.size())));
    }

    @Test
    @DisplayName("JUnit test for Given personId object when findById then return person object")
    void testGivenPersonId_whenFindById_thenReturnPersonObject() throws JsonProcessingException, Exception {

        // Given / Arrange
        given(service.findById(PERSON_ID)).willReturn(person);

        // When / Act
        ResultActions response = mockMvc.perform(get("/person/{id}", PERSON_ID));

        // Then / Assert
        response.andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.firstName", is(person.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(person.getLastName())))
                .andExpect(jsonPath("$.email", is(person.getEmail())));
    }

    @Test
    @DisplayName("JUnit test for Given invalid personId when findById then return not Found")
    void testGivenInvalidPersonId_whenFindById_thenReturnNotFound() throws JsonProcessingException, Exception {

        // Given / Arrange
        given(service.findById(PERSON_ID)).willThrow(ResourceNotFoundException.class);

        // When / Act
        ResultActions response = mockMvc.perform(get("/person/{id}", PERSON_ID));

        // Then / Assert
        response.andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("JUnit test for Given update person object when update then return updated person object")
    void testGivenUpdatePerson_whenUpdate_thenReturnUpdatedPersonObject() throws JsonProcessingException, Exception {

        // Given / Arrange
        given(service.findById(PERSON_ID)).willReturn(person);
        given(service.update(any(Person.class))).willAnswer((invocation) -> invocation.getArgument(0));

        // When / Act
        Person updatedPerson = new Person("Rodrigo", "Carvalho", "rodrigo@email.com", "Rua dos doces, 0", "Male");

        ResultActions response = mockMvc.perform(put("/person").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPerson)));

        // Then / Assert
        response.andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.firstName", is(updatedPerson.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedPerson.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedPerson.getEmail())));
    }

    @Test
    @DisplayName("JUnit test for Given unexistent person when update then return not Found")
    void testGivenUnexistentPerson_whenUpdate_thenReturnNotFound() throws JsonProcessingException, Exception {

        // Given / Arrange
        given(service.findById(PERSON_ID)).willThrow(ResourceNotFoundException.class);
        given(service.update(any(Person.class))).willAnswer((invocation) -> invocation.getArgument(1));

        // When / Act
        Person updatedPerson = new Person("Rodrigo", "Carvalho", "rodrigo@email.com", "Rua dos doces, 0", "Male");

        ResultActions response = mockMvc.perform(put("/person").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPerson)));

        // Then / Assert
        response.andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("JUnit test for Given personId when delete then return noContent")
    void testGivenPersonId_whenDelete_thenReturnNoContent() throws JsonProcessingException, Exception {

        // Given / Arrange
        willDoNothing().given(service).delete(PERSON_ID);

        // When / Act
        ResultActions response = mockMvc.perform(delete("/person/{id}", PERSON_ID).contentType(MediaType.APPLICATION_JSON));

        // Then / Assert
        response.andExpect(status().isNoContent()).andDo(print());
    }
}