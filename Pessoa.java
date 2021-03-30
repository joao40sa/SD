import java.io.Serializable;
import java.util.ArrayList;

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
	private ArrayList<ArrayList<String>> historicoVotos;

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
		this.historicoVotos = new ArrayList<>();
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

	public Boolean getEstado(){
		return this.estado;
	}

	public void setEstado(boolean estado){
		this.estado = estado;
	}

	public String getPassword(){
		return this.password;
	}

	public String getDepartamento(){
		return this.departamento;
	}

	public boolean adicionaEleicaoVotada(String nomeEleicao, String departamento){
		for(int i=0; i<this.historicoVotos.size(); i++){
			if(this.historicoVotos.get(i).get(0).equals(nomeEleicao))
				return false;
		}

		ArrayList<String> conjunto = new ArrayList<String>(); //PAR eleicao e local onde votou
		conjunto.add(nomeEleicao);
		conjunto.add(departamento);
		this.historicoVotos.add(conjunto);

		return true;
	}

	public ArrayList<ArrayList<String>> getHistoricoVotos(){
		return this.historicoVotos;
	}
}