package entities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import application.ProjectRun; // Importando ProjectRun
import application.SaveLoadManager;
import application.GameData; // Importando GameData

import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane; // Para mensagens de feedback


public class pauseMenu extends JFrame implements ActionListener {

    private JButton continuarButton;
    private JButton restartButton;
    private JButton saveButton;
    private JButton carregarJogoButton;
    private JButton opcoesButton;
    private JButton sairButton;
    private JPanel panel;
    private ProjectRun game; // Referência para a instância do jogo

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public pauseMenu(ProjectRun game) { // Construtor agora aceita a referência do jogo
        this.game = game; // Armazena a referência

        setTitle("Menu de Pausa");
        setSize(400, 450); // Aumentado para acomodar os botões
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Não fechar o jogo inteiro
        setLocationRelativeTo(null);
        setResizable(false); // Desabilita o redimensionamento da janela

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(Color.DARK_GRAY);

        continuarButton = new JButton("Continuar");
        restartButton = new JButton("Reiniciar");
        saveButton = new JButton("Salvar Jogo");
        carregarJogoButton = new JButton("Carregar Jogo");
        opcoesButton = new JButton("Opções");
        sairButton = new JButton("Sair");

        Font buttonFont = new Font("Arial", Font.BOLD, 18); // Fonte maior
        Color buttonBg = new Color(70, 130, 180); // SteelBlue
        Color buttonFg = Color.WHITE;

        int buttonWidth = 200;
        int buttonHeight = 40;
        int spacing = 15;
        int startY = 30;
        int centerX = (getWidth() - buttonWidth) / 2; // Centraliza os botões

        setupButton(continuarButton, buttonFont, buttonBg, buttonFg, centerX, startY, buttonWidth, buttonHeight);
        setupButton(restartButton, buttonFont, buttonBg, buttonFg, centerX, startY + (buttonHeight + spacing), buttonWidth, buttonHeight);
        setupButton(saveButton, buttonFont, buttonBg, buttonFg, centerX, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        setupButton(carregarJogoButton, buttonFont, buttonBg, buttonFg, centerX, startY + 3 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        setupButton(opcoesButton, buttonFont, buttonBg, buttonFg, centerX, startY + 4 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        setupButton(sairButton, buttonFont, buttonBg, buttonFg, centerX, startY + 5 * (buttonHeight + spacing), buttonWidth, buttonHeight);


        continuarButton.addActionListener(this);
        restartButton.addActionListener(this);
        saveButton.addActionListener(this);
        carregarJogoButton.addActionListener(this);
        opcoesButton.addActionListener(this);
        sairButton.addActionListener(this);

        panel.add(continuarButton);
        panel.add(restartButton);
        panel.add(saveButton);
        panel.add(carregarJogoButton);
        panel.add(opcoesButton);
        panel.add(sairButton);

        File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
        carregarJogoButton.setEnabled(saveFile.exists());

        add(panel);
        setVisible(true);

    }

    private void setupButton(JButton button, Font font, Color bgColor, Color fgColor, int x, int y, int width, int height) {
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        button.setBounds(x, y, width, height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == continuarButton) {
            dispose(); // Fecha o menu de pausa
            game.togglePause(); // Retoma o jogo
        } else if (e.getSource() == restartButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja reiniciar o jogo?", "Reiniciar Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                game.gamereset(); // Reinicia o jogo através da referência
                dispose(); // Fecha o menu de pausa
            }
        } else if (e.getSource() == saveButton) {
            SaveLoadManager.saveGame(game.getGameData()); // Salva o estado atual do jogo
            JOptionPane.showMessageDialog(this, "Jogo Salvo!");
            File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
            carregarJogoButton.setEnabled(saveFile.exists());
        } else if (e.getSource() == carregarJogoButton) {
            GameData loadedData = SaveLoadManager.loadGame(); // Carrega os dados do jogo
            if (loadedData != null) {
                game.applyGameData(loadedData); // Aplica os dados carregados ao jogo
                JOptionPane.showMessageDialog(this, "Jogo Carregado!");
                dispose(); // Fecha o menu de pausa
                game.togglePause(); // Retoma o jogo
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
                carregarJogoButton.setEnabled(false); // Desabilita o botão se não houver save
            }
        } else if (e.getSource() == opcoesButton) {
            JOptionPane.showMessageDialog(this, "Funcionalidade de Opções ainda não implementada.");
        } else if (e.getSource() == sairButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja sair do jogo?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Encerra o programa
            }
        }
    }
}