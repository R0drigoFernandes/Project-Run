package application;

import java.io.*;

public class SaveLoadManager {

    public static final String SAVE_FILE_NAME = "savegame.dat";

    public static void saveGame(GameData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            oos.writeObject(data);
            System.out.println("Jogo salvo com sucesso em: " + SAVE_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    public static GameData loadGame() {
        File saveFile = new File(SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            System.out.println("Nenhum jogo salvo encontrado.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            GameData data = (GameData) ois.readObject();
            System.out.println("Jogo carregado com sucesso de: " + SAVE_FILE_NAME);
            return data;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar o jogo: " + e.getMessage());
            return null;
        }
    }
}