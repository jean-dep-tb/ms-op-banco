package spring.boot.webflu.ms.op.banco.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import spring.boot.webflu.ms.op.banco.app.documents.TypeOperation;

public interface TipoOperacionDao extends ReactiveMongoRepository<TypeOperation, String> {

}
