package spring.boot.webflu.ms.op.banco.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.op.banco.app.dao.TipoOperacionBancoDao;
import spring.boot.webflu.ms.op.banco.app.documents.TipoOperacionBanco;
import spring.boot.webflu.ms.op.banco.app.service.TipoOperacionBancoService;

@Service
public class TipoOperacionBancoServiceImpl implements TipoOperacionBancoService{
	
	@Autowired
	public TipoOperacionBancoDao  tipoProductoDao;
	
	@Override
	public Flux<TipoOperacionBanco> findAllTipoproducto()
	{
	return tipoProductoDao.findAll();
	
	}
	@Override
	public Mono<TipoOperacionBanco> findByIdTipoProducto(String id)
	{
	return tipoProductoDao.findById(id);
	
	}
	
	@Override
	public Mono<TipoOperacionBanco> saveTipoProducto(TipoOperacionBanco tipoCliente)
	{
	return tipoProductoDao.save(tipoCliente);
	}
	
	@Override
	public Mono<Void> deleteTipo(TipoOperacionBanco tipoProducto) {
		return tipoProductoDao.delete(tipoProducto);
	}
	
}
