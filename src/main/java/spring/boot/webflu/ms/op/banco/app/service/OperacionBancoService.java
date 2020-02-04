package spring.boot.webflu.ms.op.banco.app.service;

import java.util.Date;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;

public interface OperacionBancoService {

	Flux<OperacionCuentaBanco> findAllOperacion();

	Mono<OperacionCuentaBanco> findByIdOperacion(String id);

	Mono<OperacionCuentaBanco> saveOperacion(OperacionCuentaBanco producto);

	Mono<OperacionCuentaBanco> saveOperacionRetiro(OperacionCuentaBanco producto);

	Mono<OperacionCuentaBanco> saveOperacionDeposito(OperacionCuentaBanco producto);

	Flux<OperacionCuentaBanco> findAllOperacionByDniCliente(String dni);
	
	/* Flux<Operacion> saveOperacionList(List<Operacion> producto); */

	Flux<OperacionCuentaBanco> consultaMovimientos(String dni, String numeroTarjeta, String codigo_bancario); //consultaMovimientos

	Mono<OperacionCuentaBanco> saveOperacionCuentaCuentaCredito(OperacionCuentaBanco operacion);

	public Mono<OperacionCuentaBanco> operacionCuentaCuenta(OperacionCuentaBanco operacion);
	
	Mono<OperacionCuentaBanco> consultaComisiones(Date from, Date to);

}
