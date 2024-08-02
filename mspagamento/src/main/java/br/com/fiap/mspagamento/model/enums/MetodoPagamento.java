package br.com.fiap.mspagamento.model.enums;

public enum MetodoPagamento {
    CC("Cartao de credito");


    private String descricao;


    MetodoPagamento(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    
}
