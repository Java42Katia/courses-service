package telran.courses.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
@EnableWebSecurity
public class CoursesSecurityConfigurer extends WebSecurityConfigurerAdapter {
	

	@Value(value = "${app.security.enable: true}")
	public boolean isSecurityEnable;
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic();
		http.cors().and().csrf().disable();
		http.authorizeHttpRequests().antMatchers("/login").permitAll();
		if (isSecurityEnable) {
			http.authorizeHttpRequests().antMatchers(HttpMethod.GET).hasAnyRole("USER", "ADMIN");
			http.authorizeHttpRequests().anyRequest().hasRole("ADMIN");
		} else {
			http.authorizeHttpRequests().anyRequest().permitAll();
		}
	}

}
