package Jogo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//FALTA SALVAR E CARREGAR JOGO


//CRIA JOGADORES, PEÇAS DE DOMINÓ, MESA E FAZ AS DISTRIBUIÇÕES. AO FINAL, INICIA RODADA 1
public class RunGame {

    Player player1 = new Player();
    Player player2 = new Player();
    Scanner in = new Scanner(System.in);
    boolean first = true;
    boolean endGameProposal = false;
    String endGameTxt = "";

    Table t = new Table();
    Piece p;
    int gamePieces = 3;

    public void RunGame() {

        System.out.println("Novo jogo de dominó!");

        System.out.println("Digite o nome do Jogador 1");
        String p1 = in.nextLine();
        System.out.println("Digite o nome do Jogador 2");
        String p2 = in.nextLine();

        //CRIACAO DAS ESTRUTURAS DO JOGO
        ArrayList <Piece> pieces = new ArrayList<>();

        //Criação das peças de dominó
        for (int i = 0; i <=gamePieces; i++){
            for(int j = i; j <= gamePieces; j++){
                p = new Piece(i,j);
                pieces.add(p);
            }
        }


        Collections.shuffle(pieces);//EMBARALHAR PEÇAS
        ArrayList<Piece> p1Hand = new ArrayList<>();
        for (int i = 0; i< pieces.size()/2; i++){
            p1Hand.add(pieces.get(i));
        }

        ArrayList<Piece> p2Hand = new ArrayList<>();
        for (int i = pieces.size()/2; i< pieces.size(); i++){
            p2Hand.add(pieces.get(i));
        }

        player1 = new Player(p1Hand, p1);
        player2 = new Player(p2Hand, p2);

        firstPlay();

        while(true){
            Play(player1);
            //Player1Play();
            CheckEndGame();
            Play(player2);
            //Player2Play();
            CheckEndGame();
        }


    }

    //DEFINE QUEM JOGA PRIMEIRO E A RODADA.
    private void firstPlay() {
        if (CheckFirstPlayer(player1)){
            System.out.printf("Primeira rodada: o jogador %s começa pois recebeu a peça | %d | %d |\n", player1.Description(), gamePieces, gamePieces);
            int n = player1.getPieceIndex(gamePieces,gamePieces);
            Piece p = player1.getPiece(n);
            t.addFirst(p);
            player1.throwPiece(n);
            notFirstPlay(player2, player1);
        }
        else{
            System.out.printf("Primeira rodada: o jogador %s começa pois recebeu a peça | %d | %d |\n", player2.Description(), gamePieces , gamePieces);
            int n = player2.getPieceIndex(gamePieces, gamePieces);
            Piece p = player2.getPiece(n);
            t.addFirst(p);
            player2.throwPiece(n);
            notFirstPlay(player1, player2);
        }
    }

    //VERIFICA SE O JOGADOR TEM A MAIOR PEÇA DO JOGO
    private boolean CheckFirstPlayer(Player player) {
        ArrayList<Piece> hand = player.ReturnHand();
        for (Piece p: hand
             ) {
            if (p.sideA == gamePieces && p.sideB == gamePieces){
                return true;
            }
        }
        return false;
    }

    //DEMAIS RODADAS.
    private void notFirstPlay(Player first, Player second){
        while (true){
            Play(first);
            CheckEndGame();
            Play(second);
            CheckEndGame();
        }

    }

    //METODO UNICO PARA JOGADA (RECEBE UM PLAYER COMO PARAMETRO)
    public void Play(Player currentPlayer){
        boolean choosingOpt = true;
        String ans;
        int countPieces = currentPlayer.CountPieces();
        if(endGameProposal){
            endGameTxt = endGameTxt + currentPlayer.Description() + ", você aceita?";
            System.out.println(endGameTxt);
            System.out.println("S: Sim | Qualquer outra tecla: Não");
            ans = in.next();
            ans = ans.toUpperCase();
            if(ans.equals("S")){
                endGameByTie(player1, player2);
                endGameProposal = false;
            }
            else{
                System.out.printf("%s rejeitou o fim do jogo.", currentPlayer.Description());
                endGameProposal = false;
            }

        }
        System.out.printf("É A VEZ DE %s!\n", currentPlayer.Description());
        t.ViewTable();
        System.out.println("Peças na mão: ");
        currentPlayer.showPieces();
        if (hasValidPiece(currentPlayer)){//SE PODE JOGAR ALGUMA PEÇA, ESSAS SÃO AS OPÇÕES
            while (choosingOpt){
                System.out.println("Qual a sua jogada?");
                System.out.printf("1-%d : Jogar peça | M : Mostrar Mesa| P : Pular\n", countPieces);
                ans = in.next();
                ans = ans.toUpperCase();
                switch (ans){
                    case "M": t.ViewTable();
                        break;
                    case "P": {
                        System.out.printf("%s pulou sua rodada!\n", currentPlayer.Description());
                        choosingOpt = false;
                        break;
                    }
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    case "10":
                    case "11":
                    case "12":
                    case "13":
                    case "14": {
                        int optNum = Integer.parseInt(ans);
                        int pieceIndex = optNum - 1;
                        if (jogadaInvalida(currentPlayer,optNum)){
                            System.out.printf("A peça de número %d não pode ser jogada. Tente outra opção ou pule a rodada\n", optNum);
                            break;
                        }
                        Piece currentPiece = currentPlayer.getPiece(pieceIndex);
                        if(UsablePiece(currentPiece)){
                            makeMove(currentPiece);
                            currentPlayer.throwPiece(pieceIndex);
                            System.out.println("Peça jogada: " + currentPiece.pieceDescription());
                            choosingOpt = false;
                        }
                        else {
                            System.out.println("Não é possível inserir esta peça... Tente outra opção ou pule a rodada");
                            break;
                        }
                        break;
                    }
                    default: {
                        System.out.println("Jogada inválida!");
                        break;
                    }
                }
            }
        }
        else{//NÃO HÁ PEÇAS A JOGAR -> PULA OU PROPOE FIM DE JOGO
            while (choosingOpt){
                System.out.println("Aparentemente, você não tem jogadas válidas... Você pode pular a sua rodada, ou propor o fim do jogo. Propor o fim do jogo está sujeito à aceitação do pedido pelo outro jogador, e pulará a sua rodada.");
                System.out.println("M : Mostrar Mesa| P : Pular | Q: Propor o fim do jogo");
                ans = in.next();
                ans = ans.toUpperCase();
                switch (ans){
                    case "M":
                        t.ViewTable();
                        break;
                    case "P":
                        System.out.printf("%s pulou sua rodada!\n", currentPlayer.Description());
                        choosingOpt = false;
                        break;
                    case "Q":
                        endGameTxt = "O jogador " + currentPlayer.Description() + " Propôs o fim do jogo..." ;
                        endGameProposal = true;
                        choosingOpt =  false;
                        break;
                }
            }

        }

    }

    //METODO QUE VAI PEGAR A PEÇA E VER ONDE É POSSÍVEL JOGAR NA MESA
    private void makeMove(Piece pieceN) {
        //PEÇA INSERÍVEL DOS 2 LADOS
        if((pieceN.sideA == t.ConsultLeft() || pieceN.sideB == t.ConsultLeft()) && (pieceN.sideA == t.ConsultRight() || pieceN.sideB == t.ConsultRight())){
            ChooseYourDestiny(pieceN);
        }
        else {
            //PEÇA A ESQUERDA
            if (pieceN.sideA == t.ConsultLeft() || pieceN.sideB == t.ConsultLeft()) {
                t.addLeft(pieceN);
            }
            //PEÇA A DIREITA
            if (pieceN.sideA == t.ConsultRight() || pieceN.sideB == t.ConsultRight()) {
                t.addRight(pieceN);
            }
        }

    }

    //SE FOR POSSÍVEL JOGAR EM AMBAS AS EXTREMIDADES, TEMOS ESSAS OPÇÕES
    private void ChooseYourDestiny(Piece pieceN) {//QUANDO POSSO INSERIR A PEÇA TANTO À ESQUERDA, QUANTO À DIREITA.
        System.out.println("M - Consultar Mesa | L - Inserir à esquerda | R - Inserir à direita");
        String option = in.next();
        option = option.toUpperCase();
        switch (option){
            case "M": t.ViewTable();
            ChooseYourDestiny(pieceN);
            break;
            case "L": {
                t.addLeft(pieceN);
                break;
            }
            case "R": {
                t.addRight(pieceN);
                break;
            }
            default:
                System.out.println("Opção inválida, use C, L ou R conforme a opção desejada.");
                ChooseYourDestiny(pieceN);
        }
    }

    //VERIFICA SE A JOGADA É VÁLIDA PARA EVITAR ERROS NAS OPÇÕES
    private boolean jogadaInvalida(Player p, int n) { // EXEMPLO SE A MÃO TEM 3 PEÇAS E PEDE A PEÇA 4
        int handSize = p.CountPieces();

        if (handSize < n){
            return true;
        }
        return false;
    }

    //VERIFICA SE O JOGO ACABOU APÓS CADA PEÇA JOGADA POR UM JOGADOR
    private void CheckEndGame() {
        if (player1.emptyHand()){
            System.out.printf("%s venceu! Parabéns %s!!!\n", player1.Description(), player1.Description());
            t.ViewTable();
            System.exit(0);
        }
        if(player2.emptyHand()){
            System.out.printf("%s venceu! Parabéns %s!!!\n",player2.Description(), player2.Description());
            t.ViewTable();
            System.exit(0);
        }
    }

    //VERIFICA SE A PEÇA PODE SER INSERIDA NA MESA
    public boolean UsablePiece(Piece p){
        if (checkGameLeft(p)) return true;
        else if (checkGameRight(p)) return true;
        return false;
    }

    //SE RETORNAR FALSO NÃO HÁ PEÇAS A INSERIR...
    private boolean hasValidPiece(Player h){
        ArrayList<Piece> currentHand = new ArrayList<>();
        currentHand = h.ReturnHand();
        for (Piece p: currentHand
             ) {
            if(checkOptions(p)) return true;

        }
        return false;
    }

    //ESTA REPETIDO
    private boolean checkOptions(Piece p){
        if (checkGameLeft(p)) return true;
        else if (checkGameRight(p)) return true;
        return false;
    }

    //VE SE PODE INSERIR À ESQUERDA DA MESA
    private boolean  checkGameLeft(Piece p){
        if (p.sideA == t.ConsultLeft()) return true;
        else if (p.sideB == t.ConsultLeft()) return true;
        return false;
    }

    //IDEM PARA A DIREITA
    private boolean checkGameRight(Piece p){
        if (p.sideA == t.ConsultRight()) return true;
        else if (p.sideB == t.ConsultRight()) return true;
        return false;
    }

    //METODO PARA ENCERRAR UM JOGO TRAVADO, QUANDO NÃO É POSSÍVEL INSERIR NOVAS PEÇAS
    private void endGameByTie(Player player1, Player player2) {
        int p1Points = 0;

        ArrayList<Piece> hand = player1.ReturnHand();
        for (Piece p: hand
             ) {
            p1Points = p1Points + p.sideA + p.sideB;
        }
        int p2Points = 0;
        hand = player2.ReturnHand();
        for (Piece p: hand
                ) {
            p2Points = p2Points + p.sideA + p.sideB;
        }

        if (p1Points == p2Points){
            System.out.printf("%s e %s empataram com %d pontos! Impressionante!\n", player1.Description(), player2.Description(), p1Points);
        }

        if(p1Points < p2Points){
            System.out.printf("%s venceu com %d pontos contra %d pontos de %s.\n",player1.Description(), p1Points, p2Points, player2.Description());
            t.ViewTable();
            System.out.printf("Parabéns %s!!!\n", player1.Description());
            System.exit(0);

        }

        if (p2Points < p1Points){
            System.out.printf("%s venceu com %d pontos contra %d pontos de %s.\n",player2.Description(), p2Points, p1Points, player1.Description());
            t.ViewTable();
            System.out.printf("Parabéns %s!!!\n", player2.Description());
            System.exit(0);
        }

        else {
            System.out.println("Deu merda...");
        }
    }


    //METODO PARA CARREGAR JOGO SALVO
    public void LoadGame() throws FileNotFoundException {
        System.out.printf("Escolha o arquivo que você quer carregar:\n * 1: Jogo Salvo 1\n * 2: Jogo Salvo 2\n");
        int n = in.nextInt();
        String a = "";
        boolean choosing = true;
        while(choosing){
            switch (n) {
                case 1:
                    a = "jogo1.txt";
                    choosing = false;
                    break;
                case 2:
                    a = "jogo2.txt";
                    choosing = false;
                    break;
                case 3:
                    a = "jogo3.txt";
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
        Scanner load = new Scanner(new File(a));

        //PRIMEIRA LINHA DO ARQUIVO PARA CONSTRUIR A MESA
        //SEGUNDA LINHA DO ARQUIVO PARA CONSTRUIR A MÃO DO PLAYER1
        //TERCEIRA LINHA DO PARA CONSTRUIR A MÃO DO PLAYER2
        // QUARTA LINHA DO ARQUIVO PARA DETERMINAR DE QUEM É A VEZ

    }

    //METODO PARA SALVAR JOGO
    public void SaveGame(){

    }
}
