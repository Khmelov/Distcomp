package by.molchan.Publisher;

import by.molchan.Publisher.DTOs.Requests.ArticleRequestDTO;
import by.molchan.Publisher.DTOs.Requests.CreatorRequestDTO;
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
		CreatorRequestDTO creator = new CreatorRequestDTO();
		creator.setLogin("");
		Set<ConstraintViolation<CreatorRequestDTO>> violations = validator.validate(creator);
		assertThat(violations).isNotEmpty();
	}

	@Test
	void notNullLogin() {
		CreatorRequestDTO creator = new CreatorRequestDTO();
		creator.setLogin(null);
		Set<ConstraintViolation<CreatorRequestDTO>> violations = validator.validate(creator);
		assertThat(violations).isNotEmpty();
	}

	@Test
	void notBlankLogin() {
		CreatorRequestDTO creator = new CreatorRequestDTO();
		creator.setLogin("  ");
		Set<ConstraintViolation<CreatorRequestDTO>> violations = validator.validate(creator);
		assertThat(violations).isNotEmpty();
	}


	@Test
	void notNullCreatorId(){
		ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
		Set<ConstraintViolation<ArticleRequestDTO>> violations = validator.validate(articleRequestDTO);
		assertThat(violations).isNotEmpty();
	}
}
