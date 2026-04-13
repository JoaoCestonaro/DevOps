package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.repository.ProfessorRepository;

@Service

public class ProfessorService {

    //Injeção de dependência do repositório de professor
    @Autowired
    private ProfessorRepository professorRepository;

    //Método para salvar um professor

    public Professor save(Professor professor){
        return professorRepository.save(professor);
    }

    //Método para listar todas os professsores
    public List<Professor> findAll(){
        return professorRepository.findAll();
    }
    
    //Método para deletar professor
    public void deleteById(Integer id){
        professorRepository.deleteById(id);
    }

    //Método para pesquisar professor por id
    public Professor findById(Integer id){
        return professorRepository.findById(id).orElse(null);
    }
}
