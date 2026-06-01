package br.com.fatecads.fatecads.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.RecuperacaoSenha;
import br.com.fatecads.fatecads.entity.Usuario;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Integer> {

    List<RecuperacaoSenha> findByUsuarioAndUsadoEmIsNull(Usuario usuario);

    Optional<RecuperacaoSenha> findFirstByUsuarioAndUsadoEmIsNullOrderByIdRecuperacaoSenhaDesc(Usuario usuario);
}
