package com.upc.trashtracker.servicios;

import com.upc.trashtracker.entidades.Rol;
import com.upc.trashtracker.repositorio.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    @Transactional
    public Rol crear(String nombreRol, String descripcion) {
        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        rol.setDescripcion(descripcion);
        return rolRepository.save(rol);
    }

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Long id) {
        return rolRepository.findById(id);
    }

    @Transactional
    public Rol actualizarDatos(Long id, String nombreRol, String descripcion) {
        Rol rol = rolRepository.findById(id).get();
        if (nombreRol != null) {
            rol.setNombreRol(nombreRol);
        }
        if (descripcion != null) {
            rol.setDescripcion(descripcion);
        }
        return rolRepository.save(rol);
    }

    @Transactional
    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }
}
