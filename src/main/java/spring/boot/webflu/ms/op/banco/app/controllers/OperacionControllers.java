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
import spring.boot.webflu.ms.op.banco.app.documents.OperationCurrentAccount;
import spring.boot.webflu.ms.op.banco.app.service.OperacionService;
import spring.boot.webflu.ms.op.banco.app.service.TipoOperacionService;

@RequestMapping("/api/OperCuentasCorrientes")
@RestController
public class OperacionControllers {

	@Autowired
	private OperacionService productoService;

	//Muestra todos las operaciones existentes
	@GetMapping
	public Mono<ResponseEntity<Flux<OperationCurrentAccount>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(productoService.findAllOperacion())

		);
	}

	//Filtra todas cuentas bancarias por id
	@GetMapping("/{id}")
	public Mono<ResponseEntity<OperationCurrentAccount>> viewId(@PathVariable String id) {
		return productoService.findByIdOperacion(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	//actualiza cuenta bancaria
	@PutMapping
	public Mono<OperationCurrentAccount> updateProducto(@RequestBody OperationCurrentAccount producto) {
		System.out.println(producto.toString());
		return productoService.saveOperacion(producto);
	}
	
	//Realiza una Transaccion(RETIROS) 
	//guardando en el microservicio op-banco(movimientos)
	// Y Actualiza el saldo de la tarjeta(retiro)
	//mayor a 2 transacciones ya sea deposito o retiro, se les cobrara un monto de comision por tipo de tarjeta
	@PostMapping("/retiro")
	public Mono<OperationCurrentAccount> saveOperacionRetiro(@RequestBody OperationCurrentAccount producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionRetiro(producto);
	}

	//Realiza una Transaccion(Deposito) 
	//guardando en el microservicio operaciones(movimientos) 
	// Y Actualiza el saldo de la tarjeta(deposito)
	//mayor a 2 transacciones ya sea deposito o retiro, se les cobrara un monto de comision por tipo de tarjeta
	@PostMapping("/deposito")
	public Mono<OperationCurrentAccount> saveOperacionDeposito(@RequestBody OperationCurrentAccount producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionDeposito(producto);
	}
	
	//para pagar una cuenta de credito con
	//una cuenta de banco
	@PostMapping("/Cuenta_a_Cuenta")
	public Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(@RequestBody OperationCurrentAccount producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionCuentaCuenta(producto);
	}
	
	
	/*Guarda o Crea una tarjeta bancaria(tipo: ahorro, plazo fijo ....) 
	 * - si el cliente ya tiene cuenta bancaria no debe de registrarlo*/
	@PostMapping
	public Mono<OperationCurrentAccount> guardarProducto(@RequestBody OperationCurrentAccount prod) {
		return productoService.saveOperacion(prod);
	}	
	
	
	//Muestra todas los movimientos bancarios por el numero de dni del cliente
	@GetMapping("/dni/{dni}")
	public Flux<OperationCurrentAccount> listProductoByDicliente(@PathVariable String dni) {
		Flux<OperationCurrentAccount> oper = productoService.findAllOperacionByDniCliente(dni);
		return oper;
	}
	
	//Muestra todos los movimientos bancarios por cliente y numero tarjeta(cuenta de ahorros)
	@GetMapping("/MovimientosBancarios/{dni}/{numTarjeta}")
	public Flux<OperationCurrentAccount> movimientosBancarios(@PathVariable String dni, @PathVariable String numTarjeta,@PathVariable String codigo_bancario) {
		Flux<OperationCurrentAccount> oper = productoService.consultaMovimientos(dni, numTarjeta,codigo_bancario);
		return oper;
	}
	
	//Reporte de movimientos con comisiones por periodo de tiempo 
	@GetMapping("consultaRangoFecha/{fecha1}")
	public Mono<ResponseEntity<OperationCurrentAccount>> consultaMovimientosComisiones(@PathVariable String fecha1) throws ParseException{

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
			String f1 = fecha1.split("&&")[0]+" 00:00:00.000 +0000";
			Date from = format.parse(f1);
			Date to = format.parse(fecha1.split("&&")[1]+" 00:00:00.000 +0000");
			System.out.println(format.format(from));
			return productoService.consultaComisiones(from,to).map(p-> ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(p))
					.defaultIfEmpty(ResponseEntity.notFound().build());
		}
	
		
}



