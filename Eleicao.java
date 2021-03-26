import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

class Eleicao implements Serializable{

	private Date data_inicio;
	private Date data_fim;
	private String titulo;
	private String descricao;
	private ArrayList<ListaCandidatos> lista_candidatos;
	private ArrayList<String> mesas_voto;

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
		this.mesas_voto = new ArrayList<>();
	}

	public boolean addListaCandidatos(ListaCandidatos lista){
		for(int i=0; i<this.lista_candidatos.size(); i++){
			if(this.lista_candidatos.get(i).getNome().equals(lista.getNome())){
				return false;
			}
		}
		this.lista_candidatos.add(lista);
		return true;
	}

	public boolean removeListaCandidatos(String nome){
		int ind = -1;
		for(int i=0; i<this.lista_candidatos.size(); i++){
			if(this.lista_candidatos.get(i).getNome().equals(nome)){
				ind = i;
				break;
			}
		}
		if(ind == -1)
			return false;
		this.lista_candidatos.remove(ind);
		return true;
	}

	public boolean adicionaMesa(String nomeMesa){
		for(int i=0; i<mesas_voto.size(); i++){
			if(mesas_voto.get(i).equals(nomeMesa)){
				return false;
			}
		}
		mesas_voto.add(nomeMesa);
		return true;
	}

	public boolean removeMesa(String nomeMesa){
		int ind = -1;
		for(int i=0; i<mesas_voto.size(); i++){
			if(mesas_voto.get(i).equals(nomeMesa)){
				ind = -1;
				break;
			}
		}
		if(ind==-1)
			return false;
		mesas_voto.remove(ind);
		return true;
	}

	public void setDescricao(String new_descricao){
		this.descricao = new_descricao;
	}

	public void setTitulo(String new_titulo){
		this.titulo = new_titulo;
	}

	public void setDataFim(Date new_data_fim){
		this.data_fim = new_data_fim;
	}

	public void setDataInicio(Date new_data_inicio){
		this.data_inicio = new_data_inicio;
	}

	public Date getDataInicio(){
		return this.data_inicio;
	}

	public Date getDataFim(){
		return this.data_fim;
	}

	public String getTitulo(){
		return this.titulo;
	}

	public String getRestricaoTipo(){
		return this.restPessoa;
	}

	public String toString(){
		return "TITULO: "+this.titulo+"\nDESCRICAO: "+this.descricao+"\nDATA INICIO: "+this.data_inicio+"\nDATA FIM: "+this.data_fim+"\nRESTRICAO PESSOA: "+this.restPessoa+"\nRESTRICAO DEPARTAMENTO: "+this.restDep;
	}

}