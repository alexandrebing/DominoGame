package Jogo;

import java.util.ArrayList;

public class Table {

    ArrayList<Jogo.Piece> piecesInTable = new ArrayList<>();

    boolean emptyTable = true;

    public void ViewTable() {
        System.out.println("**************MESA DE JOGO*****************");
        for (Jogo.Piece p : piecesInTable
                ) {
            System.out.printf("| %d | %d | ;", p.sideA, p.sideB);
        }
        System.out.println();
        System.out.println("*******************************");
    }

    public void addLeft(Jogo.Piece p){
        Jogo.Piece o = piecesInTable.get(0);
        if (o.sideA != p.sideB){
            p.Flip();
        }
        piecesInTable.add(0, p);
    }

    public void addRight(Jogo.Piece p){
        Jogo.Piece o = piecesInTable.get(piecesInTable.size()-1);
        if (o.sideB != p.sideA){
            p.Flip();
        }
        piecesInTable.add(p);
    }

    public int ConsultLeft(){
        Jogo.Piece p = piecesInTable.get(0);
        return p.sideA;
    }

    public int ConsultRight(){
        Jogo.Piece p = piecesInTable.get(piecesInTable.size()-1);
        return p.sideB;
    }

    public void addFirst(Jogo.Piece p){
        piecesInTable.add(p);
    }

    public boolean EmptyTable(){
        return piecesInTable.isEmpty();
    }


}

