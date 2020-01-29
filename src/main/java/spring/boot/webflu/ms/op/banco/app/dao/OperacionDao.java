package spring.boot.webflu.ms.op.banco.app.dao;

import java.util.Date;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperationCurrentAccount;

public interface OperacionDao extends ReactiveMongoRepository<OperationCurrentAccount, String> {
	@Query("{ 'dni' : ?0 }")
	Flux<OperationCurrentAccount> viewDniCliente(String dni);

	@Query("{ 'dni' : ?0 , 'cuenta_origen' : ?1 }")
	Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numTarjeta );
	
	@Query("{'fechaOperacion' : {'$gt' : ?0, '$lt' : ?1}}")
	Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to);
}
