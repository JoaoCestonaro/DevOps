package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeClienteController {

    @GetMapping("/homecliente")
    public String homeCliente() {
        return "homecliente";
    }
}
