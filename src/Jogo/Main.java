package Jogo;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        RunGame game = new RunGame();
        while(true) {
            System.out.println("1 - Novo Jogo | 2 - Carregar Jogo | 3 - Encerrar");
            Scanner in = new Scanner(System.in);
            String a = in.nextLine();
            switch (a) {
                case "1":
                    game.setGame();
                    break;
                case "2":
                    game.LoadGame();
                    break;
                case "3":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }

    }
}
