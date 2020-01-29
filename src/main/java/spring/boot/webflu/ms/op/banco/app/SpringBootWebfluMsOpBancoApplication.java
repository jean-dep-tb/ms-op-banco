package spring.boot.webflu.ms.op.banco.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;
import spring.boot.webflu.ms.op.banco.app.documents.OperationCurrentAccount;
import spring.boot.webflu.ms.op.banco.app.documents.TypeOperation;
import spring.boot.webflu.ms.op.banco.app.service.OperacionService;
import spring.boot.webflu.ms.op.banco.app.service.TipoOperacionService;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluMsOpBancoApplication implements CommandLineRunner{
	
	@Autowired
	private OperacionService operacionService;

	@Autowired
	private TipoOperacionService tipoOperacionService;
	
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
		
		TypeOperation deposito = new TypeOperation("1","Deposito");
		TypeOperation retiro = new TypeOperation("2","Retiro");
		TypeOperation entrecuentas = new TypeOperation("3","EntreCuentas");
		
		
		
		Flux.just(deposito,retiro,entrecuentas)
		.flatMap(tipoOperacionService::saveTipoProducto)
		.doOnNext(c -> {
			log.info("Tipo de producto creado: " +  c.getDescripcion() + ", Id: " + c.getIdTipo());
		}).thenMany(					
				Flux.just(
						//return serviceCredito.saveProducto(procredito);
						new OperationCurrentAccount("47305710","900001","100001", new Date(),deposito,1000.00,10.0),
						new OperationCurrentAccount("47305710","900002","100004", new Date(),retiro,2000.00,20.0),
						new OperationCurrentAccount("47305710","900003","100003", new Date(),entrecuentas,3000.00,30.0)
						
						)					
					.flatMap(operacion -> {
						return operacionService.saveOperacion(operacion);
					})					
				).subscribe(operacion -> log.info("Insert: " + operacion.getCuenta_destino() 
					+ " " + operacion.getCuenta_origen() + " " + operacion.getMontoPago()));
		
	}

}
