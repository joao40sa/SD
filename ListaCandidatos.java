import java.io.Serializable;
import java.util.ArrayList;

class ListaCandidatos implements Serializable{

	private String nome;
	private ArrayList<Pessoa> candidatos; //0-Presidente, 1-VicePresidente, ...

	public ListaCandidatos(String nome, ArrayList<Pessoa> candidatos){
		this.nome = nome;
		this.candidatos = candidatos;
	}

	public String getNome(){
		return this.nome;
	}
}