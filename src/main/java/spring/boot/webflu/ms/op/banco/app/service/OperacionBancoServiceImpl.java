package spring.boot.webflu.ms.op.banco.app.service;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.client.ProductoBancoClient;
import spring.boot.webflu.ms.op.banco.app.client.ProductoBancoCreditoClient;
import spring.boot.webflu.ms.op.banco.app.dao.OperacionBancoDao;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;
import spring.boot.webflu.ms.op.banco.app.documents.TipoOperacionBanco;
import spring.boot.webflu.ms.op.banco.app.dto.CuentaBanco;
import spring.boot.webflu.ms.op.banco.app.exception.RequestException;
import spring.boot.webflu.ms.op.banco.app.service.OperacionBancoService;

@Service
public class OperacionBancoServiceImpl implements OperacionBancoService {
	
	Double comision = 0.0;

	private static final Logger log = LoggerFactory.getLogger(OperacionBancoServiceImpl.class);
	
	@Autowired
	public OperacionBancoDao productoDao;

	@Autowired
	public OperacionBancoDao tipoProductoDao;
	
	@Autowired
	private ProductoBancoClient productoBancoClient;
	
	@Autowired
	private ProductoBancoCreditoClient productoBancoCreditoClient;

	@Override
	public Flux<OperacionCuentaBanco> findAllOperacion() {
		return productoDao.findAll();

	}

	@Override
	public Mono<OperacionCuentaBanco> findByIdOperacion(String id) {
		return productoDao.findById(id);

	}

	@Override
	public Flux<OperacionCuentaBanco> findAllOperacionByDniCliente(String dni) {
		return productoDao.viewDniCliente(dni);

	}

	@Override
	public Mono<OperacionCuentaBanco> saveOperacionCuentaCuenta(OperacionCuentaBanco operacion) {
		
		//OBTENER LA CUENTA DE BANCO
		Mono<CuentaBanco> oper1 = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),operacion.getCodigo_bancario_origen());
		log.info("datos cliente  --->> "+oper1.map(p -> {
			return p.toString();
		}));


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

				comision = 5.0;
				
				if (c1.getSaldo() == 100) {

					throw new RequestException(
							"NO PUEDE REALIZAR RETIROS, MONTO MINIMO EN LA CUENTA S/.100");
				}
				
			}
			
			//consultar todso los moviemientos realizados
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			
			return valor.flatMap(p -> {
				System.out.println("Numero de transacciones >>>>>>" + p);
				// NUMERO DE OPERACIONES -> MOVIMIENTOS
				if (p > 2) {
					System.out.println("Numero de transacciones >>>>>>" + p);
					operacion.setComision(comision);
				}
				
				//REALIZAR UN RETIRNO EN EL MS-PRODUCTO BANCARIO
				Mono<CuentaBanco> oper2 = productoBancoClient
						.retiroBancario(operacion.getCuenta_origen(),operacion.getMontoPago(),operacion.getComision(),operacion.getCodigo_bancario_origen());
				
				System.out.println("paso el metodo");

				return oper2.flatMap(c -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}
					
				System.out.println("PARAMETROS : " + "MONTO : " + operacion.getMontoPago() + " BANCO : "  + operacion.getCodigo_bancario_origen());	
				
				//REALIZAR UN PAGO DE UNA CUENTA DE CREDITO					
				Mono<CuentaBanco> oper3 = productoBancoCreditoClient.despositoBancario(operacion.getMontoPago(),operacion.getCuenta_destino(),operacion.getCodigo_bancario_destino());
				
				System.out.println("paso un pago");
				
				return oper3.flatMap(d -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}
					//PARA QUE REGISTRE UNA TRANSACCION
					TipoOperacionBanco tipo = new TipoOperacionBanco();
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
	public Mono<OperacionCuentaBanco> saveOperacionRetiro(OperacionCuentaBanco operacion) {
		//OBTENIENDO EL NUMERO DE CUENTA + EL BANCO AL QUE PERTENECE
		Mono<CuentaBanco> oper1 = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),operacion.getCodigo_bancario_origen());

		System.out.println("listo");
		
		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) { //ahorro
				
				comision = 2.0;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {//corriente

				comision = 3.0;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {//plazo fijo

				comision = 4.0;

				//4 cuenta ahorro personal VIP
				//5 cuenta corriente personal VIP
				//6 empresarial PYME 
				//7 empresarial Corporative
				//8 cuenta plazo fijo VIP
			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {
				
				comision = 5.0;

				if (c1.getSaldo() == 100) {

					throw new RequestException(
							"NO PUEDE REALIZAR RETIROS, MONTO MINIMO EN LA CUENTA S/.100");
				}
			}
			
			//CONTAR EL NUMERO DE MOVIMIENTOS
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			
			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES, SI LOS MOVIMIENTOS ES MAYOR A DOS
				if (p > 2) {
					System.out.println("Numero de transacciones >>>>>>" + p);
					operacion.setComision(comision);
				}
				
				//REALIZAR UN RETIRNO EN EL MS-PRODUCTO BANCARIO
				Mono<CuentaBanco> oper2 = productoBancoClient
						.retiroBancario(operacion.getCuenta_origen(),operacion.getMontoPago(),operacion.getComision(),operacion.getCodigo_bancario_origen());
								
				return oper2.flatMap(c -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}
					
					//PARA QUE REGISTRE UNA TRANSACCION
					TipoOperacionBanco tipo = new TipoOperacionBanco();
					tipo.setIdTipo("2");
					tipo.setDescripcion("Retiro");
					operacion.setTipoOperacion(tipo);

					return productoDao.save(operacion);

				});
			});
		});
	}

	@Override
	public Mono<OperacionCuentaBanco> saveOperacionDeposito(OperacionCuentaBanco operacion) {
		
		//PARA OBTENER LA CUENTA DE BANCO - CUENTA BANCARIA
		Mono<CuentaBanco> oper1 = productoBancoClient.findByNumeroCuenta(operacion.getCuenta_origen(),operacion.getCodigo_bancario_origen());
		
		return oper1.flatMap(c1 -> {
			
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {
				comision = 2.0;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {
				comision = 3.0;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {
				comision = 4.0;
			}else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {
				
				comision = 5.0;

				if (c1.getSaldo() == 100) {

					throw new RequestException(
							"NO PUEDE REALIZAR RETIROS, MONTO MINIMO EN LA CUENTA S/.100");
				}
			}
			
			//CONSULTAR EL NUMERO DE OPERACIONES
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			
			//Mono<Long> valor = productoDao.viewDniCliente(operacion.getDni()).count();
			
			return valor.flatMap(p -> {
				if (p > 2) {
					//ASIGNA LA COMISION
					System.out.println("Numero de transacciones >>>>>>" + p);
					operacion.setComision(comision);
				}
				//REALIZA EL DEPOSITO EN LA CUENTA DE BANCO
				Mono<CuentaBanco> oper = productoBancoClient
						.despositoBancario(operacion.getMontoPago(),operacion.getCuenta_origen(),operacion.getComision(),operacion.getCodigo_bancario_origen());
				
				System.out.println("paso el metodo");			
				
				return oper.flatMap(c -> {
					if (c.getNumero_cuenta() == null) {
						return Mono.error(new InterruptedException("TARGETA INVALIDA"));
					}

					//PARA QUE REGISTRE UNA TRANSACCION
					TipoOperacionBanco tipo = new TipoOperacionBanco();
					tipo.setIdTipo("1");
					tipo.setDescripcion("Deposito");
					operacion.setTipoOperacion(tipo);
					
					
					return productoDao.save(operacion);

				});

			});

		});
	}

	@Override
	public Mono<OperacionCuentaBanco> saveOperacion(OperacionCuentaBanco producto) {
		return productoDao.save(producto);
	}

	@Override
	public Flux<OperacionCuentaBanco> consultaMovimientos(String dni, String numTarjeta, String codigo_bancario) {

		return productoDao.consultaMovimientos(dni, numTarjeta);
	}
	
	@Override
	public Mono<OperacionCuentaBanco> consultaComisiones(Date from, Date to) {
		return productoDao.consultaComisiones(from, to);
	}

	

}
