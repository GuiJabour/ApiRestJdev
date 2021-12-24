package curso.api.rest.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name="role")
@SequenceGenerator(name = "seq_role", sequenceName = "seq_role_name", allocationSize = 1, initialValue = 1)

public class Role implements GrantedAuthority{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="seq_role" )
	private Long id;
	
	private String nomeRole;/*Por exemplo: ROLE_SECRETÁRIO ou ROLE_ADMIN*/

	@Override
	/*Retorna o nome do papel, acesso, autorização...*/
	public String getAuthority() { 
		
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeRole() {
		return nomeRole;
	}

	public void setNomeRole(String nomeRole) {
		this.nomeRole = nomeRole;
	}
	
	
	
	
	
}
