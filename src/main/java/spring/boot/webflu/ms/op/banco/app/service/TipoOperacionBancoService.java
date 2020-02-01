package spring.boot.webflu.ms.op.banco.app.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.documents.TipoOperacionBanco;

public interface TipoOperacionBancoService {
	
	Flux<TipoOperacionBanco> findAllTipoproducto();
	Mono<TipoOperacionBanco> findByIdTipoProducto(String id);
	Mono<TipoOperacionBanco> saveTipoProducto(TipoOperacionBanco tipoProducto);
	Mono<Void> deleteTipo(TipoOperacionBanco tipoProducto);
	
}
