package br.com.erudio.unitstests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;
import br.com.erudio.services.PersonServices;

@Order(2)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {
    
    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonServices service;

    private Person person;

    @BeforeEach
	void setup() {
		person = new Person("Edielson", "Assis", "edielson@email.com", "Rua dos sonhos, 1000", "Male");
	}

    @Test
    @DisplayName("JUnit test for Given Person object when save person then return saved person")
    void testGivenPersonObject_whenSavePerson_thenReturnSavedPerson() {

        // Given / Arrenge
        given(repository.findByEmail(anyString())).willReturn(Optional.empty()); // retorna um Optional vazio, garantindo que o email nao existe na base de dados
        given(repository.save(person)).willReturn(person);

        // When / Act
        Person savedPerson = service.create(person);

        // Then / Assert
        assertNotNull(savedPerson);
        assertEquals("Edielson", savedPerson.getFirstName());
    }
    
    @Test
    @DisplayName("JUnit test for Given existing email when throws exception")
    void testGivenExistingEmail_whenSavePerson_thenThrowsException() {

        // Given / Arrenge
        given(repository.findByEmail(anyString())).willReturn(Optional.of(person)); // retorna um Optional com o objeto person, indicando que o email ja existe na base de dados

        // When / Act
        assertThrows(RuntimeException.class, () -> {
            service.create(person);
        });

        // Then / Assert
        verify(repository, never()).save(any(Person.class)); // garante que o servico nao seja chamado, caso o email ja exista
    }
    
    @Test
    @DisplayName("JUnit test for Given when findAll then return people list")
    void testGivenPeopleList_whenFindAll_thenReturnPeopleList() {

        // Given / Arrange
		Person p1 = new Person("Carlos", "Oliveira", "carlos@email.com", "Rua dos sonhos, 1000", "Male");
		Person p2 = new Person("Rodrigo", "Carvalho", "rodrigo@email.com", "Rua dos doces, 0", "Male");

        given(repository.findAll()).willReturn(List.of(p1, p2));

		// When / Act
		List<Person> personList = service.findAll();

		// Then / Assert
		assertNotNull(personList);
		assertEquals(2, personList.size());
		assertTrue(personList.stream().anyMatch(x -> x.getFirstName().equals("Rodrigo")));
    }
    
    @Test
    @DisplayName("JUnit test for Given when findAll then return empty people list")
    void testGivenPeopleList_whenFindAll_thenReturnEmptyPeopleList() {

        // Given / Arrange
        given(repository.findAll()).willReturn(Collections.emptyList()); // uma alternativa ao Collections.emptyList() seria o List.of()

		// When / Act
		List<Person> personList = service.findAll();

		// Then / Assert
		assertTrue(personList.isEmpty());
		assertEquals(0, personList.size());
    }

    @Test
    @DisplayName("JUnit test for Given Person ID when findById then return person object")
    void testGivenPersonID_whenFindById_thenReturnPersonObject() {

        // Given / Arrenge
        given(repository.findById(anyLong())).willReturn(Optional.of(person));

        // When / Act
        Person savedPerson = service.findById(1l);

        // Then / Assert
        assertNotNull(savedPerson);
        assertEquals("Edielson", savedPerson.getFirstName());
    }

    @Test
    @DisplayName("JUnit test for Given update Person when update then return updated person object")
    void testGivenUpdatePerson_whenUpdate_thenReturnUpdatedPersonObject() {

        // Given / Arrenge
        given(repository.findById(person.getId())).willReturn(Optional.of(person));
        given(repository.save(person)).willReturn(person);

        person.setFirstName("Maria");
		person.setLastName("Souza");
		person.setAddress(person.getAddress());
		person.setGender("Female");

        // When / Act
        service.update(person);

        // Then / Assert
        assertNotNull(person);
        assertEquals("Maria", person.getFirstName());
    }

    @Test
    @DisplayName("JUnit test for Given delete Person when do nothing")
    void testGivenDeletePerson_whenDelete_thenDoNothing() {

        // Given / Arrenge
        given(repository.findById(person.getId())).willReturn(Optional.of(person));
        willDoNothing().given(repository).delete(person);

        // When / Act
        service.delete(person.getId());

        // Then / Assert
        verify(repository, times(1)).delete(person);
    }
}