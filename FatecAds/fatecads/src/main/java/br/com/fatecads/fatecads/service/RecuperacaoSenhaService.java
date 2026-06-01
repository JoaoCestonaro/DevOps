package br.com.fatecads.fatecads.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.RecuperacaoSenha;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.RecuperacaoSenhaRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service
public class RecuperacaoSenhaService {

    private static final int MAX_TENTATIVAS = 5;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecuperacaoSenhaRepository recuperacaoSenhaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private WhatsAppService whatsAppService;

    @Value("${password-reset.otp-expiration-minutes:10}")
    private Integer minutosExpiracao;

    private final SecureRandom secureRandom = new SecureRandom();

    public void solicitarCodigo(String loginUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByLoginUsuario(loginUsuario);
        if (usuarioOptional.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOptional.get();
        String telefone = formatarTelefoneBrasil(usuario.getTelefoneUsuario());
        if (telefone.isBlank()) {
            return;
        }

        invalidarCodigosAnteriores(usuario);

        String codigo = gerarCodigo();
        RecuperacaoSenha recuperacaoSenha = new RecuperacaoSenha();
        recuperacaoSenha.setUsuario(usuario);
        recuperacaoSenha.setCodigoHash(passwordEncoder.encode(codigo));
        recuperacaoSenha.setExpiraEm(LocalDateTime.now().plusMinutes(minutosExpiracao));
        recuperacaoSenha.setTentativas(0);
        recuperacaoSenhaRepository.save(recuperacaoSenha);

        String mensagem = "Seu codigo de recuperacao de senha e: " + codigo
                + ". Ele expira em " + minutosExpiracao + " minutos.";
        whatsAppService.enviarMensagem(telefone, mensagem);
    }

    public boolean redefinirSenha(String loginUsuario, String codigo, String novaSenha) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByLoginUsuario(loginUsuario);
        if (usuarioOptional.isEmpty()) {
            return false;
        }

        Optional<RecuperacaoSenha> recuperacaoOptional = recuperacaoSenhaRepository
                .findFirstByUsuarioAndUsadoEmIsNullOrderByIdRecuperacaoSenhaDesc(usuarioOptional.get());

        if (recuperacaoOptional.isEmpty()) {
            return false;
        }

        RecuperacaoSenha recuperacaoSenha = recuperacaoOptional.get();
        if (recuperacaoSenha.getExpiraEm().isBefore(LocalDateTime.now())
                || recuperacaoSenha.getTentativas() >= MAX_TENTATIVAS) {
            recuperacaoSenha.setUsadoEm(LocalDateTime.now());
            recuperacaoSenhaRepository.save(recuperacaoSenha);
            return false;
        }

        recuperacaoSenha.setTentativas(recuperacaoSenha.getTentativas() + 1);

        if (!passwordEncoder.matches(codigo, recuperacaoSenha.getCodigoHash())) {
            recuperacaoSenhaRepository.save(recuperacaoSenha);
            return false;
        }

        usuarioService.atualizarSenha(usuarioOptional.get(), novaSenha);
        recuperacaoSenha.setUsadoEm(LocalDateTime.now());
        recuperacaoSenhaRepository.save(recuperacaoSenha);
        return true;
    }

    private void invalidarCodigosAnteriores(Usuario usuario) {
        for (RecuperacaoSenha recuperacaoSenha : recuperacaoSenhaRepository.findByUsuarioAndUsadoEmIsNull(usuario)) {
            recuperacaoSenha.setUsadoEm(LocalDateTime.now());
            recuperacaoSenhaRepository.save(recuperacaoSenha);
        }
    }

    private String gerarCodigo() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private String formatarTelefoneBrasil(String telefone) {
        if (telefone == null) {
            return "";
        }

        String apenasDigitos = telefone.replaceAll("\\D", "");
        if (apenasDigitos.isBlank()) {
            return "";
        }

        if (apenasDigitos.length() == 10 || apenasDigitos.length() == 11) {
            return "55" + apenasDigitos;
        }

        return apenasDigitos;
    }
}
