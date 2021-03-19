import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

class Eleicao implements Serializable{

	private Date data_inicio;
	private Date data_fim;
	private String titulo;
	private String descricao;
	private ArrayList<ListaCandidatos> lista_candidatos;

	private String restPessoa;
	private String restDep; 

	public Eleicao(String titulo, String descricao, Date data_inicio, Date data_fim, String restPessoa, String restDep){
		this.titulo = titulo;
		this.descricao = descricao;
		this.data_inicio = data_inicio;
		this.data_fim = data_fim;
		this.restDep = restDep;
		this.restPessoa = restPessoa;
		this.lista_candidatos = new ArrayList<>();
	}

	public boolean addListaCandidatos(ListaCandidatos lista){
		boolean add = true;
		for(int i=0; i<this.lista_candidatos.size(); i++){
			if(this.lista_candidatos.get(i).getNome() == lista.getNome()){
				return false;
			}
		}
		this.lista_candidatos.add(lista);
		return true;
	}

	public String getTitulo(){
		return this.titulo;
	}

	public String toString(){
		return "TITULO: "+this.titulo+"\nDESCRICAO: "+this.descricao+"\nDATA INICIO: "+this.data_inicio+"\nDATA FIM: "+this.data_fim+"\nRESTRICAO PESSOA: "+this.restPessoa+"\nRESTRICAO DEPARTAMENTO: "+this.restDep;
	}

}