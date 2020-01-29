package spring.boot.webflu.ms.op.banco.app.service;

import java.util.Date;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperationCurrentAccount;

public interface OperacionService {

	Flux<OperationCurrentAccount> findAllOperacion();
	
	Mono<OperationCurrentAccount> findByIdOperacion(String id);

	Mono<OperationCurrentAccount> saveOperacion(OperationCurrentAccount producto);
	
	Mono<OperationCurrentAccount> saveOperacionRetiro(OperationCurrentAccount producto);

	Mono<OperationCurrentAccount> saveOperacionDeposito(OperationCurrentAccount producto);
	
	Flux<OperationCurrentAccount> findAllOperacionByDniCliente(String dni);

	/*Flux<Operacion> saveOperacionList(List<Operacion> producto);*/

	Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numeroTarjeta);
	
	Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(OperationCurrentAccount operacion);

	Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to);
}
