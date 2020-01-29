package spring.boot.webflu.ms.op.banco.app.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.client.ProductoBancoClient;
import spring.boot.webflu.ms.op.banco.app.client.ProductoBancoCreditoClient;
import spring.boot.webflu.ms.op.banco.app.dao.OperacionDao;
import spring.boot.webflu.ms.op.banco.app.documents.OperationCurrentAccount;
import spring.boot.webflu.ms.op.banco.app.documents.TypeOperation;
import spring.boot.webflu.ms.op.banco.app.dto.CurrentAccount;
import spring.boot.webflu.ms.op.banco.app.exception.RequestException;
import spring.boot.webflu.ms.op.banco.app.service.OperacionService;
import spring.boot.webflu.ms.op.banco.app.service.TipoOperacionService;

@Service
public class OperacionServiceImpl implements OperacionService {
	
	Double comision = 0.0;

	private static final Logger log = LoggerFactory.getLogger(OperacionServiceImpl.class);
	
	@Autowired
	public OperacionDao productoDao;

	@Autowired
	public OperacionDao tipoProductoDao;

	@Autowired
	private TipoOperacionService tipoProductoService;
	
	@Autowired
	private ProductoBancoClient productoBancoClient;
	
	@Autowired
	private ProductoBancoCreditoClient productoBancoCreditoClient;

	@Override
	public Flux<OperationCurrentAccount> findAllOperacion() {
		return productoDao.findAll();

	}

	@Override
	public Mono<OperationCurrentAccount> findByIdOperacion(String id) {
		return productoDao.findById(id);

	}

	@Override
	public Flux<OperationCurrentAccount> findAllOperacionByDniCliente(String dni) {
		return productoDao.viewDniCliente(dni);

	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(OperationCurrentAccount operacion) {
		
		//consultar al ms-cuenta-banco las cuentas del cliente
		
		Mono<CurrentAccount> oper1 = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen());
		log.info("datos cliente  --->> "+oper1.map(p -> {
			return p.toString();
		}));
		
		//consultar al ms-cuenta-banco
//		Mono<CurrentAccount> oper1 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
//				.uri("/numero_cuenta/" + operacion.getCuenta_origen()).retrieve().bodyToMono(CurrentAccount.class).log();

		/*
			tipo producto
			Ahorro = 1
			Cuentas corrientes  = 2
			Cuentas a plazo fijo = 3
			cuenta ahorro personal VIP 0 = 4
			cuenta corriente personal VIP = 5
			empresarial PYME  = 6 
			empresarial Corporative = 7
			cuenta plazo fijo VIP = 8

		 */
		
		
		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {  // si es producto = ahorro

				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) { //si es cuenta corrientes

				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) { //si es cuenta plazo fijo

				comision = 4.5;

				//las demas cuentas deben de tener un monto minimo
			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {

				if (c1.getSaldo() == 20) {

					throw new RequestException(
							"Ya no puede realizar retiros, debe tener un monton minimo" + " de S/.20 en su cuenta.");
				}
				
				//CUANTO SERIA LA COMISION
				
			}
			
			//consultar todso los moviemientos realizados
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			
			return valor.flatMap(p -> {
				// NUMERO DE OPERACIONES -> MOVIMIENTOS
				if (p > 2) {
					operacion.setComision(comision);
				}
				
				//REALIZAR UN RETIRNO EN EL MS-PRODUCTO BANCARIO
				Mono<CurrentAccount> oper2 = productoBancoClient
						.retiroBancario(operacion.getCuenta_origen(),operacion.getMontoPago(),operacion.getComision());
				
				System.out.println("paso el metodo");
				
				//Mono<CurrentAccount> oper2 = productoBancoClient.retiroBancario(operacion);
				
//				Mono<CurrentAccount> oper2 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
//						.put().uri("/retiro/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
//								+ operacion.getComision()).retrieve().bodyToMono(CurrentAccount.class).log();

				return oper2.flatMap(c -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}

				
				//REALIZAR UN PAGO DE UNA CUENTA DE CREDITO
					
				Mono<CurrentAccount> oper3 = productoBancoCreditoClient.despositoBancario(operacion.getMontoPago(),operacion.getCuenta_destino());
					
//				Mono<CurrentAccount> oper3 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoCredito/")
//						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
//						.put().uri("/pago/" + operacion.getCuenta_destino() + "/" + operacion.getMontoPago())
//						.retrieve().bodyToMono(CurrentAccount.class).log();
							
				return oper3.flatMap(d -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}
					
					TypeOperation tipo = new TypeOperation();
					tipo.setIdTipo("3");
					tipo.setDescripcion("Pago credito");
					operacion.setTipoOperacion(tipo);

					return productoDao.save(operacion);
				});
				
				});
			});
		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionRetiro(OperationCurrentAccount operacion) {
		
		Mono<CurrentAccount> oper1 = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen());
		
//		Mono<CurrentAccount> oper1 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
//				.uri("/numero_cuenta/" + operacion.getCuenta_origen()).retrieve().bodyToMono(CurrentAccount.class).log();

		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {

				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {

				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {

				comision = 4.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {

				if (c1.getSaldo() == 20) {

					throw new RequestException(
							"Ya no puede realizar retiros, debe tener un monton minimo" + " de S/.20 en su cuenta.");
				}
			}
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES
				if (p > 2) {
					operacion.setComision(comision);
				}
				
				//REALIZAR UN RETIRNO EN EL MS-PRODUCTO BANCARIO
				Mono<CurrentAccount> oper2 = productoBancoClient
						.retiroBancario(operacion.getCuenta_origen(),operacion.getMontoPago(),operacion.getComision());
								
//				Mono<CurrentAccount> oper2 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().put()
//						.uri("/retiro/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
//								+ operacion.getComision())
//						.retrieve().bodyToMono(CurrentAccount.class).log();
								
				return oper2.flatMap(c -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}

					TypeOperation tipo = new TypeOperation();
					tipo.setIdTipo("2");
					tipo.setDescripcion("Retiro");
					operacion.setTipoOperacion(tipo);

					return productoDao.save(operacion);

				});
			});
		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionDeposito(OperationCurrentAccount operacion) {
		
		//consumir otros microservicio - cuenta bancaria
		Mono<CurrentAccount> oper1 = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen());
			
//		Mono<CurrentAccount> oper1 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
//				.uri("/numero_cuenta/" + operacion.getCuenta_origen()).retrieve().bodyToMono(CurrentAccount.class).log();
		
		
		
		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {
				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {
				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {
				comision = 4.5;
			}
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			return valor.flatMap(p -> {
				if (p > 2) {
					operacion.setComision(comision);
				}
				
				Mono<CurrentAccount> oper = productoBancoClient
						.despositoBancario(operacion.getMontoPago(),operacion.getCuenta_origen(),operacion.getComision());
				
				System.out.println("paso el metodo");
				
//				Mono<CurrentAccount> oper = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().put()
//						.uri("/deposito/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
//								+ operacion.getComision())
//						.retrieve().bodyToMono(CurrentAccount.class).log();
				
				
				
				return oper.flatMap(c -> {
					if (c.getNumero_cuenta() == null) {
						return Mono.error(new InterruptedException("No existe Numero de tarjeta"));
					}

					TypeOperation tipo = new TypeOperation();

					/*
					 * tipo.setIdTipo(operacion.getTipoOperacion().getIdTipo());
					 * tipo.setDescripcion(operacion.getTipoOperacion().getDescripcion());
					 */
					tipo.setIdTipo("1");
					tipo.setDescripcion("Deposito");
					operacion.setTipoOperacion(tipo);
					return productoDao.save(operacion);

				});

			});

		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacion(OperationCurrentAccount producto) {
		return productoDao.save(producto);
	}

	@Override
	public Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numTarjeta) {

		return productoDao.consultaMovimientos(dni, numTarjeta);
	}
	
	@Override
	public Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to) {
		return productoDao.consultaComisiones(from, to);
	}

	

}
