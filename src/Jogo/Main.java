package Jogo;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        RunGame game = new RunGame();
        System.out.println("1 - Novo Jogo | 2 - Carregar Jogo");
        String a = "1";
        switch (a) {
            case "1":
                game.RunGame();
                break;
            case "2":
                game.LoadGame();
                break;
        }


    }
}
