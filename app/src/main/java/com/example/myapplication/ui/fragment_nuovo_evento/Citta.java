package com.example.myapplication.ui.fragment_nuovo_evento;

import java.util.ArrayList;

public class Citta {
    private String nome;
    private double latitudine;
    private double longitudine;
    private ArrayList<String> luoghi;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatit() {
        return latitudine;
    }

    public void setLatit(double latit) {
        this.latitudine = latit;
    }

    public double getLongit() {
        return longitudine;
    }

    public void setLongit(double longit) {
        this.longitudine = longit;
    }

    public ArrayList<String> getLuoghi() {
        return luoghi;
    }

    public void setLuoghi(ArrayList<String> IDluoghi) {
        this.luoghi = IDluoghi;
    }
}
