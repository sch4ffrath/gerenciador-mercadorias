package br.com.zerium.gerenciador.dao;

import br.com.zerium.gerenciador.model.Movimentacao;
import br.com.zerium.gerenciador.model.Produto;
import br.com.zerium.gerenciador.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private final MovimentacaoDAO movimentacaoDAO;

    public ProdutoDAO() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    public void adicionarProduto(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                    Movimentacao mov = new Movimentacao(produto.getId(), "ENTRADA", produto.getQuantidade(), "Estoque inicial");
                    movimentacaoDAO.registrarMovimentacao(mov);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarProduto(Produto produto) {
        Produto produtoAntigo = buscarPorId(produto.getId());
        if (produtoAntigo == null) return;
        int quantidadeAntiga = produtoAntigo.getQuantidade();

        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());
            stmt.setInt(5, produto.getId());
            stmt.executeUpdate();

            int quantidadeNova = produto.getQuantidade();
            if (quantidadeNova > quantidadeAntiga) {
                int diferenca = quantidadeNova - quantidadeAntiga;
                Movimentacao mov = new Movimentacao(produto.getId(), "ENTRADA", diferenca, "Ajuste de estoque (aumento)");
                movimentacaoDAO.registrarMovimentacao(mov);
            } else if (quantidadeNova < quantidadeAntiga) {
                int diferenca = quantidadeAntiga - quantidadeNova;
                Movimentacao mov = new Movimentacao(produto.getId(), "SAIDA", diferenca, "Ajuste de estoque (baixa)");
                movimentacaoDAO.registrarMovimentacao(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registrarVenda(int produtoId, int quantidadeVendida, String observacao) throws Exception {
        Produto produto = buscarPorId(produtoId);
        if (produto == null) {
            throw new Exception("Produto não encontrado.");
        }
        if (produto.getQuantidade() < quantidadeVendida) {
            throw new Exception("Estoque insuficiente. Disponível: " + produto.getQuantidade());
        }

        int novaQuantidade = produto.getQuantidade() - quantidadeVendida;
        String sqlUpdate = "UPDATE produtos SET quantidade = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, produtoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar o estoque do produto.", e);
        }

        Movimentacao mov = new Movimentacao(produtoId, "SAIDA", quantidadeVendida, observacao);
        movimentacaoDAO.registrarMovimentacao(mov);
    }

    public void removerProduto(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome ASC";
        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade")
                );
                produto.setId(rs.getInt("id"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Produto produto = new Produto(
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade")
                    );
                    produto.setId(rs.getInt("id"));
                    return produto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}