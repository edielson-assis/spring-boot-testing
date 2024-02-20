package br.com.erudio.unitstests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.com.erudio.integrationstests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;

@Order(1)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest extends AbstractIntegrationTest {

	@Autowired
	private PersonRepository repository;

	private Person person;

	@BeforeEach
	void setup() {
		person = new Person("Edielson", "Assis", "edielson@email.com", "Rua dos sonhos, 1000", "Male");
	}

	@DisplayName("Given person objejct when save then return saved person")
	@Test
	void testGivenPersonObject_whenSave_thenReturnSavedPerson() {

		// Given / Arrange & When / Act
		Person savePerson = repository.save(person);

		// Then / Assert
		assertNotNull(savePerson);
		assertTrue(savePerson.getId() > 0);
		assertEquals("Edielson", savePerson.getFirstName());
	}
	
	@DisplayName("Given person list when findAll then return person list")
	@Test
	void testGivenPersonList_whenFindAll_thenReturnPersonList() {

		// Given / Arrange
		Person p1 = new Person("Edielson", "Assis", "edielson@email.com", "Rua dos sonhos, 1000", "Male");
		Person p2 = new Person("Rodrigo", "Carvalho", "rodrigo@email.com", "Rua dos doces, 0", "Male");

		repository.saveAll(List.of(p1, p2));

		// When / Act
		List<Person> personList = repository.findAll();

		// Then / Assert
		assertNotNull(personList);
		assertEquals(2, personList.size());
		assertTrue(personList.stream().anyMatch(x -> x.getFirstName().equals("Rodrigo")));
	}

	@DisplayName("Given person objejct when findById then return person object")
	@Test
	void testGivenPersonObject_whenFindById_thenReturnPersonObject() {

		// Given / Arrange
		repository.save(person);

		// When / Act
		Person savePerson = repository.findById(person.getId()).get();

		// Then / Assert
		assertNotNull(savePerson);
		assertEquals(person.getId(), savePerson.getId());
		assertNotEquals("Rodrigo", savePerson.getFirstName());
	}

	@DisplayName("Given person objejct when findByemail then return person object")
	@Test
	void testGivenPersonObject_whenFindByEmail_thenReturnPersonObject() {

		// Given / Arrange
		repository.save(person);

		// When / Act
		Person savePerson = repository.findByEmail(person.getEmail()).get();

		// Then / Assert
		assertNotNull(savePerson);
		assertEquals(person.getId(), savePerson.getId());
		assertNotEquals("Rodrigo", savePerson.getFirstName());
	}

	@DisplayName("Given person objejct when updatePerson then return updated person object")
	@Test
	void testGivenPersonObject_whenUpdatePerson_thenReturnUpdatedPersonObject() {

		// Given / Arrange
		repository.save(person);

		// When / Act
		Person savePerson = repository.findByEmail(person.getEmail()).get();
		savePerson.setFirstName("Rodrigo");
		savePerson.setEmail("rodrigo@email.com");

		Person updatedPerson = repository.save(savePerson);

		// Then / Assert
		assertNotNull(updatedPerson);
		assertEquals("rodrigo@email.com", updatedPerson.getEmail());
		assertEquals("Rodrigo", updatedPerson.getFirstName());
	}

	@DisplayName("Given person objejct when delete then remove person")
	@Test
	void testGivenPersonObject_whenDelete_thenRemovePerson() {

		// Given / Arrange
		repository.save(person);

		// When / Act
		repository.deleteById(person.getId());

		Optional<Person> personOptional = repository.findById(person.getId());

		// Then / Assert
		assertEquals(Optional.empty(), personOptional);
		assertTrue(personOptional.isEmpty());
	}

	@DisplayName("Given firstName and lastName when findByJPQL then return person object")
	@Test
	void testGivenFirstNameAndLastName_whenFindByJPQL_thenReturnPersonObject() {

		// Given / Arrange
		repository.save(person);

		String firstName = "Edielson";
		String lastName = "Assis";

		// When / Act
		Person personJPQL = repository.findByJPQL(firstName, lastName);

		// Then / Assert
		assertEquals(firstName, personJPQL.getFirstName());
		assertNotNull(personJPQL);
	}
	
	@DisplayName("Given firstName and lastName when findByJPQLNamedParameters then return person object")
	@Test
	void testGivenFirstNameAndLastName_whenFindByJPQLNamedParameters_thenReturnPersonObject() {

		// Given / Arrange
		repository.save(person);

		String firstName = "Edielson";
		String lastName = "Assis";

		// When / Act
		Person personJPQL = repository.findByJPQLNamedParameters(firstName, lastName);

		// Then / Assert
		assertEquals(firstName, personJPQL.getFirstName());
		assertNotNull(personJPQL);
	}
	
	@DisplayName("Given firstName and lastName when findByNativeSQL then return person object")
	@Test
	void testGivenFirstNameAndLastName_whenFindByNativeSQL_thenReturnPersonObject() {

		// Given / Arrange
		repository.save(person);

		String firstName = "Edielson";
		String lastName = "Assis";

		// When / Act
		Person personJPQL = repository.findByNativeSQL(firstName, lastName);

		// Then / Assert
		assertEquals(firstName, personJPQL.getFirstName());
		assertNotNull(personJPQL);
	}
	
	@DisplayName("Given firstName and lastName when findByNativeSQLWithNamedParameters then return person object")
	@Test
	void testGivenFirstNameAndLastName_whenFindByNativeSQLWithNamedParameters_thenReturnPersonObject() {

		// Given / Arrange
		repository.save(person);

		String firstName = "Edielson";
		String lastName = "Assis";

		// When / Act
		Person personJPQL = repository.findByNativeSQLWithNamedParameters(firstName, lastName);

		// Then / Assert
		assertEquals(firstName, personJPQL.getFirstName());
		assertNotNull(personJPQL);
	}
}