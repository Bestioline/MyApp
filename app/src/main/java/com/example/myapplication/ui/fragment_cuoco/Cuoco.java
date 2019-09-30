package com.example.myapplication.ui.fragment_cuoco;

public class Cuoco {
    private String nome, tel,password;
    private String email;
    private String imageProf;
    private int rot;

    public Cuoco(){}

    public Cuoco (String mail, String password){
        this.email=email;
        this.password=password;
    }



    public Cuoco(String nome, String tel, String password, String email, String imageProf, int rot) {
        this.nome = nome;
        this.tel = tel;
        this.password = password;
        this.email = email;
        this.imageProf = imageProf;
        this.rot = rot;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageProf() {
        return imageProf;
    }

    public void setImageProf(String imageProf) {
        this.imageProf = imageProf;
    }

    public int getRot() {
        return rot;
    }

    public void setRot(int rot) {
        this.rot = rot;
    }

}
