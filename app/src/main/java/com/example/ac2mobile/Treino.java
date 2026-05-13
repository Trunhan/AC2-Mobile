package com.example.ac2mobile;

import java.io.Serializable;

public class Treino implements Serializable {

    private String id; // ID gerado pelo Firebase
    private String nome;
    private String tipoAtividade;
    private String data;
    private int duracao;
    private String intensidade;
    private boolean concluido;

    // CONSTRUTOR VAZIO: Obrigatório para o Firebase (Firestore/Realtime Database)
    public Treino() {
    }

    // CONSTRUTOR COMPLETO: Útil para criar o objeto no código
    public Treino(String id, String nome, String tipoAtividade, String data, int duracao, String intensidade, boolean concluido) {
        this.id = id;
        this.nome = nome;
        this.tipoAtividade = tipoAtividade;
        this.data = data;
        this.duracao = duracao;
        this.intensidade = intensidade;
        this.concluido = concluido;
    }

    // GETTERS E SETTERS (O Firebase usa esses métodos para ler e gravar os dados)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(String tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public String getIntensidade() {
        return intensidade;
    }

    public void setIntensidade(String intensidade) {
        this.intensidade = intensidade;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    @Override
    public String toString() {
        return nome + " (" + tipoAtividade + ")";
    }
}