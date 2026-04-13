package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;

@Service

public class DisciplinaService {

    //Injeção de dependência do repositório de disciplinas
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    //Método para salvar uma disciplina

    public Disciplina save(Disciplina disciplina){
        return disciplinaRepository.save(disciplina);
    }

    //Método para listar todas as disciplinas
    public List<Disciplina> findAll(){
        return disciplinaRepository.findAll();
    }
    
    //Método para deletar disciplina
    public void deleteById(Integer id){
        disciplinaRepository.deleteById(id);
    }

    //Método para pesquisar disciplina por id
    public Disciplina findById(Integer id){
        return disciplinaRepository.findById(id).orElse(null);
    }
}
