import java.io.Serializable;

class Pessoa implements Serializable{

	private String nome;
	private String password;
	private String departamento;
	private String faculdade;
	private String contacto;
	private String morada;
	private int numero;
	private String validade_cc;
	private String tipo;
	private boolean estado;

	public Pessoa(String nome, String password, String departamento, String faculdade, String contacto, String morada, int numero, String validade_cc, String tipo){
		this.nome = nome;
		this.password = password;
		this.departamento = departamento;
		this.faculdade = faculdade;
		this.contacto = contacto;
		this.morada = morada;
		this.numero = numero;
		this.validade_cc = validade_cc;
		this.tipo = tipo;
		this.estado = false;
	}

	public int getNumero()
	{
		return this.numero;
	}

	public String getTipo(){
		return this.tipo;
	}

	public String toString(){
		return "TIPO: "+this.tipo+"\nNOME: "+this.nome+"\nNUMERO: "+this.numero;
	}

	public void getEstado(){
		return this.estado;
	}

	public void setEstado(boolean estado){
		this.estado = estado;
	}
}
