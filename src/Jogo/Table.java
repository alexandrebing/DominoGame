package Jogo;

import java.util.ArrayList;

public class Table {

    private ArrayList<Piece> piecesInTable = new ArrayList<>();

    public void ViewTable() {
        System.out.println("**************MESA DE JOGO*****************");
        for (Piece p : piecesInTable
                ) {
            System.out.printf("| %d | %d | ;", p.sideA, p.sideB);
        }
        System.out.println();
        System.out.println("*******************************");
    }

    public void addLeft(Piece p){
        Piece o = piecesInTable.get(0);
        if (o.sideA != p.sideB){
            p.Flip();
        }
        piecesInTable.add(0, p);
    }

    public void addRight(Piece p){
        Piece o = piecesInTable.get(piecesInTable.size()-1);
        if (o.sideB != p.sideA){
            p.Flip();
        }
        piecesInTable.add(p);
    }

    public int ConsultLeft(){
        Piece p = piecesInTable.get(0);
        return p.sideA;
    }

    public int ConsultRight(){
        Piece p = piecesInTable.get(piecesInTable.size()-1);
        return p.sideB;
    }

    public void add(Piece p){
        piecesInTable.add(p);
    }

    public ArrayList<Piece> getPiecesInTable(){
        return piecesInTable;
    }

    public void resetTable() {
        piecesInTable = new ArrayList<>();
    }
}

