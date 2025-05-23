package cl.duoc.week3.service;

import java.time.LocalDateTime;

import cl.duoc.week3.domain.exceptions.SinViajesException;
import cl.duoc.week3.domain.exceptions.EntidadNoEncontrada;
import cl.duoc.week3.domain.models.Viaje;
import cl.duoc.week3.repository.IViajeRepository;
import cl.duoc.week3.web.dtos.ViajeCreateRequest;
import cl.duoc.week3.web.dtos.ViajeUpdateRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

import cl.duoc.week3.domain.exceptions.ViajeInvalido;
import cl.duoc.week3.repository.IUsuarioRepository;

@Service
@Slf4j
public class ViajeService implements IViajeService{
    private final IViajeRepository repository;
    private final IUsuarioRepository usuarioRepository;

    public ViajeService(IViajeRepository repository, IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.repository = repository;
    }

    @Override
    public List<Viaje> findAll() {
        log.info("Buscando todos los viajes");
        List<Viaje> viajes = (List<Viaje>)repository.findAll();
        if (viajes.isEmpty()){
            throw new SinViajesException();
        }
        return viajes;
    }

    @Override
    public Viaje findById(Long id) {
        log.info("Buscando viaje con id {}", id);
        Viaje viaje= repository.findById(id).orElseThrow(() -> new EntidadNoEncontrada("Viaje", id));
        return viaje;
    }

    @Override
    public Viaje create(ViajeCreateRequest request) {
        log.info("Creando viaje con id chofer {} y id pasajero {}", request.idChofer(), request.idPasajero());
        Viaje viaje = new Viaje();
        setterEntities(viaje, request.idChofer(), request.idPasajero());
        viaje.setDireccion(request.direccion());
        viaje.setCiudad(request.ciudad());
        viaje.setFechaCreacion(LocalDateTime.now());
        return repository.save(viaje);
        
    }

    @Override
    public Viaje update(ViajeUpdateRequest request) {
        log.info("Actualizando viaje con id {}", request.id());
        Viaje viaje = repository.findById(request.id()).orElseThrow(() -> new EntidadNoEncontrada("Viaje", request.id()));
        setterEntities(viaje, request.idChofer(), request.idPasajero());
        viaje.setDireccion(request.direccion());
        viaje.setCiudad(request.ciudad());
        viaje.setFechaCreacion(LocalDateTime.now());
        return repository.save(viaje);
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando viaje con id {}", id);
        Viaje viaje = repository.findById(id).orElseThrow(() -> new EntidadNoEncontrada("Viaje", id));
        repository.delete(viaje);
    }

    private void setterEntities(Viaje viaje, Long choferId, Long pasajeroId) {
        log.info("Seteando chofer y pasajero");
        var chofer=usuarioRepository.findById(choferId).orElseThrow(() -> new EntidadNoEncontrada("Chofer", choferId));
        var pasajero=usuarioRepository.findById(pasajeroId).orElseThrow(() -> new EntidadNoEncontrada("Pasajero", pasajeroId));
        
        if(!chofer.getRol().equals("CHOFER")){
            log.error("Chofer no valido");
            throw new ViajeInvalido("Chofer no valido");
        }
        if(!pasajero.getRol().equals("PASAJERO")){
            log.error("Pasajero no valido");
            throw new ViajeInvalido("Pasajero no valido");
        }
        viaje.setChofer(chofer);
        viaje.setPasajero(pasajero);
    }
}
