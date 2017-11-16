package Jogo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//FALTA CRIAR PONTUAÇÃO E AUMENTAR A COMPLEXIDADE DO JOGO, PARA QUE SE REPITA ATÉ QUE ATINJA UMA PONTUAÇÃO X.
//CRIA JOGADORES, PEÇAS DE DOMINÓ, MESA E FAZ AS DISTRIBUIÇÕES. AO FINAL, INICIA RODADA 1
public class RunGame {

    Player player1 = new Player();
    Player player2 = new Player();
    Scanner in = new Scanner(System.in);
    boolean first = true;
    boolean endTurnProposal = false;
    String endGameTxt = "";
    ArrayList <Piece> pieces = new ArrayList<>();
    ArrayList<Piece> stdPieces = new ArrayList<>();

    Jogo.Table t = new Jogo.Table();
    Jogo.Piece p;
    int gamePieces = 3;
    int totalPoints = 20;

    public void RunGame() throws FileNotFoundException {

        System.out.println("Novo jogo de dominó!");

        System.out.println("Digite o nome do Jogador 1");
        String p1 = in.nextLine();
        System.out.println("Digite o nome do Jogador 2");
        String p2 = in.nextLine();

        //Criação das peças de dominó
        pieces = MakePieces(gamePieces);

        player1.setName(p1);
        player2.setName(p2);

        NewTurn();
    }

    private ArrayList<Piece> MakePieces(int n){
        ArrayList<Piece> thisSet = new ArrayList<>();
        for (int i = 0; i <=n; i++){
            for(int j = i; j <= n; j++){
                p = new Jogo.Piece(i,j);
                thisSet.add(p);
            }
        }
        return thisSet;
    }

    //METODO PARA INICIALIZAR RODADA
    private void NewTurn() throws FileNotFoundException {
        if (player1.getPoints() >= totalPoints || player2.getPoints() >= totalPoints ) {
            endGame();
        }

        t.resetTable();
        pieces = MakePieces(gamePieces);
        Collections.shuffle(pieces);//EMBARALHAR PEÇAS
        ArrayList<Piece> p1Hand = new ArrayList<>();
        for (int i = 0; i < pieces.size() / 2; i++) {
            p1Hand.add(pieces.get(i));
        }

        ArrayList<Piece> p2Hand = new ArrayList<>();
        for (int i = pieces.size() / 2; i < pieces.size(); i++) {
            p2Hand.add(pieces.get(i));
        }

        player1.NewHand(p1Hand);
        player2.NewHand(p2Hand);

        firstPlay();

    }

    //DEFINE QUEM JOGA PRIMEIRO E A RODADA.
    private void firstPlay() throws FileNotFoundException {
        if (CheckFirstPlayer(player1)){
            System.out.printf("Primeira rodada: o jogador %s começa pois recebeu a peça | %d | %d |\n", player1.Description(), gamePieces, gamePieces);
            int n = player1.getPieceIndex(gamePieces,gamePieces);
            Jogo.Piece p = player1.getPiece(n);
            t.add(p);
            player1.throwPiece(n);
            notFirstPlay(player2, player1);
        }
        else{
            System.out.printf("Primeira rodada: o jogador %s começa pois recebeu a peça | %d | %d |\n", player2.Description(), gamePieces , gamePieces);
            int n = player2.getPieceIndex(gamePieces, gamePieces);
            Jogo.Piece p = player2.getPiece(n);
            t.add(p);
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
    private void notFirstPlay(Player first, Player second) throws FileNotFoundException {
        while (true){
            Play(first, second);
            CheckEndTurn();
            Play(second, first);
            CheckEndTurn();
        }

    }

    //METODO UNICO PARA JOGADA (RECEBE UM PLAYER COMO PARAMETRO) TÁ GRANDE, DIVIDIR?
    public void Play(Player currentPlayer, Player notMyTurn) throws FileNotFoundException { //Coloquei o segundo jogador também para fins de Save
        boolean choosingOpt = true;
        String ans;
        int countPieces = currentPlayer.CountPieces();
        if(endTurnProposal){
            endGameTxt = endGameTxt + currentPlayer.Description() + ", você aceita?";
            System.out.println(endGameTxt);
            System.out.println("S: Sim | Qualquer outra tecla: Não");
            ans = in.next();
            ans = ans.toUpperCase();
            if(ans.equals("S")){
                endTurnProposal = false;
                endTurnByTie(currentPlayer, notMyTurn);
            }
            else{
                endTurnProposal = false;
                System.out.printf("%s rejeitou o fim do jogo.", currentPlayer.Description());
            }

        }
        System.out.printf("É A VEZ DE %s!\n", currentPlayer.Description());
        t.ViewTable();
        System.out.println("Peças na mão: ");
        currentPlayer.showPieces();
        if (hasValidPiece(currentPlayer)){//SE PODE JOGAR ALGUMA PEÇA, ESSAS SÃO AS OPÇÕES
            while (choosingOpt){
                System.out.println("Qual a sua jogada?");
                System.out.printf("1-%d : Jogar peça | Z: Mostrar pontuação | S: Salvar Jogo | M : Mostrar Mesa| P : Pular | Q : Encerrar\n", countPieces);
                ans = in.next();
                ans = ans.toUpperCase();
                switch (ans){
                    case "Q":
                        System.exit(0);
                    case "Z":
                        System.out.printf("Pontuação:\n %s tem %d pontos\n %s tem %d pontos\n",currentPlayer.Description(), currentPlayer.getPoints(), notMyTurn.Description(), notMyTurn.getPoints());
                        break;
                    case "M":  System.out.printf("É A VEZ DE %s!\n", currentPlayer.Description());
                        t.ViewTable();
                        currentPlayer.showPieces();
                        break;
                    case "P": {
                        System.out.printf("%s pulou sua rodada!\n", currentPlayer.Description());
                        choosingOpt = false;
                        break;
                    }
                    case "S":{
                        System.out.println("Escolha o espaço de salvamento:\n 1: Salvar no espaço 1\n 2: Salvar no espaço 2\n 3: Salvar no espaço 3");
                        String inRes = in.next();
                        switch (inRes){
                            case "1":
                                SaveGame(currentPlayer,notMyTurn, inRes);
                                break;
                            case "2":
                                SaveGame(currentPlayer,notMyTurn, inRes);
                                break;
                            case "3":
                                SaveGame(currentPlayer,notMyTurn, inRes);
                                break;
                            default:
                                System.out.println("Opção Inválida!");
                                break;
                        }
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
                        Jogo.Piece currentPiece = currentPlayer.getPiece(pieceIndex);
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
                System.out.println("M : Mostrar Mesa | S : Salvar Jogo | P : Pular | F: Propor o fim do jogo | Q: Encerrar");
                ans = in.next();
                ans = ans.toUpperCase();
                switch (ans){
                    case "Q":
                        System.exit(0);
                    case "Z":  System.out.printf("Pontuação:\n %s tem %d pontos\n %s tem %d pontos\n",currentPlayer.Description(), currentPlayer.getPoints(), notMyTurn.Description(), notMyTurn.getPoints());
                        break;
                    case "M":
                        System.out.printf("É A VEZ DE %s!\n", currentPlayer.Description());
                        t.ViewTable();
                        currentPlayer.showPieces();
                        break;
                    case "S":{
                        System.out.println("Escolha o espaço de salvamento:\n 1: Salvar no espaço 1\n 2: Salvar no espaço 2\n 3: Salvar no espaço 3");
                        String inRes = in.next();
                        switch (inRes){
                            case "1":
                                SaveGame(currentPlayer,notMyTurn, inRes);
                                break;
                            case "2":
                                SaveGame(currentPlayer,notMyTurn, inRes);
                                break;
                            case "3":
                                SaveGame(currentPlayer,notMyTurn, inRes);
                                break;
                            default:
                                System.out.println("Opção Inválida!");
                                break;
                        }
                        break;
                    }
                    case "P":
                        System.out.printf("%s pulou sua rodada!\n", currentPlayer.Description());
                        choosingOpt = false;
                        break;
                    case "F":
                        endGameTxt = "O jogador " + currentPlayer.Description() + " Propôs o fim do jogo..." ;
                        endTurnProposal = true;
                        choosingOpt =  false;
                        break;
                }
            }

        }

    }

    //METODO QUE VAI PEGAR A PEÇA E VER ONDE É POSSÍVEL JOGAR NA MESA
    private void makeMove(Jogo.Piece pieceN) {
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
    private void ChooseYourDestiny(Jogo.Piece pieceN) {//QUANDO POSSO INSERIR A PEÇA TANTO À ESQUERDA, QUANTO À DIREITA.
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
    private boolean jogadaInvalida(Jogo.Player p, int n) { // EXEMPLO SE A MÃO TEM 3 PEÇAS E PEDE A PEÇA 4
        int handSize = p.CountPieces();

        if (handSize < n){
            return true;
        }
        return false;
    }

    //VERIFICA SE A RODADA ACABOU APÓS CADA PEÇA JOGADA POR UM JOGADOR
    private void CheckEndTurn() throws FileNotFoundException {
        //JOGADOR 1 FICOU SEM PEÇAS
        if (player1.emptyHand()){
            int p = checkLooserPoints(player2);
            System.out.printf("%s venceu a rodada! %s acumulou %d pontos...\n", player1.Description(), player2.Description(), p);
            player2.UpdatePoints(p);
            t.ViewTable();
            NewTurn();
            //System.exit(0);
        }
        //JOGADOR 2 FICOU SEM PEÇAS
        if(player2.emptyHand()){
            int p = checkLooserPoints(player1);
            System.out.printf("%s venceu a rodada! %s acumulou %d pontos...\n", player2.Description(), player1.Description(), p);
            player1.UpdatePoints(p);
            t.ViewTable();
            NewTurn();
            //System.exit(0);
        }
    }

    //CONTA QUANTOS PONTOS O PERDEDOR SOMOU
    private int checkLooserPoints(Player player) {
        int points = 0;
        ArrayList<Piece> hand = player.ReturnHand();
        for (Piece p: hand
                ) {
            points = points + p.sideA + p.sideB;
        }
        return points;
    }

    //VERIFICA SE A PEÇA PODE SER INSERIDA NA MESA
    public boolean UsablePiece(Piece p){
        if (checkGameLeft(p)) return true;
        else if (checkGameRight(p)) return true;
        return false;
    }

    //SE RETORNAR FALSO NÃO HÁ PEÇAS A INSERIR...
    private boolean hasValidPiece(Player h){
        ArrayList<Jogo.Piece> currentHand = new ArrayList<>();
        currentHand = h.ReturnHand();
        for (Jogo.Piece p: currentHand
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
    private void endTurnByTie(Player player1, Player player2) throws FileNotFoundException {
        int p1Points = 0;

        ArrayList<Piece> hand = player1.ReturnHand();
        for (Piece p: hand
             ) {
            p1Points = p1Points + p.sideA + p.sideB;
        }
        int p2Points = 0;
        hand = player2.ReturnHand();
        for (Jogo.Piece p: hand
                ) {
            p2Points = p2Points + p.sideA + p.sideB;
        }

        player1.UpdatePoints(p1Points);
        player2.UpdatePoints(p2Points);

        if (p1Points == p2Points){
            System.out.printf("%s e %s empataram com %d pontos! Impressionante!\n", player1.Description(), player2.Description(), p1Points);
            NewTurn();
        }

        if(p1Points < p2Points){
            System.out.printf("%s venceu a rodada com %d pontos contra %d pontos de %s.\n",player1.Description(), p1Points, p2Points, player2.Description());
            t.ViewTable();
            System.out.printf("Parabéns %s!!!\n", player1.Description());
            NewTurn();
            //System.exit(0);

        }

        if (p2Points < p1Points){
            System.out.printf("%s venceu a rodada com %d pontos contra %d pontos de %s.\n",player2.Description(), p2Points, p1Points, player1.Description());
            t.ViewTable();
            System.out.printf("Parabéns %s!!!\n", player2.Description());
            NewTurn();
            //System.exit(0);
        }

        else {
            System.out.println("Deu merda...");
            System.exit(0);
        }
    }


    //METODO PARA CARREGAR JOGO SALVO
    public void LoadGame() throws FileNotFoundException {
        System.out.printf("Escolha o arquivo que você quer carregar:\n * 1: Jogo Salvo 1\n * 2: Jogo Salvo 2\n * 3: Jogo Salvo 3\n");
        int n = in.nextInt();
        String gameSave = "";
        String section [];
        Piece p;
        boolean choosing = true;
        while(choosing){
            switch (n) {
                case 1:
                    gameSave = "save1.txt";
                    choosing = false;
                    break;
                case 2:
                    gameSave = "save2.txt";
                    choosing = false;
                    break;
                case 3:
                    gameSave = "save3.txt";
                    choosing = false;
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
        Scanner load = new Scanner(new File(gameSave));

        String line [] = new String [5];
        int i = 0;

        while (load.hasNextLine()){
            line [i] = load.nextLine();
            i ++;
        }

        section = line[0].split(";");
        for (int j = 0; j < section.length; j = j+2){
            int pa = Integer.parseInt(section[j]);
            int pb = Integer.parseInt(section[j+1]);
            p = new Piece(pa,pb);
            t.add(p);
        }

        player1.setName(line[1]);

        section = line[2].split(";");
        for (int j = 0; j < section.length; j = j+2){
            int pa = Integer.parseInt(section[j]);
            int pb = Integer.parseInt(section[j+1]);
            p = new Piece(pa,pb);
            player1.addPiece(p);
        }

        player2.setName(line[3]);

        section = line[4].split(";");
        for (int j = 0; j < section.length; j = j+2){
            int pa = Integer.parseInt(section[j]);
            int pb = Integer.parseInt(section[j+1]);
            p = new Piece(pa,pb);
            player2.addPiece(p);
        }

        notFirstPlay(player1,player2);

        //PRIMEIRA LINHA DO ARQUIVO PARA CONSTRUIR A MESA
        //SEGUNDA LINHA DO ARQUIVO PARA CONSTRUIR A MÃO DO PLAYER1
        //TERCEIRA LINHA DO PARA CONSTRUIR A MÃO DO PLAYER2
        // QUARTA LINHA DO ARQUIVO PARA DETERMINAR DE QUEM É A VEZ

    }

    //METODO PARA SALVAR JOGO
    public void SaveGame(Player currentPlayer, Player nextPlayer, String fileNum) throws FileNotFoundException {

        ArrayList<Piece> listOfPieces;
        //int gameTable [] = new int [t.countPieces()];
        String gameTable = "";
        String currentPlayerName = currentPlayer.Description() + "\n";
        //int p1Hand [] = new int [currentPlayer.CountPieces()];
        String p1Hand = "";
        String nextPlayerName = nextPlayer.Description()+"\n";
        String p2Hand = "";
        //int p2Hand [] = new int [nextPlayer.CountPieces()];
        String res;

        listOfPieces = t.getPiecesInTable();
        //PEGAR DADOS DA MESA
        for (Piece p : listOfPieces
             ) {
            gameTable = gameTable + p.sideA + ";" + p.sideB + ";";
        }
        gameTable = gameTable + "\n";

        //Pegando mao do jogador que salvou
        listOfPieces = currentPlayer.ReturnHand();
        for (Piece p : listOfPieces
                ) {
            p1Hand = p1Hand + p.sideA + ";" + p.sideB + ";";
        }
        p1Hand = p1Hand + "\n";

        listOfPieces = nextPlayer.ReturnHand();
        for (Piece p : listOfPieces
                ) {
            p2Hand = p2Hand + p.sideA + ";" + p.sideB + ";";
        }
        // aqui não insere nova linha

        res = gameTable + currentPlayerName + p1Hand + nextPlayerName + p2Hand;

        //System.out.println(res);

        //Salva o arquivo num arquivo txt
        String file = "save" + fileNum + ".txt";

        PrintWriter out = new PrintWriter(file);

        out.println(res);

        out.close();

    }

    //METODO QUE ENCERRA O JOGO
    private void endGame() {
        System.out.println("Termina o jogo aqui em Porto Alegre! (voz do Galvão Bueno)");
        int p1Points = player1.getPoints();
        int p2Points = player2.getPoints();
        if (p1Points == p2Points){
            System.out.printf("%s e %s empataram com %d pontos! Impressionante!\n", player1.Description(), player2.Description(), p1Points);
        }

        if(p1Points < p2Points){
            System.out.printf("%s venceu o jogo com %d pontos contra %d pontos de %s. Parabéns\n",player1.Description(), p1Points, p2Points, player2.Description());
            t.ViewTable();
            System.out.printf("Parabéns %s!!!\n", player1.Description());
            //System.exit(0);

        }

        if (p2Points < p1Points){
            System.out.printf("%s venceu o jogo com %d pontos contra %d pontos de %s. Parabéns!\n",player2.Description(), p2Points, p1Points, player1.Description());
            t.ViewTable();
            System.out.printf("Parabéns %s!!!\n", player2.Description());
        }
        System.exit(0);
    }

}
