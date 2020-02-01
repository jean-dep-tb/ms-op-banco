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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;
import spring.boot.webflu.ms.op.banco.app.service.OperacionBancoService;

@RequestMapping("/api/operacionBancaria") //  OperCuentasCorrientes
@RestController
public class OperacionBancoControllers {

	@Autowired
	private OperacionBancoService productoService;

	//OPERACIONES EXISTENTES
	@GetMapping
	public Mono<ResponseEntity<Flux<OperacionCuentaBanco>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAllOperacion())

		);
	}

	//FILTRO CUENTAS BANCARIAS
	@GetMapping("/{id}")
	public Mono<ResponseEntity<OperacionCuentaBanco>> viewId(@PathVariable String id) {
		return productoService.findByIdOperacion(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping
	public Mono<OperacionCuentaBanco> updateProducto(@RequestBody OperacionCuentaBanco producto) {
		System.out.println(producto.toString());
		return productoService.saveOperacion(producto);
	}
	
	//RETIROS - TRANSACCION : UPDATE-CUENTAS-SALDO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO) - TIPO TARGETA
	@PostMapping("/retiro")
	public Mono<OperacionCuentaBanco> operacionRetiro(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionRetiro(producto);
	}
	
	//DEPOSITO - TRANSACCION : UPDATE-CUENTAS-SALDO - 2 TRACCIONES COBRA COMISION(RETIRO O DEPOSITO) - TIPO TARGETA
	@PostMapping("/deposito")
	public Mono<OperacionCuentaBanco> operacionDeposito(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionDeposito(producto);
	}
	
	//PAGO DE CUENTA CREDITO CON UNA CUENTA DE BANCO
	@PostMapping("/CuentaBancoACredito") //Cuenta_a_Cuenta
	public Mono<OperacionCuentaBanco> operacionCuentaBancoACredito(@RequestBody OperacionCuentaBanco producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionCuentaCuenta(producto);
	}

	//GUARDA UNA OPERACION BANCO
	@PostMapping
	public Mono<OperacionCuentaBanco> guardarProducto(@RequestBody OperacionCuentaBanco prod) {
		return productoService.saveOperacion(prod);
	}	
	
	
	//MOVIMIENTO DE CLIENTES
	@GetMapping("/dni/{dni}")
	public Flux<OperacionCuentaBanco> listProductoByDicliente(@PathVariable String dni) {
		Flux<OperacionCuentaBanco> oper = productoService.findAllOperacionByDniCliente(dni);
		return oper;
	}
	
	//MOVIMIENTOS BANCARIOS POR CLIENTES Y NRO TARGETA
	@GetMapping("/MovimientosBancarios/{dni}/{numTarjeta}")
	public Flux<OperacionCuentaBanco> movimientosBancarios(@PathVariable String dni, @PathVariable String numTarjeta,@PathVariable String codigo_bancario) {
		Flux<OperacionCuentaBanco> oper = productoService.consultaMovimientos(dni, numTarjeta,codigo_bancario);
		return oper;
	}
	
	//COMISION POR TIEMPO
	@GetMapping("consultaRangoFecha/{fecha1}")
	public Mono<ResponseEntity<OperacionCuentaBanco>> consultaMovimientosComisiones(@PathVariable String fecha1) throws ParseException{

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
			String f1 = fecha1.split("&&")[0]+" 00:00:00.000 +0000";
			Date from = format.parse(f1);
			Date to = format.parse(fecha1.split("&&")[1]+" 00:00:00.000 +0000");
			System.out.println(format.format(from));
			return productoService.consultaComisiones(from,to).map(p-> ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(p))
					.defaultIfEmpty(ResponseEntity.notFound().build());
		}
	
		
}



