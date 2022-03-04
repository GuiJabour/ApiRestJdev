package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*Mapeia URLs, endereços... Autoriza ou bloqueia acesso a urls*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	/* Configura as solicitações de acesso por http */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/* Ativando a proteção contra usuários que não estão validados por token */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

				/*
				 * Ativando a permissão para acesso à pagina inical do sistema Ex:
				 * Sistema.com.br/index/
				 */
				.disable().authorizeRequests().antMatchers("/").permitAll().
				
				antMatchers("/index", "/recuperar/**").permitAll()

				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

				/* URL de logout */
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

				/* Mapeia url de logout e invalida o usuário */

				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

				/* A implementação filtra as requisições de login para autenticação */
				.and()
				.addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)

				/*
				 * Filtra demais requisições para verificar presença do token JWT no HEADER HTTP
				 */
				.addFilterBefore(new JWTAPIAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/* Service que ira consultar usuario no banco de dados */
		auth.userDetailsService(implementacaoUserDetailsService)
				/* Padrão de codificação de senha */
				.passwordEncoder(new BCryptPasswordEncoder());

	}

}
