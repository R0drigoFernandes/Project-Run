package entities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; // Importe KeyListener

import application.ProjectRun; // Importando ProjectRun
import application.SaveLoadManager;
import application.GameData; // Importando GameData

import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane; // Para mensagens de feedback
import java.awt.GridLayout;


public class pauseMenu extends JFrame implements ActionListener, KeyListener { // Implemente KeyListener

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
        setResizable(false); // Desativa o redimensionamento

        // Adiciona o KeyListener ao JFrame do menu de pausa
        this.addKeyListener(this);
        setFocusable(true); // Garante que o menu pode receber foco para eventos de teclado


        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10)); // 6 linhas, 1 coluna, espaçamento
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Margens internas
        panel.setBackground(Color.DARK_GRAY);

        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Color buttonColor = new Color(70, 130, 180);
        Color textColor = Color.WHITE;

        continuarButton = new JButton("Continuar");
        restartButton = new JButton("Reiniciar Jogo");
        saveButton = new JButton("Salvar Jogo");
        carregarJogoButton = new JButton("Carregar Jogo");
        opcoesButton = new JButton("Opções");
        sairButton = new JButton("Sair");

        JButton[] buttons = {continuarButton, restartButton, saveButton, carregarJogoButton, opcoesButton, sairButton};
        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFocusPainted(false);
            button.addActionListener(this);
            panel.add(button);
        }

        // Verificar se existe um jogo salvo para habilitar o botão de carregar
        File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
        carregarJogoButton.setEnabled(saveFile.exists());

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == continuarButton) {
            dispose(); // Fecha o menu de pausa
            game.togglePause(); // Retoma o jogo
        } else if (e.getSource() == restartButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja reiniciar o jogo?", "Reiniciar Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Fecha o menu de pausa
                game.gamereset(); // Reinicia o jogo
                game.togglePause(); // Despausa o jogo após o reset
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

    // Métodos KeyListener
    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            // Se P for pressionado enquanto o menu de pausa está ativo, ele o fecha
            if (this.isVisible()) {
                dispose();
                game.togglePause();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Não utilizado
    }

    // Remove o método render(Graphics g) daqui. JFrame se desenha automaticamente.
    // Se você precisa desenhar algo personalizado, faça isso em um JPanel dentro do JFrame.
}