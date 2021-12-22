package curso.api.rest.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Telefone {
	
	

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_telefones")
	@SequenceGenerator(name="seq_id_telefones", sequenceName = "seq_telefones")	
	private Long idtelefone;

	private String numero;

	@JsonIgnore
	@org.hibernate.annotations.ForeignKey(name = "usuairo_id")
	@ManyToOne(optional = false)
	private Usuario usuario;

	public Long getIdtelefone() {
		return idtelefone;
	}

	public void setIdtelefone(Long idtelefone) {
		this.idtelefone = idtelefone;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
