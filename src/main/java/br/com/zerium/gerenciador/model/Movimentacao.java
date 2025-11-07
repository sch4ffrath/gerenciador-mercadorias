package br.com.zerium.gerenciador.model;

import java.sql.Timestamp;

public class Movimentacao {
    private int id;
    private int produtoId;
    private String nomeProduto;
    private String tipo;
    private int quantidadeMovida;
    private Timestamp dataMovimentacao;
    private String observacao;

    public Movimentacao(int produtoId, String tipo, int quantidadeMovida, String observacao) {
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidadeMovida = quantidadeMovida;
        this.observacao = observacao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProdutoId() { return produtoId; }
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public String getTipo() { return tipo; }
    public int getQuantidadeMovida() { return quantidadeMovida; }
    public Timestamp getDataMovimentacao() { return dataMovimentacao; }
    public void setDataMovimentacao(Timestamp dataMovimentacao) { this.dataMovimentacao = dataMovimentacao; }
    public String getObservacao() { return observacao; }
}