package com.example.myapplication.ui.fragment_ricetta;

import org.json.JSONException;
import org.json.JSONObject;

public class Ricetta {

    private String  nome, ingredienti, id_cuoco, descrizione, foto,ricetta,categoria;
    private String id_ricetta="";
    private int rot;

    public Ricetta(){
    }

    @Override
    public String toString() {
        return "Ricetta{" +
                "nome='" + nome + '\'' +
                ", ingredienti='" + ingredienti + '\'' +
                ", id_cuoco='" + id_cuoco + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }

    public Ricetta(String titolo, String ingredienti, String id_cuoco , String descrizione, String foto) {
        this.ingredienti=ingredienti;
        this.id_cuoco = id_cuoco;
        this.nome = titolo;
        this.descrizione = descrizione;
        this.foto = foto;

    }

    public String getRicetta() {
        return ricetta;
    }

    public void setRicetta(String ricetta) {
        this.ricetta = ricetta;
    }

    public Ricetta(String titolo, String ingredienti, String id_cuoco , String descrizione, String foto, String ricetta, String categoria) {
        this.ingredienti=ingredienti;
        this.id_cuoco = id_cuoco;
        this.nome = titolo;
        this.descrizione = descrizione;
        this.foto = foto;
        this.ricetta=ricetta;
        this.categoria=categoria;

    }
    public void setId_ricetta(String id){this.id_ricetta=id;}
    public String getId_ricetta(){return id_ricetta;}

    public String getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(String ingredienti) {
        this.ingredienti = ingredienti;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getId_cuoco() {
        return id_cuoco;
    }

    public void setId_cuoco(String id_cuoco) {
        this.id_cuoco = id_cuoco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String titolo) {
        this.nome = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject jo = new JSONObject();
        jo.put("nome", nome);
        jo.put("ingredienti", ingredienti);
        jo.put("id_cuoco",id_cuoco);
        jo.put("descrizione",descrizione);
        jo.put("foto",foto);
        jo.put("id_ricetta",id_ricetta);
        jo.put("categoria",categoria);
        return jo;
    }

    public int getRot() {
        return rot;
    }

    public void setRot(int rot) {
        this.rot = rot;
    }
}
