package br.com.zerium.gerenciador.app; // Verifique o nome do seu pacote

import br.com.zerium.gerenciador.view.TelaGerenciamento;
import com.formdev.flatlaf.FlatLightLaf; // <<< MUDANÇA 1: Importa o tema claro padrão
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Aplica o tema FlatLaf Light (tema claro padrão)
            UIManager.setLookAndFeel(new FlatLightLaf()); // <<< MUDANÇA 2: Usa o novo tema
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Falha ao carregar o tema FlatLaf.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TelaGerenciamento().setVisible(true));
    }
}