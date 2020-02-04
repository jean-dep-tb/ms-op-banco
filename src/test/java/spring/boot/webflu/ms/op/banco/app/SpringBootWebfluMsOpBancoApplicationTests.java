package spring.boot.webflu.ms.op.banco.app;

import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.OperacionCuentaBanco;
import spring.boot.webflu.ms.op.banco.app.documents.TipoOperacionBanco;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluMsOpBancoApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void listarCuentaBanco() {
		client.get().uri("/api/operacionBancaria")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk() 
		.expectHeader().contentType(MediaType.APPLICATION_JSON) //.hasSize(2);
		.expectBodyList(OperacionCuentaBanco.class).consumeWith(response -> {
			
			List<OperacionCuentaBanco> opBancario = response.getResponseBody();
			
			opBancario.forEach(p -> {
				System.out.println(p.getCodigo_bancario_origen());
			});
			
			Assertions.assertThat(opBancario.size() > 0).isTrue();
		});
	}
	
	@Test
	public void crearCuentaBanco() {
		
		TipoOperacionBanco tict = new TipoOperacionBanco();
		tict.setIdTipo("1");
		tict.setDescripcion("retiro");
		
		OperacionCuentaBanco opBanco = new OperacionCuentaBanco();
		opBanco.setDni("47305710");
		opBanco.setCuenta_origen("900001");
		opBanco.setCuenta_destino("900002");		
		opBanco.setTipoOperacion(tict);
		opBanco.setMontoPago(3000.0);
		
		client.post()
		.uri("/api/operacionBancaria")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(opBanco), OperacionCuentaBanco.class)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(OperacionCuentaBanco.class)
		.consumeWith(response -> {
			OperacionCuentaBanco b = response.getResponseBody();
			Assertions.assertThat(b.getDni()).isNotEmpty().isEqualTo("47305710");
			Assertions.assertThat(b.getCuenta_origen()).isNotEmpty().isEqualTo("900001");
			Assertions.assertThat(b.getCuenta_destino()).isNotEmpty().isEqualTo("900002");
			Assertions.assertThat(b.getTipoOperacion().getDescripcion()).isNotEmpty().isEqualTo("retiro");
			Assertions.assertThat(b.getMontoPago()).isEqualTo(3000.0);
		});
	}
	

}
