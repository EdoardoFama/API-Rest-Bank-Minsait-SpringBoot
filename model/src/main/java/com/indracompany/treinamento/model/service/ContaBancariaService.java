package com.indracompany.treinamento.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.indracompany.treinamento.exception.AplicacaoException;
import com.indracompany.treinamento.exception.ExceptionValidacoes;
import com.indracompany.treinamento.model.dto.ContaClienteDTO;
import com.indracompany.treinamento.model.dto.DepositoDTO;
import com.indracompany.treinamento.model.dto.SaqueDTO;
import com.indracompany.treinamento.model.dto.TransferenciaBancariaDTO;
import com.indracompany.treinamento.model.entity.ContaBancaria;
import com.indracompany.treinamento.model.entity.Operacao;
import com.indracompany.treinamento.model.repository.ContaBancariaRepository;
import com.indracompany.treinamento.model.service.Enum.TipoOperacao;
import com.indracompany.treinamento.util.CpfUtil;

@Service
public class ContaBancariaService extends GenericCrudService<ContaBancaria, Long, ContaBancariaRepository>{
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private OperacaoService operacaoService;
	
	@Autowired
	private ContaBancariaRepository contaBancariaRepository;
	
	public List<ContaClienteDTO> listarContasDoCliente(String cpf){
		
		boolean cpfValido = CpfUtil.validaCPF(cpf);
		if (!cpfValido) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_CPF_INVALIDO, cpf);
		}
		
		List<ContaBancaria> contasBancarias = contaBancariaRepository.findByClienteCpf(cpf);
		
		if (contasBancarias== null || contasBancarias.isEmpty()) {
			throw new AplicacaoException(ExceptionValidacoes.ALERTA_NENHUM_REGISTRO_ENCONTRADO);
		}
		
		List<ContaClienteDTO> listaRetornoDTO = new ArrayList<>();
		
		for (ContaBancaria conta : contasBancarias) {
			ContaClienteDTO dto = new ContaClienteDTO();
			BeanUtils.copyProperties(conta, dto);
			dto.setNomeCliente(conta.getCliente().getNome());
			listaRetornoDTO.add(dto);
		}
		
		return listaRetornoDTO;
	}
	
	public void salvarDeposito(DepositoDTO dto) {
		ContaBancaria contaBancaria = depositar(dto);
		
		Operacao operacao = new Operacao();
		operacao.setTipo(TipoOperacao.D);
		operacao.setDataCadastro(LocalDateTime.now());
		operacao.setValorOperacao(dto.getValor());
		operacao.setConta(contaBancaria);
		operacaoService.salvar(operacao);
		
		
	}

	private ContaBancaria depositar(DepositoDTO dto) {
		ContaBancaria contaBancaria = this.carregarConta(dto.getAgencia(), dto.getNumeroConta());
		contaBancaria.setSaldo(contaBancaria.getSaldo() + dto.getValor());
		super.salvar(contaBancaria);
		return contaBancaria;
	}
	
	public void salvarSaque(SaqueDTO dto) {
		ContaBancaria contaBancaria = sacar(dto);
		
		
		Operacao operacao = new Operacao();
		operacao.setTipo(TipoOperacao.S);
		operacao.setDataCadastro(LocalDateTime.now());
		operacao.setValorOperacao(dto.getValor());
		operacao.setConta(contaBancaria);
		operacaoService.salvar(operacao);
	}

	private ContaBancaria sacar(SaqueDTO dto) {
		ContaBancaria contaBancaria = this.carregarConta(dto.getAgencia(), dto.getNumeroConta());
		if (contaBancaria.getSaldo() < dto.getValor()) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_SALDO_INEXISTENTE);
		}
		contaBancaria.setSaldo(contaBancaria.getSaldo() - dto.getValor());
		super.salvar(contaBancaria);
		return contaBancaria;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void transferir(TransferenciaBancariaDTO transferenciaDto) {
		
		SaqueDTO saqueDto = new SaqueDTO();
		saqueDto.setAgencia(transferenciaDto.getAgenciaOrigem());
		saqueDto.setNumeroConta(transferenciaDto.getNumeroContaOrigem());
		saqueDto.setValor(transferenciaDto.getValor());
		

		ContaBancaria conta = this.sacar(saqueDto);
		
		DepositoDTO depositoDto = new DepositoDTO();
		depositoDto.setAgencia(transferenciaDto.getAgenciaDestino());
		depositoDto.setNumeroConta(transferenciaDto.getNumeroContaDestino());
		depositoDto.setValor(transferenciaDto.getValor());
		
		ContaBancaria contaDestino = this.depositar(depositoDto);
		
		Operacao operacao = new Operacao();
		operacao.setTipo(TipoOperacao.T);
		operacao.setDataCadastro(LocalDateTime.now());
		operacao.setValorOperacao(transferenciaDto.getValor());
		operacao.setConta(conta);
		operacao.setContaDestino(contaDestino);
		operacaoService.salvar(operacao);
		
	}
	
	public ContaBancaria carregarConta(String agencia, String numero) {
		ContaBancaria conta = contaBancariaRepository.findByAgenciaAndNumero(agencia, numero);
		
		if (conta == null) {
			throw new AplicacaoException(ExceptionValidacoes.ERRO_CONTA_INVALIDA);
		}
		
		return conta;
	}
	
	
	public List<Operacao> carregarExtrato(String agencia, String numero, LocalDateTime dataInicial, LocalDateTime dataFinal) {
		ContaBancaria conta = carregarConta(agencia, numero);
		return operacaoService.carregarOperacao(conta.getId(),dataInicial, dataFinal);
	}
	
	
	
}
