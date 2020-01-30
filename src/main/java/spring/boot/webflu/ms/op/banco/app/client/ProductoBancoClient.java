package spring.boot.webflu.ms.op.banco.app.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperationCurrentAccount;
import spring.boot.webflu.ms.op.banco.app.dto.CurrentAccount;

@Service
public class ProductoBancoClient {

	private static final Logger log = LoggerFactory.getLogger(CurrentAccount.class);
	
	@Autowired
	@Qualifier("productoBanco")
	private WebClient productoBancoClient;
	
	//consumir de una cuenta de banco
//	public Mono<CurrentAccount> findByNumeroCuenta(String num) {
//		
//		Map<String, String> pathVariable = new HashMap<String,String>();
//		pathVariable.put("numero_cuenta",cuenta_origen);
//		pathVariable.put("monto",);
//		
//		return productoBancoClient.get()
//				.uri("/numero_cuenta/{num}/{codigo_bancario}",pathVariable)
//				.accept(MediaType.APPLICATION_JSON)
//				.retrieve()
//				.bodyToMono(CurrentAccount.class);
//		    	
//	}
	
	public Mono<CurrentAccount> findByNumeroCuenta(String num,String codigo_bancario_destino) {
		
		log.info("Actualizando: cuenta origen --> retiro --> numTargeta : "+ num + " codigo_bancario_destino : " + codigo_bancario_destino);
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("num",num);
		pathVariable.put("codigo_bancario",codigo_bancario_destino);
		
		return productoBancoClient.get()
				.uri("/numero_cuenta/{num}/{codigo_bancario}",pathVariable)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(CurrentAccount.class);
		    	
	}
	
//	Mono<CurrentAccount> oper2 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoBancario/")
//			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
//			.put().uri("/retiro/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
//					+ operacion.getComision()).retrieve().bodyToMono(CurrentAccount.class).log();
	
	
	//consumir de la cuenta de banco
	//public Mono<CurrentAccount> retiroBancario(OperationCurrentAccount op) {
	public Mono<CurrentAccount> retiroBancario(String cuenta_origen,Double monto,Double comision,String codigo_bancario_destino) {
		
		log.info("Actualizando: cuenta origen --> retiro bancario : "+ cuenta_origen 
				+ " monto : " + monto + " comision : " + comision + " banco de destino " + codigo_bancario_destino);
		
		//.uri("/retiro/{numero_cuenta}/{monto}/{comision}")
		
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("numero_cuenta",cuenta_origen);
		pathVariable.put("monto",Double.toString(monto));//Casteamos la cantidad para envia en el map
		pathVariable.put("comision",Double.toString(comision));
		pathVariable.put("codigo_bancario",codigo_bancario_destino);
		
		return productoBancoClient
					.put()
				   .uri("/retiro/{numero_cuenta}/{monto}/{comision}/{codigo_bancario}",pathVariable)
				   .accept(MediaType.APPLICATION_JSON)
				   .contentType(MediaType.APPLICATION_JSON)
				   .retrieve()
				   .bodyToMono(CurrentAccount.class).log();		
		
	}
	
	
	//consumir de la cuenta de banco
	//public Mono<CurrentAccount> retiroBancario(OperationCurrentAccount op) {
	public Mono<CurrentAccount> despositoBancario(Double monto,String cuenta_origen,Double comision) {
		
		log.info("Actualizando: cuenta origen --> deposito bancario : "+ cuenta_origen + " monto : " + monto + " comision : " + comision);
		 
		//.uri("/retiro/{numero_cuenta}/{monto}/{comision}")
		
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("numero_Cuenta",cuenta_origen);
		pathVariable.put("monto",Double.toString(monto));//Casteamos la cantidad para envia en el map
		pathVariable.put("comision",Double.toString(comision));
		
		return productoBancoClient
					.put()
				   .uri("/deposito/{numero_Cuenta}/{monto}/{comision}",pathVariable)
				   .accept(MediaType.APPLICATION_JSON)
				   .contentType(MediaType.APPLICATION_JSON)
				   .retrieve()
				   .bodyToMono(CurrentAccount.class).log();		
		
	}
	
	
	
	
}
