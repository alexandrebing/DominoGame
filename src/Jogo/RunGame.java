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
    Scanner in = new Scanner(System.in);
    ArrayList <Piece> pieces = new ArrayList<>();
    ArrayList<Player> player = new ArrayList<>();

    Jogo.Table t = new Jogo.Table();
    Jogo.Piece p;
    int gamePieces = 3;
    int totalPoints = 20;
    int jump = 0;
    int numOfPlayers;

    public void setGame() throws FileNotFoundException {
        //jogo para 2 jogadores
        System.out.println("1 - Jogo para 2 jogadores | 2 - Jogo para 4 jogadores | 3 - Jogo contra IA");
        String ans = in.nextLine();
        switch (ans){
            case "1":
                RunGame(2);
                break;
            case "2":
                gamePieces = 6;
                RunGame(4);
                break;
            default:
                System.out.println("Jogada Inválida");
                break;
        }
        //jogo para 4 jogadores
    }


    public void RunGame(int n) throws FileNotFoundException {

        System.out.println("Novo jogo de dominó!");

        numOfPlayers = n;

        for (int i = 0; i < numOfPlayers ; i++) {
            Player currentPlayer = new Player();
            System.out.println("Digite o nome do Jogador " + (i+1));
            String name = in.nextLine();
            currentPlayer.setName(name);
            player.add(currentPlayer);
        }

        //Criação das peças de dominó
        pieces = MakePieces(gamePieces);

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
        for (Player p: player
             ) {
            if(p.getPoints() >= totalPoints)
                endGame();
        }

        t.resetTable();
        pieces = MakePieces(gamePieces);
        Collections.shuffle(pieces);//EMBARALHAR PEÇAS
        if (numOfPlayers == 2) {
            ArrayList<Piece> p1Hand = new ArrayList<>();
            for (int i = 0; i < pieces.size() / 2; i++) {
                p1Hand.add(pieces.get(i));
            }

            ArrayList<Piece> p2Hand = new ArrayList<>();
            for (int i = pieces.size() / 2; i < pieces.size(); i++) {
                p2Hand.add(pieces.get(i));
            }

            player.get(0).NewHand(p1Hand);
            player.get(1).NewHand(p2Hand);
        }

        if (numOfPlayers == 4){
            ArrayList<Piece> p1Hand = new ArrayList<>();
            ArrayList<Piece> p2Hand = new ArrayList<>();
            ArrayList<Piece> p3Hand = new ArrayList<>();
            ArrayList<Piece> p4Hand = new ArrayList<>();

            int m = 0;
            int gap = pieces.size()/4;
            int s = 0;
            s = s + gap;

            for (int i = 0; i < s; i++) {
                p1Hand.add(pieces.get(i));
            }
            m = s;
            s = s + gap;
            for (int i = m; i < s; i++) {
                p2Hand.add(pieces.get(i));
            }
            m = s;
            s = s + gap;
            for (int i = m; i < s; i++) {
                p3Hand.add(pieces.get(i));
            }
            m = s;
            s = s + gap;
            for (int i = m; i < s; i++) {
                p4Hand.add(pieces.get(i));
            }
            player.get(0).NewHand(p1Hand);
            player.get(1).NewHand(p2Hand);
            player.get(2).NewHand(p3Hand);
            player.get(3).NewHand(p4Hand);
        }

        firstPlay();

    }

    //DEFINE QUEM JOGA PRIMEIRO E A RODADA.
    private void firstPlay() throws FileNotFoundException {
        Player p;
        for (int i = 0; i < player.size(); i++) {
            p = player.get(i);
            if(CheckFirstPlayer(p)){
                System.out.printf("Primeira rodada: o jogador %s começa pois recebeu a peça | %d | %d |\n", p.Description(), gamePieces, gamePieces);
                int n = p.getPieceIndex(gamePieces,gamePieces);
                Piece piece = p.getPiece(n);
                t.add(piece);
                p.throwPiece(n);
                player.remove(i);
                player.add(p);
                notFirstPlay(player);

            }
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
    private void notFirstPlay(ArrayList<Player> playerList) throws FileNotFoundException {
        while (true){
            Play();
            CheckEndTurn();
        }

    }

    //METODO UNICO PARA JOGADA (RECEBE UM PLAYER COMO PARAMETRO) TÁ GRANDE, DIVIDIR?
    public void Play() throws FileNotFoundException { //Coloquei o segundo jogador também para fins de Save
        Player currentPlayer = player.get(0);
        boolean choosingOpt = true;
        String ans;
        int countPieces = currentPlayer.CountPieces();
        jump = 0;
        int jumpLimit = player.size();
        if(jump == jumpLimit){
            endTurnByTie(player);

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
                    case "Z": checkPontucao();
                        break;
                    case "M":  System.out.printf("É A VEZ DE %s!\n", currentPlayer.Description());
                        t.ViewTable();
                        currentPlayer.showPieces();
                        break;
                    case "P": {
                        System.out.printf("%s pulou sua rodada!\n", currentPlayer.Description());
                        jump++;
                        choosingOpt = false;
                        break;
                    }
                    case "S":{
                        System.out.println("Escolha o espaço de salvamento:\n 1: Salvar no espaço 1\n 2: Salvar no espaço 2\n 3: Salvar no espaço 3");
                        String inRes = in.next();
                        switch (inRes){
                            case "1":
                                SaveGame(inRes);
                                break;
                            case "2":
                                SaveGame(inRes);
                                break;
                            case "3":
                                SaveGame(inRes);
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
                            jump = 0;
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
                    case "Z":  checkPontucao();
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
                                SaveGame(inRes);
                                break;
                            case "2":
                                SaveGame(inRes);
                                break;
                            case "3":
                                SaveGame(inRes);
                                break;
                            default:
                                System.out.println("Opção Inválida!");
                                break;
                        }
                        break;
                    }
                    case "P":
                        System.out.printf("%s pulou sua rodada!\n", currentPlayer.Description());
                        jump++;
                        choosingOpt = false;
                        break;
                    default:
                        System.out.println("Opção Inválida");
                        break;
                }
            }

        }

    }

    private void checkPontucao() {
        System.out.println("Pontuação");
        for (Player p: player
             ) {
            System.out.printf("%s tem %d pontos\n",p.Description(), p.getPoints());

        }
    }

    //METODO QUE VAI PEGAR A PEÇA E VER ONDE É POSSÍVEL JOGAR NA MESA
    private void makeMove(Jogo.Piece pieceN) {
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
        //VERIFICA PEÇAS DO JOGADOR ATIVO
        Player currentPlayer = player.get(0);
        if (currentPlayer.emptyHand()){
            System.out.println("RODADA ENCERRADA\nPontuação Atualizada:");
            for (Player pl:player
                 ) {
                int points = checkLooserPoints(pl);
                pl.UpdatePoints(points);
                System.out.printf(" %s: %d pontos na rodada (total %d)\n", pl.Description(), points, pl.getPoints());

            }
            t.ViewTable();
            NewTurn();
            //System.exit(0);
        }
        player.remove(0);
        player.add(currentPlayer);
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
    private void endTurnByTie(ArrayList<Player> allPlayers) throws FileNotFoundException {

        int points[] = new int[allPlayers.size()];
        int n = 0;
        for (Player p: allPlayers
             ) {
            points[n] = 0;
            ArrayList<Piece> hand = p.ReturnHand();
            for (Piece piece: hand
                 ) {
                points[n] = points[n] + piece.sideA + piece.sideB;
            }
            p.UpdatePoints(points[n]);
            n++;
        }

        System.out.println("Pontuação atualizada:");
        n = 0;
        for (Player p: allPlayers
             ) {
            System.out.printf(" %s: %d pontos na rodada, %d pontos no total\n", p.Description(), points[n],p.getPoints());
        }

        NewTurn();
    }


    //METODO PARA CARREGAR JOGO SALVO
    /*
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

        String line [] = new String [7];
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

        player1.UpdatePoints(Integer.parseInt(line[3]));

        player2.setName(line[4]);

        section = line[5].split(";");
        for (int j = 0; j < section.length; j = j+2){
            int pa = Integer.parseInt(section[j]);
            int pb = Integer.parseInt(section[j+1]);
            p = new Piece(pa,pb);
            player2.addPiece(p);
        }

        player2.UpdatePoints(Integer.parseInt(line[6]));

        notFirstPlay(player);

        //PRIMEIRA LINHA DO ARQUIVO PARA CONSTRUIR A MESA
        //SEGUNDA LINHA DO ARQUIVO PARA CONSTRUIR A MÃO DO PLAYER1
        //TERCEIRA LINHA DO PARA CONSTRUIR A MÃO DO PLAYER2
        // QUARTA LINHA DO ARQUIVO PARA DETERMINAR DE QUEM É A VEZ

    }
    */

    //METODO PARA SALVAR JOGO
    public void SaveGame(String fileNum) throws FileNotFoundException {




        /*int numOfPlayers = player.size();
        ArrayList<Piece> listOfPieces;
        //int gameTable [] = new int [t.countPieces()];
        String gameTable = "";

        //int p1Hand [] = new int [currentPlayer.CountPieces()];
        String res;

        listOfPieces = t.getPiecesInTable();

        //PEGAR DADOS DA MESA
        for (Piece p : listOfPieces
             ) {
            gameTable = gameTable + p.sideA + ";" + p.sideB + ";";
        }
        gameTable = gameTable + "\n";

        //Pegando mao do jogador que salvou
        String currentPlayerName = currentPlayer.Description() + "\n";
        String currentPlayerHand = "";
        String currentPlayerPoints = Integer.toString(currentPlayer.getPoints()) + "\n";
        listOfPieces = currentPlayer.ReturnHand();
        for (Piece p : listOfPieces
                ) {
            currentPlayerHand = currentPlayerHand + p.sideA + ";" + p.sideB + ";";
        }
        currentPlayerHand = currentPlayerHand + "\n";

        //DADOS DOS DEMAIS JOGADORES

        res = gameTable + currentPlayerName + currentPlayerHand + currentPlayerPoints;
        int n = 0;
        String [] otherPlayersNames = new String [otherPlayers.size()];
        String [] otherPlayersHand = new String [otherPlayers.size()];
        String [] otherPlayersPoints = new String [otherPlayers.size()];
        for (Player p: otherPlayers
             ) {
            otherPlayersNames[n] = p.Description() + "\n";
            otherPlayersPoints[n] = Integer.toString(p.getPoints());
            otherPlayersHand[n] = "";
            listOfPieces = p.ReturnHand();
            for (Piece piece: listOfPieces
                 ) {
                otherPlayersHand[n] = otherPlayersHand[n] + piece.sideA + ";" + piece.sideB;
            }
            otherPlayersHand[n] = otherPlayersHand[n] + "\n";
            res = res + otherPlayersNames[n] + otherPlayersHand[n] + otherPlayersPoints[n];
        }

        //System.out.println(res);

        //Salva o arquivo num arquivo txt
        String file = "save" + fileNum + ".txt";

        PrintWriter out = new PrintWriter(file);

        out.println(res);

        out.close();
        */
    }


    //METODO QUE ENCERRA O JOGO
    private void endGame() {
        System.out.println("Termina o jogo aqui em Porto Alegre! (voz do Galvão Bueno)\nPontuação final:");

        for (Player pl: player
             ) {
            System.out.printf("%s: %d pontos\n",pl.Description(), pl.getPoints());
        }
        System.exit(0);
    }

}
