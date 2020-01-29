package spring.boot.webflu.ms.op.banco.app.documents;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection ="Operaciones")
public class OperationCurrentAccount {

	@NotEmpty
	private String dni;
	@NotEmpty
	private String cuenta_origen;
	@NotEmpty
	private String cuenta_destino;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaOperacion;
	@NotEmpty
	private TypeOperation tipoOperacion;
	@NotEmpty
	private double montoPago;
	
	private Double comision = 0.0;

	public OperationCurrentAccount() {
		
	}

	public OperationCurrentAccount(String dni, String cuenta_origen,
			String cuenta_destino, Date fechaOperacion,TypeOperation tipoOperacion,
			double montoPago, Double comision) {
		this.dni = dni;
		this.cuenta_origen = cuenta_origen;
		this.cuenta_destino = cuenta_destino;
		this.fechaOperacion = fechaOperacion;
		this.tipoOperacion = tipoOperacion;
		this.montoPago = montoPago;
		this.comision = comision;
	}
		
	//private tipoProducto tipoCliente;
	
	
	
	
}










