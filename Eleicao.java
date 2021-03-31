import java.util.Date;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

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
	private int totalVotos;
	private int votosBranco;
	private int votosNulo;
	private boolean estado; //estado da eleição;

	public Eleicao(String titulo, String descricao, Date data_inicio, Date data_fim, String restPessoa, String restDep){
		this.titulo = titulo;
		this.descricao = descricao;
		this.data_inicio = data_inicio;
		this.data_fim = data_fim;
		this.restDep = restDep;
		this.restPessoa = restPessoa;
		this.lista_candidatos = new ArrayList<>();
		this.mesas_voto = new ArrayList<>();
		this.votosBranco = 0;
		this.votosNulo = 0;
		this.totalVotos = 0;
		this.estado = true; //true -> eleicao on ----------- false -> eleicao off
	}

	public boolean adicionaVoto(String lista){
		if(lista.equals("branco")){
			this.votosBranco++;
			this.totalVotos++;
			return true;
		}
		else if(lista.equals("nulo")){
			this.votosNulo++;
			this.totalVotos++;
			return true;
		}
		else{
			for(int i=0; i<this.lista_candidatos.size(); i++){
				if(lista_candidatos.get(i).getNome().equals(lista)){
					this.totalVotos++;
					lista_candidatos.get(i).addVoto();
					return true;
				}
			}
		}
		return false;
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

	public ArrayList<ListaCandidatos> getListasCandidatos(){
		return this.lista_candidatos;
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

	public String getRestricaoDep(){
		return this.restDep;
	}

	public String toString(){
		return "TITULO: "+this.titulo+"\nDESCRICAO: "+this.descricao+"\nDATA INICIO: "+this.data_inicio+"\nDATA FIM: "+this.data_fim+"\nRESTRICAO PESSOA: "+this.restPessoa+"\nRESTRICAO DEPARTAMENTO: "+this.restDep;
	}

	public int getTotalVotos(){
		return this.totalVotos;
	}
	
	public int getVotosBrancos(){
		return this.votosBranco;
	}

	public int getVotosNulos(){
		return this.votosNulo;
	}



	public boolean getEstado(){
		return this.estado;
	}


	public void setEstado(boolean state){
		this.estado = state;
	}
}