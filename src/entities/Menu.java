package entities;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

import application.ProjectRun;
import application.GameData;
import application.SaveLoadManager;
import java.io.File; // Importe para verificar a existência do arquivo de save

public class Menu extends JFrame implements ActionListener{
    /**
     *
     */
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

        // Criação do Painel
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 linhas, 1 coluna, espaçamento de 10px
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Margens internas
        panel.setBackground(Color.DARK_GRAY); // Cor de fundo do painel

        // Criação dos Botões
        novoJogoButton = new JButton("Novo Jogo");
        carregarJogoButton = new JButton("Carregar Jogo");
        opcoesButton = new JButton("Opções");
        sairButton = new JButton("Sair");

        // Estilização dos Botões
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Color buttonColor = new Color(70, 130, 180); // Azul aço
        Color textColor = Color.WHITE;

        novoJogoButton.setFont(buttonFont);
        novoJogoButton.setBackground(buttonColor);
        novoJogoButton.setForeground(textColor);
        novoJogoButton.setFocusPainted(false); // Remove a borda de foco

        carregarJogoButton.setFont(buttonFont);
        carregarJogoButton.setBackground(buttonColor);
        carregarJogoButton.setForeground(textColor);
        carregarJogoButton.setFocusPainted(false);

        opcoesButton.setFont(buttonFont);
        opcoesButton.setBackground(buttonColor);
        opcoesButton.setForeground(textColor);
        opcoesButton.setFocusPainted(false);

        sairButton.setFont(buttonFont);
        sairButton.setBackground(buttonColor);
        sairButton.setForeground(textColor);
        sairButton.setFocusPainted(false);

        // Adicionar Listeners aos Botões
        novoJogoButton.addActionListener(this);
        carregarJogoButton.addActionListener(this);
        opcoesButton.addActionListener(this);
        sairButton.addActionListener(this);

        // Verificar se existe um jogo salvo para habilitar o botão de carregar
        File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
        carregarJogoButton.setEnabled(saveFile.exists());


        // Adicionar botões ao painel
        panel.add(novoJogoButton);
        panel.add(carregarJogoButton);
        panel.add(opcoesButton);
        panel.add(sairButton);

        // Adicionar painel à janela
        add(panel);

        // Torna a janela visível
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == novoJogoButton) {
            ProjectRun game = new ProjectRun(); // Cria uma nova instância do jogo
            JFrame frame = new JFrame("Jogo de Corrida");
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.startGame(); // Chama o método para iniciar a thread do jogo
            this.dispose(); // Fecha a janela do menu
        } else if (e.getSource() == carregarJogoButton) {
            GameData loadedData = SaveLoadManager.loadGame();
            if (loadedData != null) {
                JOptionPane.showMessageDialog(this, "Jogo carregado com sucesso!");
                ProjectRun game = new ProjectRun(); // Cria uma nova instância do jogo
                JFrame frame = new JFrame("Jogo de Corrida");
                frame.add(game);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                game.applyGameData(loadedData); // Aplica os dados carregados
                game.startGame(); // Inicia o jogo com os dados carregados
                this.dispose(); // Fecha a janela do menu
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
                carregarJogoButton.setEnabled(false); // Desabilita o botão se não houver arquivo
            }
        } else if (e.getSource() == opcoesButton) {

            // Adicione a lógica para configurações do jogo aqui
        } else if (e.getSource() == sairButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja sair?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Sai da aplicação
            }
        }
    }
}