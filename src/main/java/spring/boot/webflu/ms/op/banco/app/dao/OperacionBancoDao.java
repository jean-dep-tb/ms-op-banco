package spring.boot.webflu.ms.op.banco.app.dao;

import java.util.Date;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;

public interface OperacionBancoDao extends ReactiveMongoRepository<OperacionCuentaBanco, String> {
	@Query("{ 'dni' : ?0 }")
	Flux<OperacionCuentaBanco> viewDniCliente(String dni);

	@Query("{ 'dni' : ?0 , 'cuenta_origen' : ?1, 'codigo_bancario_origen' : ?2 }")
	Flux<OperacionCuentaBanco> consultaMovimientos(String dni, String numTarjeta, String codigo_bancario);
	
	@Query("{'fechaOperacion' : {'$gt' : ?0, '$lt' : ?1}}")
	Mono<OperacionCuentaBanco> consultaComisiones(Date from, Date to);
}
