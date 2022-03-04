package curso.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoErro;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ServiceEnviaEmail serviceEnviaEmail;
	
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recupera (@RequestBody Usuario login) throws Exception{
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (user == null) {
			objetoErro.setCode("404");
			objetoErro.setError("Usuário não encontrado!");
		}else {
			System.out.println("Usuario id: " + user.getId());
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			System.out.println("Inicio Envia Email!");
			serviceEnviaEmail.enviarEmail("Recuperação de Senha", user.getLogin(), "Sua nova senha é: " + senhaNova);
			System.out.println("Fim envia email");
			/*Rotina de envio de e-mail*/
			objetoErro.setCode("200");
			objetoErro.setError("Acesso enviado para seu email!");
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
	}
	
	
}
