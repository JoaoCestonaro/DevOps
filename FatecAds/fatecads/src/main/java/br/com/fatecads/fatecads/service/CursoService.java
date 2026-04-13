package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.repository.CursoRepository;

@Service

public class CursoService {

    //Injeção de dependência do repositório de cursos
    @Autowired
    private CursoRepository cursoRepository;

    //Método para salvar um curso

    public Curso save(Curso curso){
        return cursoRepository.save(curso);
    }

    //Método para listar todos os cursos
    public List<Curso> findAll(){
        return cursoRepository.findAll();
    }

    //Método para deletar curso
    public void deleteById(Integer id){
        cursoRepository.deleteById(id);
    }

    //Método para pesquisar curso por id
    public Curso findById(Integer id){
        return cursoRepository.findById(id).orElse(null);
    }
}
