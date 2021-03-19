import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

class Eleicao implements Serializable{

	Date data_inicio;
	Date data_fim;
	String titulo;
	String descricao;
	ArrayList<ArrayList<Pessoa>> lista_candidatos

	public Eleicao(String titulo, String descricao, Date data_inicio, Date data_fim){
		this.titulo = titulo;
		this.descricao = descricao;
		this.data_inicio = data_inicio;
		this.data_fim = data_fim;
		this.lista_candidatos = new ArrayList<>();
	}

	public String getTitulo(){
		return this.titulo;
	}

	public String toString(){
		return "TITULO: "+this.titulo+"\nDESCRICAO: "+this.descricao+"\nDATA INICIO: "+this.data_inicio+"\nDATA FIM: "+this.data_fim;
	}

}