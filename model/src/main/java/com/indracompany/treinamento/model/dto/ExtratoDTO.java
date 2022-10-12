package com.indracompany.treinamento.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExtratoDTO {
	
	private String agencia;
	
	private String numero;
	
	private LocalDateTime dataInicial;
	
	private LocalDateTime dataFinal;
}
