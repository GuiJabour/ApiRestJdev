package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Autowired
	private TelefoneRepository telefoneRepository;

	@GetMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value ="cacheuser", allEntries = true)
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
 
	}

	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value ="cacheUsuarios", allEntries = true)
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> init() throws InterruptedException {

		Pageable page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> usuarios = usuarioRepository.findAll(page);

		//Thread.sleep(6000);/*Segura o código por 6 segundos - simulando o processo lento*/
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}

	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CacheEvict(value ="cacheUsuarios", allEntries = true)
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> usuarioPagina(@PathVariable("pagina") int pagina) throws InterruptedException {

		Pageable page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> usuarios = usuarioRepository.findAll(page);

		//Thread.sleep(6000);/*Segura o código por 6 segundos - simulando o processo lento*/
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CacheEvict(value ="cacheUsuarios", allEntries = true)
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException {

		PageRequest pageRequest  = null;
		Page<Usuario> usuarios = null;
		
		if (nome == null || (nome!=null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findAll(pageRequest);
		}else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		
		
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CacheEvict(value ="cacheUsuarios", allEntries = true)
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> usuarioPorNomePage(@PathVariable("nome") String nome, @PathVariable("page") int page) throws InterruptedException {

		PageRequest pageRequest  = null;
		Page<Usuario> usuarios = null;
		
		if (nome == null || (nome!=null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findAll(pageRequest);
		}else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		
		
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		for (int pos = 0; pos<usuario.getTelefones().size(); pos++){
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		/*Consumindo API Externa*/
		if (usuario.getCep() != null) {
		URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
		URLConnection connection = url.openConnection();
		InputStream inputStream = connection.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = bufferedReader.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		
		/*Consumindo API Externa*/
		}
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuariosalvo = usuarioRepository.save(usuario);
		
		implementacaoUserDetailsService.insereAcessoPadrao(usuariosalvo.getId());

		return new ResponseEntity<Usuario>(usuariosalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		
		for (int pos = 0; pos<usuario.getTelefones().size(); pos++){
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemp = usuarioRepository.findById(usuario.getId()).get();
		
		if (!userTemp.getSenha().equals(usuario.getSenha())) {/*Senhas Diferentes*/
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		Usuario usuariosalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuariosalvo, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String deletar(@PathVariable Long id) {
		usuarioRepository.deleteById(id);
		return "Deletado!";
	}
	
	@DeleteMapping(value="/removerTelefone/{id}", produces="application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return "Telefone Deletado!";
	}

}
