package br.com.fiap.repository;

import br.com.fiap.model.CartaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Integer> {

    @Query("SELECT c FROM CartaoCredito c WHERE c.cpf = :cpf")
    List<CartaoCredito> findByCpf(String cpf);
}
