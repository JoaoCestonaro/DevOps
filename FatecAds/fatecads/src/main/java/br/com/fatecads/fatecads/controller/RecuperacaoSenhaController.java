package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.service.RecuperacaoSenhaService;

@Controller
public class RecuperacaoSenhaController {

    private static final String MENSAGEM_SOLICITACAO = "Se os dados estiverem corretos, enviaremos um codigo pelo WhatsApp.";

    @Autowired
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @GetMapping("/recuperar-senha")
    public String formularioSolicitacao() {
        return "recuperacao/solicitar";
    }

    @PostMapping("/recuperar-senha")
    public String solicitarCodigo(@RequestParam String loginUsuario, Model model) {
        recuperacaoSenhaService.solicitarCodigo(loginUsuario);
        model.addAttribute("mensagem", MENSAGEM_SOLICITACAO);
        model.addAttribute("loginUsuario", loginUsuario);
        return "recuperacao/validar";
    }

    @GetMapping("/recuperar-senha/validar")
    public String formularioValidacao() {
        return "recuperacao/validar";
    }

    @PostMapping("/recuperar-senha/validar")
    public String validarCodigo(
            @RequestParam String loginUsuario,
            @RequestParam String codigo,
            @RequestParam String novaSenha,
            Model model) {

        boolean senhaAtualizada = recuperacaoSenhaService.redefinirSenha(loginUsuario, codigo, novaSenha);
        if (senhaAtualizada) {
            return "redirect:/login?senhaRedefinida";
        }

        model.addAttribute("erro", "Codigo invalido ou expirado.");
        model.addAttribute("loginUsuario", loginUsuario);
        return "recuperacao/validar";
    }
}
