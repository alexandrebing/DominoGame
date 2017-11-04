package Jogo;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        RunGame game = new RunGame();
        game.RunGame();
        game.LoadGame();

    }
}
