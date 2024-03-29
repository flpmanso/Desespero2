package com.example.desespero2;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class Post {

    private int id;
    private String nome;
    private Double latitude;
    private Double longitude;
    private java.sql.Date dataInclusao;

    public Post(String nome) {
        this.nome = nome;
    }


    public Post(String nome, Double latitude, Double longitude) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Post() {

    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
