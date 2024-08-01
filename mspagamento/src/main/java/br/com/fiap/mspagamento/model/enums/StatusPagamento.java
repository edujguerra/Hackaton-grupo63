package br.com.fiap.mspagamento.model.enums;

public enum StatusPagamento {
    A("Aprovado"),
    C("Cancelado");


    private String descricao;


    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    
}
