package com.indracompany.treinamento.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.indracompany.treinamento.model.entity.Operacao;

public interface OperacaoRepository extends GenericCrudRepository<Operacao, Long>{
	
	public List<Operacao> findByContaIdAndDataCadastroBetween(Long id, LocalDateTime dataInicial, LocalDateTime dataFinal);
	
}
