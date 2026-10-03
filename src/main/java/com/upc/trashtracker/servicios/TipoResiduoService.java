package com.upc.trashtracker.servicios;

import com.upc.trashtracker.entidades.TipoResiduo;
import com.upc.trashtracker.repositorio.TipoResiduoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoResiduoService {

    @Autowired
    private TipoResiduoRepository tipoResiduoRepository;

    public List<TipoResiduo> listarTodos() {
        return tipoResiduoRepository.findAll();
    }

    public Optional<TipoResiduo> buscarPorId(Long id) {
        return tipoResiduoRepository.findById(id);
    }

    @Transactional
    public void eliminar(Long id) {
        tipoResiduoRepository.deleteById(id);
    }

    @Transactional
    public TipoResiduo crear(String nombre) {
        TipoResiduo tipoResiduo = new TipoResiduo();
        tipoResiduo.setNombre(nombre);
        return tipoResiduoRepository.save(tipoResiduo);
    }

    @Transactional
    public TipoResiduo actualizarNombre(Long id, String nombre) {
        TipoResiduo tipoResiduo = tipoResiduoRepository.findById(id).get();
        tipoResiduo.setNombre(nombre);
        return tipoResiduoRepository.save(tipoResiduo);
    }
}
