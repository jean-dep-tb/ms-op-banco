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
import spring.boot.webflu.ms.op.banco.app.dto.CurrentAccount;

@Service
public class ProductoBancoCreditoClient {

	private static final Logger log = LoggerFactory.getLogger(CurrentAccount.class);
	
	@Autowired
	@Qualifier("productoBancoCredito")
	private WebClient productoBancoCreditoClient;
	
//	Mono<CurrentAccount> oper3 = WebClient.builder().baseUrl("http://gateway:8099/producto_bancario/api/ProductoCredito/")
//			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
//			.put().uri("/pago/" + operacion.getCuenta_destino() + "/" + operacion.getMontoPago())
//			.retrieve().bodyToMono(CurrentAccount.class).log();
	
	//producto credito
	public Mono<CurrentAccount> despositoBancario(Double monto,String cuenta_destino,String codigo_bancario_destino) {
		
		Map<String, String> pathVariable = new HashMap<String,String>();
		pathVariable.put("monto",Double.toString(monto));
		pathVariable.put("numero_cuenta",cuenta_destino);
		pathVariable.put("codigo_bancario",codigo_bancario_destino);
		
		log.info("Actualizando: cuenta origen ---> deposito-credito: "+ cuenta_destino," monto : " + monto + "banco destino" + codigo_bancario_destino);	
		
		return productoBancoCreditoClient.put()
				   .uri("/pago/{numero_cuenta}/{monto}/{codigo_bancario}",pathVariable)
				   .accept(MediaType.APPLICATION_JSON)
				   .contentType(MediaType.APPLICATION_JSON)
				   .retrieve()
				   .bodyToMono(CurrentAccount.class);		
	}
	
}
