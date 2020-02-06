package spring.boot.webflu.ms.op.banco.app.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CuentaBanco {
	
	private String id;
	private String dni;
	private String numeroCuenta;
	private TipoCuentaBanco tipoProducto;
	private String fecha_afiliacion;
	private String fecha_caducidad;
	private double saldo;
	private String usuario;
	private String clave;
	//
	private String codigoBanco;
	
}










