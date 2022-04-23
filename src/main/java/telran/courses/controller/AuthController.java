package telran.courses.controller;

import java.util.Base64;

import javax.validation.Valid;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value(value = "${app.security.enable: true}")
	public static boolean isSecurityEnable;
	
	public AuthController(AccountingManagement accounting, PasswordEncoder passwordEncoder) {
		this.accounting = accounting;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping
	LoginResponse login( @RequestBody @Valid LoginData loginData) {
		
		if(!CoursesSecurityConfigurer.isSecurityEnable) {
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
