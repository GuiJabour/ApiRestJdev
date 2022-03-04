package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/* Tempo de validade do token */
	private static final long EXPIRATION_TIME = 172800000;

	/* Senha única para compor a autenticação e ajudar na seguraça */
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/* Prefixo padrão de token */
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/* Gerando token de autenticado e adicionando ao cabeçalho e resposta http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		/* Montagem do token */
		String JWT = Jwts.builder() /* Chama o gerador de token */
				.setSubject(username)/* Adiciona o Usuário */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))/* Tempo de expiração */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();/*
																		 * Compactação de algoritmo e geração de senha
																		 */

		/* Junta o token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; /* Exp: Bearer 81gf91fgf812121hhrsw4r454534545fd */

		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Authorizattion: Bearer 23vijsdfnewrwe */

		/*Liberando acesso para porta diferente do angular*/
		
		
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
				.atualizaTokenUsuario(JWT, username);
		
		
		
		liberacaoCors(response); /*Liberando resposta para portas diferentes que usam a API*/
		
		/* Escreve token como resposta no corpo http */
		
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
		response.flushBuffer();

	}

	/*
	 * Retorna o usuário validado com token ou, caso não seja válido, retorna null
	 */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

		/* Pega o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);

		try {
		if (token != null) {

			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

			/* Faz a validação do token do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET) /* Exp: Bearer 81gf91fgf812121hhrsw4r454534545fd */
					.parseClaimsJws(tokenLimpo) /* 81gf91fgf812121hhrsw4r454534545fd */
					.getBody().getSubject();/* Joãozinho */

			if (user != null) {

				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				/* Retorna o usuário logado */

				if (usuario != null) {

					if (usuario.getToken().equalsIgnoreCase(tokenLimpo)) {

						return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
								usuario.getAuthorities());
					}
				}

			}

		}
		}catch (ExpiredJwtException e ) {
			
			try {
				response.getOutputStream().println("Seu Token está expirado! "
						+ "Faça o login ou informe um novo token para autenticação!");
			} catch (IOException e1) {
			}
			
		}

		liberacaoCors(response);

		return null; /* Não autorizado */

	}

	
	private void liberacaoCors(HttpServletResponse response) throws IOException {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {

			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {

			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {

			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Methods") == null) {

			response.addHeader("Access-Control-Allow-Methods", "*");
		}
		

	}

}
