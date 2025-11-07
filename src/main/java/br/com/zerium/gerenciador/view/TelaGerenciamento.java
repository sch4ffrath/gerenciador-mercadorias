package br.com.zerium.gerenciador.view;

import br.com.zerium.gerenciador.dao.MovimentacaoDAO;
import br.com.zerium.gerenciador.dao.ProdutoDAO;
import br.com.zerium.gerenciador.model.Movimentacao;
import br.com.zerium.gerenciador.model.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaGerenciamento extends JFrame {

    private final ProdutoDAO produtoDAO;
    private final MovimentacaoDAO movimentacaoDAO;
    private Integer produtoSelecionadoId = null;

    // Componentes da Aba de Estoque
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabelaProdutos;
    private JButton botaoSalvarEstoque, botaoDeletarEstoque, botaoLimparForm;
    private JTextField campoNome, campoDescricao, campoPreco, campoQuantidade;

    // Componentes da Aba de Vendas
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabelaVendas;
    private JComboBox<Produto> comboProdutosVenda;
    private JSpinner spinnerQuantidadeVenda;
    private JTextField campoObservacaoVenda;

    // Componentes da Aba de Relatório
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabelaRelatorio;

    // Cores
    private final Color COR_VERDE = new Color(39, 174, 96);
    private final Color COR_VERMELHO = new Color(192, 57, 43);
    private final Color COR_AZUL = new Color(41, 128, 185);
    private final Color COR_CINZA = new Color(127, 140, 141);

    public TelaGerenciamento() {
        this.produtoDAO = new ProdutoDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();

        configurarJanela();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.addTab("Gerenciar Estoque", criarPainelEstoque());
        tabbedPane.addTab("Registrar Venda", criarPainelVendas());
        tabbedPane.addTab("Relatório de Movimentações", criarPainelRelatorio());

        add(tabbedPane, BorderLayout.CENTER);
        atualizarDados();
    }

    private void configurarJanela() {
        setTitle("Sistema de Gerenciamento de Mercadorias");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private JPanel criarPainelEstoque() {
        JPanel painelEstoque = new JPanel(new BorderLayout(15, 15));
        painelEstoque.setBorder(new EmptyBorder(15, 15, 15, 15));

        modeloTabelaProdutos = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrição", "Preço", "Qtd."}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(modeloTabelaProdutos);
        configurarTabela(tabelaProdutos);
        painelEstoque.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        JPanel painelFormulario = criarFormularioEstoque();
        painelEstoque.add(painelFormulario, BorderLayout.SOUTH);
        return painelEstoque;
    }

    private JPanel criarFormularioEstoque() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados do Produto",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; campoNome = new JTextField(20); painelFormulario.add(campoNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoDescricao = new JTextField(); painelFormulario.add(campoDescricao, gbc);
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Preço (ex: 99.90):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoPreco = new JTextField(); painelFormulario.add(campoPreco, gbc);
        gbc.gridx = 0; gbc.gridy = 3; painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; campoQuantidade = new JTextField(); painelFormulario.add(campoQuantidade, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botaoSalvarEstoque = personalizarBotao(new JButton(), "Salvar", COR_VERDE);
        botaoDeletarEstoque = personalizarBotao(new JButton(), "Deletar", COR_VERMELHO);
        botaoLimparForm = personalizarBotao(new JButton(), "Limpar", COR_CINZA);
        painelBotoes.add(botaoLimparForm);
        painelBotoes.add(botaoDeletarEstoque);
        painelBotoes.add(botaoSalvarEstoque);

        gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(15, 5, 5, 5);
        painelFormulario.add(painelBotoes, gbc);

        adicionarListenersEstoque();
        return painelFormulario;
    }

    private JPanel criarPainelVendas() {
        JPanel painelVendas = new JPanel(new BorderLayout(15, 15));
        painelVendas.setBorder(new EmptyBorder(15, 15, 15, 15));

        modeloTabelaVendas = new DefaultTableModel(new Object[]{"Data", "Produto Vendido", "Qtd.", "Observação"}, 0);
        tabelaVendas = new JTable(modeloTabelaVendas);
        configurarTabela(tabelaVendas);
        painelVendas.add(new JScrollPane(tabelaVendas), BorderLayout.CENTER);

        JPanel painelFormulario = criarFormularioVenda();
        painelVendas.add(painelFormulario, BorderLayout.SOUTH);

        return painelVendas;
    }

    private JPanel criarFormularioVenda() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registrar Nova Venda",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painelFormulario.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; comboProdutosVenda = new JComboBox<>(); painelFormulario.add(comboProdutosVenda, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; spinnerQuantidadeVenda = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1)); painelFormulario.add(spinnerQuantidadeVenda, gbc);
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Observação:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoObservacaoVenda = new JTextField(); painelFormulario.add(campoObservacaoVenda, gbc);

        JButton botaoRegistrarVenda = personalizarBotao(new JButton(), "Registrar Venda", COR_AZUL);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 5, 5, 5);
        painelFormulario.add(botaoRegistrarVenda, gbc);

        botaoRegistrarVenda.addActionListener(e -> registrarVenda());
        return painelFormulario;
    }

    private JPanel criarPainelRelatorio() {
        JPanel painelRelatorio = new JPanel(new BorderLayout(10, 10));
        painelRelatorio.setBorder(new EmptyBorder(15, 15, 15, 15));

        modeloTabelaRelatorio = new DefaultTableModel(new Object[]{"Data", "Produto", "Tipo", "Qtd. Movida", "Observação"}, 0);
        tabelaRelatorio = new JTable(modeloTabelaRelatorio);
        configurarTabela(tabelaRelatorio);
        painelRelatorio.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);

        JButton botaoAtualizarRelatorio = personalizarBotao(new JButton(), "Atualizar Relatório", COR_AZUL);
        botaoAtualizarRelatorio.addActionListener(e -> carregarDadosRelatorio());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.add(botaoAtualizarRelatorio);

        painelRelatorio.add(painelBotao, BorderLayout.NORTH);
        return painelRelatorio;
    }

    private void adicionarListenersEstoque() {
        botaoSalvarEstoque.addActionListener(e -> salvarOuAtualizarProduto());
        botaoDeletarEstoque.addActionListener(e -> deletarProduto());
        botaoLimparForm.addActionListener(e -> limparFormularioEstoque());
        tabelaProdutos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) carregarProdutoParaEdicao();
            }
        });
    }

    private void salvarOuAtualizarProduto() {
        String nome = campoNome.getText();
        String precoStr = campoPreco.getText();
        String qtdStr = campoQuantidade.getText();
        if (nome.trim().isEmpty() || precoStr.trim().isEmpty() || qtdStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Preço e Quantidade são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double preco = Double.parseDouble(precoStr.replace(",", "."));
            int quantidade = Integer.parseInt(qtdStr);
            Produto produto = new Produto(nome, campoDescricao.getText(), preco, quantidade);

            if (produtoSelecionadoId == null) {
                produtoDAO.adicionarProduto(produto);
            } else {
                produto.setId(produtoSelecionadoId);
                produtoDAO.atualizarProduto(produto);
            }
            limparFormularioEstoque();
            atualizarDados();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e Quantidade devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarVenda() {
        Produto produtoSelecionado = (Produto) comboProdutosVenda.getSelectedItem();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            produtoDAO.registrarVenda(produtoSelecionado.getId(), (int) spinnerQuantidadeVenda.getValue(), campoObservacaoVenda.getText());
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            spinnerQuantidadeVenda.setValue(1);
            campoObservacaoVenda.setText("");
            atualizarDados();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Registrar Venda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idProduto = (int) modeloTabelaProdutos.getValueAt(linhaSelecionada, 0);
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar este produto?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao == JOptionPane.YES_OPTION) {
            produtoDAO.removerProduto(idProduto);
            limparFormularioEstoque();
            atualizarDados();
        }
    }

    private void carregarProdutoParaEdicao() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) return;
        produtoSelecionadoId = (Integer) modeloTabelaProdutos.getValueAt(linha, 0);
        Produto p = produtoDAO.buscarPorId(produtoSelecionadoId);
        if (p != null) {
            campoNome.setText(p.getNome());
            campoDescricao.setText(p.getDescricao());
            campoPreco.setText(String.format("%.2f", p.getPreco()).replace(",", "."));
            campoQuantidade.setText(String.valueOf(p.getQuantidade()));
            botaoSalvarEstoque.setText("Atualizar");
        }
    }

    private void carregarDadosVendas() {
        modeloTabelaVendas.setRowCount(0);
        List<Movimentacao> vendas = movimentacaoDAO.listarVendas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Movimentacao venda : vendas) {
            modeloTabelaVendas.addRow(new Object[]{
                    sdf.format(venda.getDataMovimentacao()),
                    venda.getNomeProduto(),
                    "-" + venda.getQuantidadeMovida(),
                    venda.getObservacao()
            });
        }
    }

    private void carregarDadosRelatorio() {
        modeloTabelaRelatorio.setRowCount(0);
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Movimentacao mov : movimentacoes) {
            modeloTabelaRelatorio.addRow(new Object[]{
                    sdf.format(mov.getDataMovimentacao()),
                    mov.getNomeProduto(),
                    mov.getTipo(),
                    mov.getTipo().equals("ENTRADA") ? "+" + mov.getQuantidadeMovida() : "-" + mov.getQuantidadeMovida(),
                    mov.getObservacao()
            });
        }
    }

    private void atualizarDados() {
        List<Produto> produtos = produtoDAO.listarProdutos();
        modeloTabelaProdutos.setRowCount(0);
        for (Produto p : produtos) {
            modeloTabelaProdutos.addRow(new Object[]{p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getQuantidade()});
        }

        Produto selecionado = (Produto) comboProdutosVenda.getSelectedItem();
        comboProdutosVenda.removeAllItems();
        for (Produto p : produtos) {
            comboProdutosVenda.addItem(p);
        }
        if (selecionado != null) {
            for (int i = 0; i < comboProdutosVenda.getItemCount(); i++) {
                if (comboProdutosVenda.getItemAt(i).getId() == selecionado.getId()) {
                    comboProdutosVenda.setSelectedIndex(i);
                    break;
                }
            }
        }

        carregarDadosVendas();
        carregarDadosRelatorio();
    }

    private void limparFormularioEstoque() {
        campoNome.setText("");
        campoDescricao.setText("");
        campoPreco.setText("");
        campoQuantidade.setText("");
        produtoSelecionadoId = null;
        botaoSalvarEstoque.setText("Salvar");
        tabelaProdutos.clearSelection();
        campoNome.requestFocusInWindow();
    }

    private void configurarTabela(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(false);
        header.setBackground(new Color(69, 73, 74));
        header.setForeground(Color.WHITE);
    }

    private JButton personalizarBotao(JButton botao, String texto, Color cor) {
        botao.setText(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.putClientProperty("JButton.buttonType", "roundRect");
        return botao;
    }
}