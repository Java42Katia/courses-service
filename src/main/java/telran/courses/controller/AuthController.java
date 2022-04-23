package telran.courses.controller;

import java.util.Base64;

import javax.validation.Valid;

import org.slf4j.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import telran.courses.dto.*;
import telran.courses.exceptions.BadRequestException;
import telran.courses.security.*;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class AuthController {
	static Logger LOG = LoggerFactory.getLogger(AuthController.class);
	AccountingManagement accounting;
	PasswordEncoder passwordEncoder;
	
	public AuthController(AccountingManagement accounting, PasswordEncoder passwordEncoder) {
		this.accounting = accounting;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping
	LoginResponse login( @RequestBody @Valid LoginData loginData, BindingResult errors) {
		/* V.R. There are 3 problems here:
		 *  1. errors.hasFieldErrors() always returns false, that is why whole if
		 *  will never be true. And whole construction doen't work.
		 *  2. Why BindingResult is used? 
		 *  3. isSecurityEnable can be passed here like it is done in CoursesSecurityConfigurer.
		 *  It is the possibility of resource file, it can be used not in the single place. 
		 */
		if(errors.hasFieldErrors() && !CoursesSecurityConfigurer.isSecurityEnable) {
			LOG.debug("Security disable");
			return new LoginResponse("", "ADMIN");
		}
	
		LOG.debug("login data are email {}, password: {}", loginData.email, loginData.password);
		Account account = accounting.getAccount(loginData.email);
		
		if(account == null || !passwordEncoder.matches(loginData.password, account.getPasswordHash())) {
			throw new BadRequestException("Wrong credentials");
		}
		LoginResponse response = new LoginResponse(getToken(loginData), account.getRole());
		LOG.debug("accessToken: {}, role {}", response.accessToken, response.role);
		return response;
	}

	private String getToken(LoginData loginData) {
		//"Basic <username:password> in Base64 code
		byte[] code = String.format("%s:%s", loginData.email, loginData.password).getBytes();
		return "Basic " + Base64.getEncoder().encodeToString(code);
	}

}
