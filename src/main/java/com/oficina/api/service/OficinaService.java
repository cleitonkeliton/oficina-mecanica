package com.oficina.api.service;

import com.oficina.api.model.Cliente;
import com.oficina.api.model.Veiculo;
import com.oficina.database.ClienteDAO;
import com.oficina.database.VeiculoDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OficinaService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    public List<Cliente> listarClientes() {
        return clienteDAO.listarClientes();
    }

    public List<Veiculo> listarVeiculos() {
        return veiculoDAO.listarVeiculosComClientes();
    }
}
