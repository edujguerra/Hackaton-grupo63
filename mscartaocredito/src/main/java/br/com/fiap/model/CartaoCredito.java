package br.com.fiap.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartaoCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "CPF não pode ser vazio.")
    @Column(name = "cpf", nullable = false)
    private String cpf;

    @NotBlank(message = "Limite do cartão não pode ser vazio.")
    @Column(name = "limite", nullable = false)
    private Double limite;

    @NotBlank(message = "Numero do cartão não pode ser vazio.")
    @Column(name = "numero", nullable = false)
    private String numero;

    @NotBlank(message = "Data de validade do cartão não pode ser vazio.")
    @Column(name = "data_validade", nullable = false)
    private Date data_validade;

    @NotBlank(message = "CVV do cartão não pode ser vazio.")
    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaoCredito that = (CartaoCredito) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(cpf, that.cpf) &&
                Objects.equals(limite, that.limite) &&
                Objects.equals(numero, that.numero) &&
                Objects.equals(data_validade, that.data_validade) &&
                Objects.equals(cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf, limite, numero, data_validade, cvv);
    }
}
