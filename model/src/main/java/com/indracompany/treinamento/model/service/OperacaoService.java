package com.indracompany.treinamento.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.treinamento.exception.AplicacaoException;
import com.indracompany.treinamento.exception.ExceptionValidacoes;
import com.indracompany.treinamento.model.entity.Operacao;
import com.indracompany.treinamento.model.repository.OperacaoRepository;



@Service
public class OperacaoService extends GenericCrudService<Operacao, Long, OperacaoRepository>{
	
	@Autowired
	private OperacaoRepository operacaoRepository;

	public List<Operacao> carregarOperacao(Long id, LocalDateTime dataInicial, LocalDateTime dataFinal) {
		List<Operacao> operacao = operacaoRepository.findByContaIdAndDataCadastroBetween(id, dataInicial, dataFinal);
		
		if (operacao == null) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_ID_INVALIDO);
		}
		
		return operacao;
		
	}

}


