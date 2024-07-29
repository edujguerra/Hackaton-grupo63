package br.com.fiap.mscliente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="tb_clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @NotBlank(message = "CPF não pode ser vazio.")
    @Column(name = "nr_cpf", nullable = false)
    private String cpf;

    @NotBlank(message = "Nome não pode ser vazio.")
    @Column(name = "nm_cliente", nullable = false)
    private String nome;

    @NotBlank(message = "Email não pode ser vazio.")
    @Column(name = "nr_email", nullable = false)
    private String email;

    @Column(name = "nr_fone")
    private String fone;

    @Column(name = "ds_enderec")
    private String rua;

    @Column(name = "nm_cidade")
    private String cidade;

    @Column(name = "sg_uf")
    private String uf;

    @NotBlank(message = "CEP não pode ser vazio.")
    @Column(name = "nr_cep", nullable = false)
    private String cep;

    @NotBlank(message = "País não pode ser vazio.")
    @Column(name = "nm_pais", nullable = false)
    private String pais;

}
