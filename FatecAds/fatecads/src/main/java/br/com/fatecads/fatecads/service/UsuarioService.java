package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service

public class UsuarioService {

    //Injeção de dependência do repositório de usuario
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Método para salvar um professor
    public Usuario save(Usuario usuario){

        if (usuario.getIdUsuario() != null) {
            Usuario usuarioExistente = findById(usuario.getIdUsuario());
            if (usuarioExistente != null && (usuario.getSenhaUsuario() == null || usuario.getSenhaUsuario().isBlank())) {
                usuario.setSenhaUsuario(usuarioExistente.getSenhaUsuario());
                return usuarioRepository.save(usuario);
            }
        }

        if (usuario.getSenhaUsuario() != null && !usuario.getSenhaUsuario().isBlank()) {
            usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        }
        return usuarioRepository.save(usuario);
    }

    //Método para listar todas os usuarios
    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }
    
    //Método para deletar usuario
    public void deleteById(Integer id){
        usuarioRepository.deleteById(id);
    }

    //Método para pesquisar professor por id
    public Usuario findById(Integer id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public void atualizarSenha(Usuario usuario, String novaSenha) {
        usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}
