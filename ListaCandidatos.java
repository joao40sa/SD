import java.io.Serializable;
import java.util.ArrayList;

class ListaCandidatos implements Serializable{

	private String nome;
	private ArrayList<Pessoa> candidatos; //0-Presidente, 1-VicePresidente, ...
	private int votos;

	public ListaCandidatos(String nome, ArrayList<Pessoa> candidatos){
		this.nome = nome;
		this.candidatos = candidatos;
		this.votos = 0;
	}

	public String getNome(){
		return this.nome;
	}

	public int getVotos(){
		return this.votos;
	}

	public void addVoto(){
		this.votos++;
	}
}