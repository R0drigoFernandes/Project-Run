package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File; // Importe para verificar a existência do arquivo de save

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities; // Importar SwingUtilities

import application.GameData;
import application.ProjectRun;
import application.SaveLoadManager;

public class Menu extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JButton novoJogoButton;
    private JButton carregarJogoButton;
    private JButton opcoesButton;
    private JButton sairButton;
    private JPanel panel;

    public Menu() {
        // Configurações da Janela
        setTitle("MENU DO JOGO");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setResizable(false); // Torna o menu não redimensionável

        // Criação do Painel
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 linhas, 1 coluna, espaçamento de 10px
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(50, 50, 50)); // Fundo escuro para o painel

        // Criação e estilização dos botões
        novoJogoButton = new JButton("Novo Jogo");
        styleButton(novoJogoButton, new Color(46, 139, 87)); // Verde-mar
        panel.add(novoJogoButton);

        carregarJogoButton = new JButton("Carregar Jogo");
        styleButton(carregarJogoButton, new Color(70, 130, 180)); // Azul aço
        panel.add(carregarJogoButton);

        opcoesButton = new JButton("Opções");
        styleButton(opcoesButton, new Color(169, 169, 169)); // Cinza
        panel.add(opcoesButton);

        sairButton = new JButton("Sair");
        styleButton(sairButton, new Color(220, 20, 60)); // Vermelho carmesim
        panel.add(sairButton);

        add(panel); // Adiciona o painel à janela

        // Verifica a existência do arquivo de save para habilitar/desabilitar o botão de carregar
        File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            carregarJogoButton.setEnabled(false);
        }
    }

    // Método auxiliar para estilizar botões
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove o contorno de foco
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2, true)); // Borda arredondada
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == novoJogoButton) {
            final JFrame frame = new JFrame("Jogo de Corrida");
            final ProjectRun game = new ProjectRun();
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            // Garante que o jogo só inicie APÓS o frame estar visível
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.setVisible(true); // Torna o JFrame visível
                    game.startGame();       // Inicia o jogo
                }
            });
            this.dispose(); // Fecha a janela do menu
        } else if (e.getSource() == carregarJogoButton) {
            GameData loadedData = SaveLoadManager.loadGame();
            if (loadedData != null) {
                JOptionPane.showMessageDialog(this, "Jogo carregado com sucesso!");

                final JFrame frame = new JFrame("Jogo de Corrida");
                final ProjectRun game = new ProjectRun();
                frame.add(game);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        frame.setVisible(true); // Torna o JFrame visível
                        game.applyGameData(loadedData); // Aplica os dados carregados
                        game.startGame(); // Inicia o jogo
                    }
                });
                this.dispose(); // Fecha a janela do menu
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
                carregarJogoButton.setEnabled(false); // Desabilita o botão se não houver save
            }
        } else if (e.getSource() == opcoesButton) {
            JOptionPane.showMessageDialog(this, "Funcionalidade de Opções ainda não implementada.", "Opções", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == sairButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja sair?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Exit the application
            }
        }
    }
}
