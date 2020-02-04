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
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;
import spring.boot.webflu.ms.op.banco.app.dto.CuentaBanco;

@Service
public class ProductoBancoClient {

	private static final Logger log = LoggerFactory.getLogger(CuentaBanco.class);
	
	@Autowired
	@Qualifier("productoBanco")
	private WebClient productoBancoClient;
	
	
	public Mono<CuentaBanco> findByNumeroCuenta(String num,String codigo_bancario) {
		
		log.info("nuermo de cueta : "+ num + " codigo_bancario_destino : " + codigo_bancario);
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("num",num);
		pathVariable.put("codigo_bancario",codigo_bancario);
		
		return productoBancoClient.get()
				.uri("/numero_cuenta/{num}/{codigo_bancario}",pathVariable)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(CuentaBanco.class);
		    	
	}
	
	//consumir de la cuenta de banco
	//public Mono<CurrentAccount> retiroBancario(OperationCurrentAccount op) {
	public Mono<CuentaBanco> retiroBancario(String cuenta_origen,Double monto,Double comision,String codigo_bancario_destino) {
		
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
				   .bodyToMono(CuentaBanco.class).log();		
		
	}
	
	
	//consumir de la cuenta de banco
	public Mono<CuentaBanco> despositoBancario(Double monto,String cuenta_origen,Double comision,String codigo_bancario_destino) {
		
		log.info("Actualizando: cuenta origen --> deposito bancario : "+ cuenta_origen + " monto : " + monto + " comision : " + comision);
		 
		//.uri("/retiro/{numero_cuenta}/{monto}/{comision}")
		
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("numero_Cuenta",cuenta_origen);
		pathVariable.put("monto",Double.toString(monto));//Casteamos la cantidad para envia en el map
		pathVariable.put("comision",Double.toString(comision));
		pathVariable.put("codigo_bancario",codigo_bancario_destino);
		
		return productoBancoClient
					.put()
				   .uri("/deposito/{numero_Cuenta}/{monto}/{comision}/{codigo_bancario}",pathVariable)
				   .accept(MediaType.APPLICATION_JSON)
				   .contentType(MediaType.APPLICATION_JSON)
				   .retrieve()
				   .bodyToMono(CuentaBanco.class).log();		
		
	}
	
	
	
	
}
