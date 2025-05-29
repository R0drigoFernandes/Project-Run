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
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Margem interna
        panel.setBackground(Color.DARK_GRAY); // Cor de fundo do painel

        // Criação dos Botões
        novoJogoButton = new JButton("Novo Jogo");
        carregarJogoButton = new JButton("Carregar Jogo");
        opcoesButton = new JButton("Opções");
        sairButton = new JButton("Sair");

        // Configuração dos Botões
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Color buttonBg = new Color(70, 130, 180); // SteelBlue
        Color buttonFg = Color.WHITE;

        setupButton(novoJogoButton, buttonFont, buttonBg, buttonFg);
        setupButton(carregarJogoButton, buttonFont, buttonBg, buttonFg);
        setupButton(opcoesButton, buttonFont, buttonBg, buttonFg);
        setupButton(sairButton, buttonFont, buttonBg, buttonFg);

        // Adiciona ActionListeners
        novoJogoButton.addActionListener(this);
        carregarJogoButton.addActionListener(this);
        opcoesButton.addActionListener(this);
        sairButton.addActionListener(this);

        // Adiciona os botões ao painel
        panel.add(novoJogoButton);
        panel.add(carregarJogoButton);
        panel.add(opcoesButton);
        panel.add(sairButton);

        // Verifica se existe um arquivo de save para habilitar o botão de carregar
        File saveFile = new File(SaveLoadManager.SAVE_FILE_NAME);
        carregarJogoButton.setEnabled(saveFile.exists());

        // Adiciona o painel à janela
        add(panel);
        setVisible(true); // Torna a janela visível
    }

    private void setupButton(JButton button, Font font, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false); // Remove o foco da borda
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == novoJogoButton) {
            JOptionPane.showMessageDialog(this, "Iniciando um novo jogo...");
            ProjectRun game = new ProjectRun(); // Cria uma nova instância do jogo
            game.startGame(); // Chama o método para iniciar a janela do jogo e a thread
            this.dispose(); // Fecha a janela do menu
        } else if (e.getSource() == carregarJogoButton) {
            GameData loadedData = SaveLoadManager.loadGame();
            if (loadedData != null) {
                JOptionPane.showMessageDialog(this, "Jogo carregado com sucesso!");
                ProjectRun game = new ProjectRun(); // Cria uma nova instância do jogo
                game.applyGameData(loadedData); // Aplica os dados carregados
                game.startGame(); // Inicia o jogo com os dados carregados
                this.dispose(); // Fecha a janela do menu
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
                carregarJogoButton.setEnabled(false); // Desabilita o botão se não houver arquivo
            }
        } else if (e.getSource() == opcoesButton) {
            JOptionPane.showMessageDialog(this, "Acessando opções do jogo...");
            // Adicione a lógica para configurações do jogo aqui
        } else if (e.getSource() == sairButton) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja sair?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Sai da aplicação
            }
        }
    }
}