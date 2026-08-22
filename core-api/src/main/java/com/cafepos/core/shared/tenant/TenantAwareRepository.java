package com.cafepos.core.shared.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base para TODO repositorio JPA del proyecto — extender esta interfaz en
 * vez de JpaRepository directamente.
 *
 * Motivo (causa raiz confirmada leyendo el bytecode de
 * TransactionalRepositoryProxyPostProcessor en spring-data-commons 3.5.0):
 * Spring Data solo le da una transaccion "gratis" a un metodo de
 * repositorio si ese metodo resuelve a una implementacion real respaldada
 * por una clase (RepositoryInformation.getTargetClassMethod(metodo) !=
 * metodo) — ese es el caso de los metodos heredados de SimpleJpaRepository
 * (findById, save, ...), que SI tiene @Transactional(readOnly = true) a
 * nivel de clase. Un metodo que vos declares en tu propia interfaz
 * (derivado por nombre o @Query) NO tiene esa clase de implementacion
 * detras — Spring Data lo resuelve con un proxy dinamico — asi que
 * targetClassMethod == metodo SIEMPRE, y el metodo termina "no
 * transaccional" pase lo que pase con enableDefaultTransactions. No es un
 * bug de configuracion ni de bean duplicado: es como esta diseñado el
 * mecanismo, y no hay flag que lo cambie.
 *
 * Sin una transaccion propia, TenantAwareJpaTransactionManager.doBegin()
 * nunca corre para ese metodo, y el "SET LOCAL app.current_tenant_id" que
 * activa Row-Level Security no se ejecuta — cualquier query contra una
 * tabla con RLS (toda tabla con columna tenant_id, ver V1__schema_v4.sql)
 * falla con un error crudo de Postgres, o peor, si la conexion pooled
 * trae un tenant_id de un request anterior, podria devolver datos de OTRO
 * tenant en silencio.
 *
 * @Transactional a nivel de INTERFAZ si se hereda hacia cualquier metodo
 * declarado en una subinterfaz (proxy dinamico de JDK, comportamiento
 * soportado explicitamente por Spring) — asi que extender esta interfaz
 * resuelve el problema para SIEMPRE, sin depender de que cada metodo
 * nuevo lleve su propio @Transactional a mano.
 */
@NoRepositoryBean
@Transactional(readOnly = true)
public interface TenantAwareRepository<T, ID> extends JpaRepository<T, ID> {
}
