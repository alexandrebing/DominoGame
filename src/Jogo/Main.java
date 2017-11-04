package Jogo;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        Jogo.RunGame game = new Jogo.RunGame();
        System.out.println("1 - Novo Jogo | 2 - Carregar Jogo");
        Scanner in = new Scanner(System.in);
        String a = in.nextLine();
        switch (a) {
            case "1":
                game.RunGame();
                break;
            case "2":
                game.LoadGame();
                break;
                default:
                    System.out.println("Opção inválida");
                    break;
        }


    }
}
