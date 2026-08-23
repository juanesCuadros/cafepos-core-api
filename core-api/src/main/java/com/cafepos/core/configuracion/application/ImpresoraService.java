package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.Impresora;
import com.cafepos.core.configuracion.domain.ImpresoraConexionInvalidaException;
import com.cafepos.core.configuracion.domain.ImpresoraNoEncontradaException;
import com.cafepos.core.configuracion.domain.ImpresoraRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Service
public class ImpresoraService {

    private static final int TIMEOUT_CONEXION_MS = 2500;
    private static final String TIPO_CONEXION_IP = "ip";

    private final ImpresoraRepository impresoraRepository;

    public ImpresoraService(ImpresoraRepository impresoraRepository) {
        this.impresoraRepository = impresoraRepository;
    }

    @Transactional(readOnly = true)
    public List<Impresora> listar() {
        return impresoraRepository.listar();
    }

    @Transactional(readOnly = true)
    public Impresora buscarPorId(Integer id) {
        return impresoraRepository.buscarPorId(id).orElseThrow(ImpresoraNoEncontradaException::new);
    }

    @Transactional
    public Impresora crear(Integer areaCocinaId, String tipo, String nombre, String tipoConexion, String ip,
                            Integer puerto) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        return impresoraRepository.guardar(
                new Impresora(tenantId, areaCocinaId, tipo, nombre, tipoConexion, ip, puerto));
    }

    @Transactional
    public Impresora actualizar(Integer id, JsonNullable<Integer> areaCocinaId, String tipo, String nombre,
                                 String tipoConexion, JsonNullable<String> ip, JsonNullable<Integer> puerto) {
        Impresora impresora = buscarPorId(id);
        impresora.actualizar(areaCocinaId, tipo, nombre, tipoConexion, ip, puerto);
        return impresoraRepository.guardar(impresora);
    }

    @Transactional
    public void eliminar(Integer id) {
        impresoraRepository.eliminar(buscarPorId(id));
    }

    /**
     * Solo tiene sentido para impresoras tipo_conexion=ip — una usb no
     * tiene ip/puerto que probar. Intento REAL de conexion TCP, sin
     * impresora real disponible en desarrollo el caso a demostrar es el
     * de fallo (timeout o conexion rechazada).
     */
    @Transactional(readOnly = true)
    public boolean probarConexion(Integer id) {
        Impresora impresora = buscarPorId(id);
        if (!TIPO_CONEXION_IP.equals(impresora.getTipoConexion())) {
            throw new ImpresoraConexionInvalidaException(
                    "La prueba de conexión solo aplica a impresoras con tipo_conexion=ip");
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(impresora.getIp(), impresora.getPuerto()), TIMEOUT_CONEXION_MS);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
