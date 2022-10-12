package com.indracompany.treinamento.model.service.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoOperacao {

	S("Saque"), T("Transferência"), D("Depósito");
	
	private String valor;
	
}
