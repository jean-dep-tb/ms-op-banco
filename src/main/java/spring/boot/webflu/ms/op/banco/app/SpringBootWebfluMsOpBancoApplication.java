package spring.boot.webflu.ms.op.banco.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;
import spring.boot.webflu.ms.op.banco.app.documents.TipoOperacionBanco;
import spring.boot.webflu.ms.op.banco.app.service.OperacionBancoService;
import spring.boot.webflu.ms.op.banco.app.service.TipoOperacionBancoService;

@SpringBootApplication
public class SpringBootWebfluMsOpBancoApplication implements CommandLineRunner{
	
	@Autowired
	private OperacionBancoService operacionService;

	@Autowired
	private TipoOperacionBancoService tipoOperacionService;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluMsOpBancoApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluMsOpBancoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		mongoTemplate.dropCollection("Operaciones").subscribe();
		mongoTemplate.dropCollection("TipoProducto").subscribe();
		
		TipoOperacionBanco deposito = new TipoOperacionBanco("1","Deposito");
		TipoOperacionBanco retiro = new TipoOperacionBanco("2","Retiro");
		TipoOperacionBanco cuentaCredito = new TipoOperacionBanco("3","CuentaCredito");
		TipoOperacionBanco cuentaCuenta = new TipoOperacionBanco("4","CuentaCuenta");
		
		
		
		Flux.just(deposito,retiro,cuentaCredito,cuentaCuenta)
		.flatMap(tipoOperacionService::saveTipoProducto)
		.doOnNext(c -> {
			log.info("Tipo de producto creado: " +  c.getDescripcion() + ", Id: " + c.getIdTipo());
		}).thenMany(					
				Flux.just(
						//return serviceCredito.saveProducto(procredito);
						new OperacionCuentaBanco("47305710","900001","100001", new Date(),deposito,1000.00,10.0,"bcp"),
						new OperacionCuentaBanco("47305710","900002","100004", new Date(),retiro,2000.00,20.0,"bbva"),
						new OperacionCuentaBanco("47305710","900003","100003", new Date(),cuentaCredito,3000.00,30.0,"bcp"),
						new OperacionCuentaBanco("47305710","900001","100003", new Date(),cuentaCuenta,5000.00,40.0,"bcp")
						
						)					
					.flatMap(operacion -> {
						return operacionService.saveOperacion(operacion);
					})					
				).subscribe(operacion -> log.info("Insert: " + operacion.getCuenta_destino() 
					+ " " + operacion.getCuenta_origen() + " " + operacion.getMontoPago()));
		
	}

}
