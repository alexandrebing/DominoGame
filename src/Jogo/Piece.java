package Jogo;

public class Piece {
    int sideA, sideB;

    public  Piece(){
        sideA = 0;
        sideB = 0;
    }

    public Piece(int a, int b){
        sideA = a;
        sideB = b;
    }

    public int getSideA() {
        return sideA;
    }

    public int getSideB() {
        return sideB;
    }

    public void Flip() {
        int aux = 0;
        aux = sideA;
        sideA = sideB;
        sideB = aux;
    }

    public String pieceDescription(){
        String res = Integer.toString(sideA)+ " | " + Integer.toString(sideB);
        return res;
    }
}
