package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics; // Adicionado Graphics para o paint (embora paintComponent seja preferível)
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; // Importe KeyListener
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities; // Importar SwingUtilities

import application.GameData;
import application.ProjectRun; // Importando ProjectRun
import application.SaveLoadManager;

public class pauseMenu extends JFrame implements ActionListener, KeyListener {

    private JButton continuarButton;
    private JButton restartButton;
    private JButton saveButton;
    private JButton carregarJogoButton;
    private JButton opcoesButton;
    private JButton sairButton;
    private JPanel panel;
    private ProjectRun game; // Referência para a instância do jogo

    private static final long serialVersionUID = 1L;

    public pauseMenu(ProjectRun game) { // Construtor agora aceita a referência do jogo
        this.game = game; // Armazena a referência

        setTitle("Menu de Pausa");
        setSize(400, 450); // Aumentado para acomodar os botões adicionais
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // NÃO fechar automaticamente ao clicar no 'X'
        setLocationRelativeTo(null); // Centraliza a janela
        setResizable(false); // Torna o menu não redimensionável

        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10)); // 6 linhas, 1 coluna, espaçamento de 10px
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(50, 50, 50)); // Fundo escuro para o painel

        // Configuração e adição dos botões
        continuarButton = new JButton("Continuar");
        styleButton(continuarButton, new Color(60, 179, 113)); // Verde-azulado
        panel.add(continuarButton);

        restartButton = new JButton("Reiniciar Jogo");
        styleButton(restartButton, new Color(255, 140, 0)); // Laranja
        panel.add(restartButton);

        saveButton = new JButton("Salvar Jogo");
        styleButton(saveButton, new Color(70, 130, 180)); // Azul aço
        panel.add(saveButton);

        carregarJogoButton = new JButton("Carregar Jogo");
        styleButton(carregarJogoButton, new Color(100, 149, 237)); // Azul centáurea
        panel.add(carregarJogoButton);

        opcoesButton = new JButton("Opções");
        styleButton(opcoesButton, new Color(169, 169, 169)); // Cinza
        panel.add(opcoesButton);

        sairButton = new JButton("Sair do Jogo");
        styleButton(sairButton, new Color(220, 20, 60)); // Vermelho carmesim
        panel.add(sairButton);

        add(panel);
        addKeyListener(this); // Para capturar ESC para fechar
        setFocusable(true); // Permite que o menu capture eventos de teclado

        // Verifica se existe um arquivo de save para habilitar o botão "Carregar Jogo"
        File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            carregarJogoButton.setEnabled(false);
        }
    }

    // Método auxiliar para estilizar botões
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove o contorno de foco
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2, true)); // Borda arredondada
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == continuarButton) {
            this.dispose(); // Fecha o menu de pausa
            game.togglePause(); // Notifica ProjectRun para retomar o jogo
        } else if (e.getSource() == restartButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja reiniciar o jogo?", "Reiniciar Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Fecha o menu de pausa
                game.stopGame(); // Para a thread atual do jogo

                // Executa em SwingUtilities para garantir que a interface seja atualizada corretamente
                SwingUtilities.invokeLater(() -> {
                    // Fecha a janela atual do jogo
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(game);
                    if (parentFrame != null) {
                        parentFrame.dispose();
                    }
                    // Cria e inicia uma nova instância do jogo
                    JFrame newFrame = new JFrame("Jogo de Corrida");
                    ProjectRun newGame = new ProjectRun();
                    newFrame.add(newGame);
                    newFrame.pack();
                    newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    newFrame.setLocationRelativeTo(null);
                    newFrame.setVisible(true);
                    newGame.startGame();
                });
            }
        } else if (e.getSource() == saveButton) {
            game.saveGame();
        } else if (e.getSource() == carregarJogoButton) {
            GameData loadedData = SaveLoadManager.loadGame();
            if (loadedData != null) {
                JOptionPane.showMessageDialog(this, "Jogo carregado com sucesso!");
                this.dispose(); // Fecha o menu de pausa
                game.stopGame(); // Para a thread atual do jogo

                // Executa em SwingUtilities para garantir que a interface seja atualizada corretamente
                SwingUtilities.invokeLater(() -> {
                    // Fecha a janela atual do jogo
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(game);
                    if (parentFrame != null) {
                        parentFrame.dispose();
                    }
                    // Cria e inicia uma nova instância do jogo com os dados carregados
                    JFrame newFrame = new JFrame("Jogo de Corrida");
                    ProjectRun newGame = new ProjectRun();
                    newFrame.add(newGame);
                    newFrame.pack();
                    newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    newFrame.setLocationRelativeTo(null);
                    newGame.applyGameData(loadedData); // Aplica os dados carregados
                    newFrame.setVisible(true);
                    newGame.startGame();
                });
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
                carregarJogoButton.setEnabled(false); // Desabilita o botão se não houver save
            }
        } else if (e.getSource() == opcoesButton) {
            JOptionPane.showMessageDialog(this, "Funcionalidade de Opções ainda não implementada.", "Opções", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == sairButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja sair do jogo?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Encerra o programa
            }
        }
    }

    // Métodos KeyListener (para fechar o menu com ESC)
    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (this.isVisible()) {
                dispose(); // Fecha o menu de pausa
                game.togglePause(); // Notifica ProjectRun para retomar o jogo
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Não utilizado
    }

    // Método para renderizar o menu (opcional, para personalização avançada)
    // Usar paintComponent em um JPanel é geralmente preferível para desenho customizado em Swing.
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // Exemplo: Desenhar um título no menu
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        // g.drawString("PAUSADO", (getWidth() - g.getFontMetrics().stringWidth("PAUSADO")) / 2, 80);
    }
}
