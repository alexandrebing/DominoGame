package Jogo;

import java.util.ArrayList;

public class Player {

    private ArrayList<Jogo.Piece> myHand = new ArrayList<>();
    private String name;
    private int points;

    //CONSTRUÇÃO DEFAULT DO JOGADOR
    public Player(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPiece(Piece p) {
        myHand.add(p);
    }

    //CONSTRUÇÃO JOGADOR MAIS SET DE PEÇAS
    public Player(ArrayList<Jogo.Piece> p1Hand, String n) {
        name = n;
        myHand = p1Hand;
        points = 0;
    }

    //METODOS PARA GUARDAR E VERIFICAR PONTUAÇÃO
    public void UpdatePoints(int points) {
        this.points = this.points + points;
    }

    public int getPoints() {
        return points;
    }

    //RETORNA A MÃO DO JOGADOR
    public ArrayList<Jogo.Piece> ReturnHand(){
        return myHand;

    }

    //VERIFICA SE A MÃO DO JOGADOR ESTÁ VAZIA
    public boolean emptyHand(){
        return myHand.isEmpty();
    }


    //REMOVE PEÇA DA MÃO DO JOGADRO
    public void throwPiece(int n) {
        myHand.remove(n);

    }

    //MOSTRA MÃO DO JOGADOR
    public void showPieces() {
        int n = 1;
        System.out.println("*****MINHA MÃO*****");
        for (Jogo.Piece p: myHand) {
            System.out.printf("%d - | %d | %d |\n", n, p.sideA, p.sideB );
            n++;
        }
    }

    //RETORNA O TOTAL DE PEÇAS
    public int CountPieces() {
        return myHand.size();
    }

    //RETORNA A PEÇA EM DETERMINADO INDICE DO ARRAYLIST
    public Jogo.Piece getPiece(int pieceN) {
        return myHand.get(pieceN);
    }

    //RETORNA NOME DO JOGADOR
    public String Description(){
        return name;
    }

    //METODO PARA ENCONTRAR O INDICE DA PEÇA NA MÃO DO JOGADOR.
    public int getPieceIndex(int sA, int sB) {

        for (int i = 0; i < myHand.size() ; i++){
            if ((myHand.get(i).sideA == sA && myHand.get(i).sideB == sB) || (myHand.get(i).sideA == sB && myHand.get(i).sideB == sA))
                return i;
        }
        return 0;
    }

    public void NewHand(ArrayList<Piece> hand) {
        myHand = hand;
    }
}

