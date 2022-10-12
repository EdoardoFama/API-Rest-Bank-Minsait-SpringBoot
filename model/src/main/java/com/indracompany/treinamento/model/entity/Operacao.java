package com.indracompany.treinamento.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.indracompany.treinamento.model.service.Enum.TipoOperacao;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "edoardofama_operacoes")
@Data
@EqualsAndHashCode(callSuper = true)
public class Operacao extends GenericEntity<Long>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime dataCadastro;
	
	@Column
	@Enumerated(EnumType.STRING)
	private TipoOperacao tipo;
	
	private double valorOperacao;
	
	@ManyToOne
	@JoinColumn(name = "fk_conta_bancaria_id")
	private ContaBancaria conta;
	
	@ManyToOne
	@JoinColumn(name = "fk_conta_bancaria_destino_id")
	private ContaBancaria contaDestino;





}
