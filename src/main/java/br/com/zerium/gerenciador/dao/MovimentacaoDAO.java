package br.com.zerium.gerenciador.dao;

import br.com.zerium.gerenciador.model.Movimentacao;
import br.com.zerium.gerenciador.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    public void registrarMovimentacao(Movimentacao movimentacao) {
        String sql = "INSERT INTO movimentacoes (produto_id, tipo, quantidade_movida, observacao) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movimentacao.getProdutoId());
            stmt.setString(2, movimentacao.getTipo());
            stmt.setInt(3, movimentacao.getQuantidadeMovida());
            stmt.setString(4, movimentacao.getObservacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Movimentacao> listarTodas() {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.*, p.nome AS nome_produto FROM movimentacoes m " +
                "JOIN produtos p ON m.produto_id = p.id ORDER BY m.data_movimentacao DESC";

        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Movimentacao mov = new Movimentacao(
                        rs.getInt("produto_id"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade_movida"),
                        rs.getString("observacao")
                );
                mov.setId(rs.getInt("id"));
                mov.setDataMovimentacao(rs.getTimestamp("data_movimentacao"));
                mov.setNomeProduto(rs.getString("nome_produto"));
                movimentacoes.add(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movimentacoes;
    }


    public List<Movimentacao> listarVendas() {
        List<Movimentacao> vendas = new ArrayList<>();
        // SQL com JOIN e WHERE para buscar apenas as saídas, ordenadas pela mais recente
        String sql = "SELECT m.*, p.nome AS nome_produto FROM movimentacoes m " +
                "JOIN produtos p ON m.produto_id = p.id " +
                "WHERE m.tipo = 'SAIDA' ORDER BY m.data_movimentacao DESC";

        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movimentacao mov = new Movimentacao(
                        rs.getInt("produto_id"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade_movida"),
                        rs.getString("observacao")
                );
                mov.setId(rs.getInt("id"));
                mov.setDataMovimentacao(rs.getTimestamp("data_movimentacao"));
                mov.setNomeProduto(rs.getString("nome_produto"));
                vendas.add(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vendas;
    }
}