package spring.boot.webflu.ms.op.banco.app.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;
import spring.boot.webflu.ms.op.banco.app.service.OperacionBancoService;

@RequestMapping("/api/operacionBancaria") //  OperCuentasCorrientes
@RestController
public class OperacionBancoControllers {

	@Autowired
	private OperacionBancoService productoService;

	@ApiOperation(value = "OPERACIONES EXISTENTES DE CUENTA BANCO", notes="")
	@GetMapping
	public Mono<ResponseEntity<Flux<OperacionCuentaBanco>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAllOperacion())

		);
	}

	@ApiOperation(value = "BUSCAR POR ID OP CUENTA BANCO", notes="")
	@GetMapping("/{id}")
	public Mono<ResponseEntity<OperacionCuentaBanco>> viewId(@PathVariable String id) {
		return productoService.findByIdOperacion(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@ApiOperation(value = "ACTULIZAR UN PRODUCTO", notes="")
	@PutMapping
	public Mono<OperacionCuentaBanco> updateProducto(@RequestBody OperacionCuentaBanco producto) {
		System.out.println(producto.toString());
		return productoService.saveOperacion(producto);
	}
	
	@ApiOperation(value = "RETIROS - TRANSACCION : UPDATE-CUENTAS-SALDO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO) - TIPO TARGETA", notes="")
	@PostMapping("/retiro")
	public Mono<OperacionCuentaBanco> operacionRetiro(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionRetiro(producto);
	}
		
	@ApiOperation(value = "DEPOSITO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO)", notes="")
	@PostMapping("/deposito")
	public Mono<OperacionCuentaBanco> operacionDeposito(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionDeposito(producto);
	}
	
	@ApiOperation(value = "PAGO DE CUENTA CREDITO CON UNA CUENTA DE BANCO", notes="")
	@PostMapping("/CuentaBancoACredito") //Cuenta_a_Cuenta
	public Mono<OperacionCuentaBanco> operacionCuentaBancoACredito(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionCuentaCuentaCredito(producto);
	}
	
	@ApiOperation(value = "OPERACION TRANSFERENCIA DE CUENTA A CUENTA", notes="")
	@PostMapping("/cuentaACuenta")
	public Mono<OperacionCuentaBanco> operacionCuentaACuenta(@RequestBody OperacionCuentaBanco oper) {
		//System.out.println(producto.toString());
		return productoService.operacionCuentaCuenta(oper);
	}

	@ApiOperation(value = "GUARDA UNA OPERACION BANCO", notes="")
	@PostMapping
	public Mono<OperacionCuentaBanco> guardarProducto(@RequestBody OperacionCuentaBanco prod) {
		return productoService.saveOperacion(prod);
	}	
		
	@ApiOperation(value = "LISTA LOS CLIENTE CON OP-BANCO", notes="")
	@GetMapping("/dni/{dni}")
	public Flux<OperacionCuentaBanco> operacionesBancoCliente(@PathVariable String dni) {
		Flux<OperacionCuentaBanco> oper = productoService.findAllOperacionByDniCliente(dni);
		return oper;
	}
	
	@ApiOperation(value = "MOVIMIENTOS BANCARIOS POR CLIENTES Y NRO CUENTA", notes="")
	@GetMapping("/MovimientosBancarios/{dni}/{numTarjeta}/{codigo_bancario}")
	public Flux<OperacionCuentaBanco> movimientosBancarios(@PathVariable String dni, @PathVariable String numTarjeta,@PathVariable String codigo_bancario) {
		Flux<OperacionCuentaBanco> operacion = productoService.consultaMovimientos(dni, numTarjeta,codigo_bancario);
		return operacion;
	}
	
//	@ApiOperation(value = "COMISION POR TIEMPO", notes="")
//	@GetMapping("consultaRangoFecha/{fecha}")
//	public Mono<ResponseEntity<OperacionCuentaBanco>> consultaMovimientosComisiones(@PathVariable String fecha) throws ParseException{
//
//			System.out.println("FECHA : " + fecha );
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
//			String f1 = fecha.split("&&")[0]+" 00:00:00.000+0000";
//			System.out.println(f1);
//			Date from = format.parse(f1);
//			Date to = format.parse(fecha.split("&&")[1]+" 00:00:00.000+0000");
//			System.out.println(to);			
//			System.out.println(format.format(from));
//			return productoService.consultaComisiones(from,to).map(p-> ResponseEntity.ok()
//					.contentType(MediaType.APPLICATION_JSON)
//					.body(p))
//					.defaultIfEmpty(ResponseEntity.notFound().build());
//		}
	
		
}



