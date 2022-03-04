package curso.api.rest.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long userId;
	
	private String userLogin;
	
	private String userNome;
	
	private String userSenha;
	
	
	
	public UsuarioDTO(Usuario usuario) {
		this.userId = usuario.getId();
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.userSenha = usuario.getSenha();
		
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getUserId() {
		return userId;
	}
	
	public void setUserSenha(String userSenha) {
		this.userSenha = userSenha;
	}
	public String getUserSenha() {
		return userSenha;
	}
	
	
	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserNome() {
		return userNome;
	}

	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}
	
	
	
}
