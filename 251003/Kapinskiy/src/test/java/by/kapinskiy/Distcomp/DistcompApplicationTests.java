package by.kapinskiy.Distcomp;

import by.kapinskiy.Distcomp.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Requests.UserRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class Task320ApplicationTests {
	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void notEmptyLogin() {
		UserRequestDTO user = new UserRequestDTO();
		user.setLogin("");
		Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(user);
		assertThat(violations).isNotEmpty();
	}

	@Test
	void notNullLogin() {
		UserRequestDTO user = new UserRequestDTO();
		user.setLogin(null);
		Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(user);
		assertThat(violations).isNotEmpty();
	}

	@Test
	void notBlankLogin() {
		UserRequestDTO user = new UserRequestDTO();
		user.setLogin("  ");
		Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(user);
		assertThat(violations).isNotEmpty();
	}


	@Test
	void notNullUserId(){
		IssueRequestDTO issueRequestDTO = new IssueRequestDTO();
		Set<ConstraintViolation<IssueRequestDTO>> violations = validator.validate(issueRequestDTO);
		assertThat(violations).isNotEmpty();
	}
}
