package com.example.myapplication.ui.fragment_evento;

import java.util.List;
public class Evento {
    private String nome,id_cuoco,descrizione,data,ora,luogo,id;
    private int max_partecipanti;
    private List<String> lista_part;

    public List<String> getLista_part() {
        return lista_part;
    }

    public void setLista_part(List<String> lista_part) {
        this.lista_part = lista_part;
    }

    public Evento(){}

    public Evento(String nome_evento, String id_cuoco, String descrizione, String luogo, String data, String ora, int max_partecipanti, List<String> lista) {
        this.nome = nome_evento;
        this.id_cuoco = id_cuoco;
        this.descrizione = descrizione;
        this.data = data;
        this.ora = ora;
        this.luogo=luogo;
        this.max_partecipanti = max_partecipanti;
    }



    @Override
    public String toString() {
        return "Evento{" +
                "nome_evento='" + nome + '\'' +
                ", id_cuoco='" + id_cuoco + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                ", luogo='" + luogo + '\'' +
                ", max_partecipanti=" + max_partecipanti +
                '}';
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome_evento) {
        this.nome = nome_evento;
    }

    public String getId_cuoco() {
        return id_cuoco;
    }

    public void setId_cuoco(String id_utente) {
        this.id_cuoco = id_utente;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public int getMax_partecipanti() {
        return max_partecipanti;
    }

    public void setMax_partecipanti(int max_partecipanti) {
        this.max_partecipanti = max_partecipanti;
    }

    public String getId() {

        return id;
    }
    public void setId(String id){

        this.id=id;
    }


}
