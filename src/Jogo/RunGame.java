package Jogo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.management.PlatformLoggingMXBean;
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
    int gamePieces;
    int totalPoints = 20;
    int jump = 0;
    int numOfPlayers;

    public void setGame() throws FileNotFoundException {
        //jogo para 2 jogadores
        System.out.println("1 - Jogo para 2 jogadores | 2 - Jogo para 4 jogadores ");
        String ans = in.nextLine();
        switch (ans){
            case "1":
                gamePieces = 3;
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
    private void NewTurn(){
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

        try {
            firstPlay();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
        int jumpLimit = player.size();
        if(jump == jumpLimit){
            jump = 0;
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
                System.out.println("M : Mostrar Mesa | S : Salvar Jogo | P : Pular | Q: Encerrar");
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
    private void makeMove(Piece pieceN) {
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
        if(t.ConsultLeft() == t.ConsultRight()){
            t.addRight(pieceN);
        }

        else {
            System.out.println("M - Consultar Mesa | L - Inserir à esquerda | R - Inserir à direita");
            String option = in.next();
            option = option.toUpperCase();
            switch (option) {
                case "M":
                    t.ViewTable();
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
                    System.out.println("Opção inválida, use M, L ou R conforme a opção desejada.");
                    ChooseYourDestiny(pieceN);
            }
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
    private void CheckEndTurn() {
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

            if (numOfPlayers == 4){
                int d1Points;
                int d2Points;
                System.out.println("Pontuação parcial das duplas:");
                d1Points = player.get(0).getPoints() + player.get(2).getPoints();
                System.out.printf("***Dupla %s e %s: %d pontos\n", player.get(0).Description(), player.get(2).Description(), d1Points);
                d2Points = player.get(1).getPoints() + player.get(3).getPoints();
                System.out.printf("***Dupla %s e %s: %d pontos\n", player.get(1).Description(), player.get(3).Description(), d2Points);

            }

            t.ViewTable();
            //POR QUE ESTÁ PULANDO???
            NewTurn();
        }
        else {
            player.remove(0);
            player.add(currentPlayer);
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
    private void endTurnByTie(ArrayList<Player> allPlayers) throws FileNotFoundException {

        int numOfPlayers = player.size();

        System.out.println("Pontuação atualizada:");
        for (Player p: allPlayers
             ) {
            int points = 0;
            ArrayList<Piece> hand = p.ReturnHand();
            for (Piece piece: hand
                 ) {
                points = points + piece.sideA + piece.sideB;
            }
            p.UpdatePoints(points);
            System.out.printf(" %s: %d pontos na rodada, %d pontos no total\n", p.Description(), points,p.getPoints());
        }

        if (numOfPlayers == 4){
            int d1Points;
            int d2Points;
            System.out.println("Pontuação parcial das duplas:");
            d1Points = player.get(0).getPoints() + player.get(2).getPoints();
            System.out.printf("***Dupla %s e %s: %d pontos\n", player.get(0).Description(), player.get(2).Description(), d1Points);
            d2Points = player.get(1).getPoints() + player.get(3).getPoints();
            System.out.printf("***Dupla %s e %s: %d pontos\n", player.get(1).Description(), player.get(3).Description(), d2Points);

        }


        NewTurn();
    }


    //METODO PARA CARREGAR JOGO SALVO
    public void LoadGame() throws FileNotFoundException {
        System.out.printf("Escolha o arquivo que você quer carregar:\n * 1: Jogo Salvo 1\n * 2: Jogo Salvo 2\n * 3: Jogo Salvo 3\n");
        int n = in.nextInt();
        String gameSave = "";
        String section [];
        Piece p;
        int rL;
        int cL = 0;
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

        String firstLine = load.nextLine();
        rL = (Integer.parseInt(firstLine ) * 3) + 2;
        numOfPlayers = Integer.parseInt(firstLine);

        String line [] = new String [rL];
        int i = 0;

        while (load.hasNextLine()) {
            line[i] = load.nextLine();
            i++;
        }

        section = line[cL].split(";");
        for (int j = 0; j < section.length; j = j+2){
            int pa = Integer.parseInt(section[j]);
            int pb = Integer.parseInt(section[j+1]);
            p = new Piece(pa,pb);
            t.add(p);
        }
        cL++;

        //Dados de cada jogador
        while (cL < rL-1){
            Player setPlayer = new Player();
            setPlayer.setName(line[cL]);
            cL++;
            section = line[cL].split(";");
            for (int j = 0; j < section.length; j = j+2){
                int pa = Integer.parseInt(section[j]);
                int pb = Integer.parseInt(section[j+1]);
                p = new Piece(pa,pb);
                setPlayer.addPiece(p);
            }
            cL++;
            setPlayer.UpdatePoints(Integer.parseInt(line[cL]));
            player.add(setPlayer);
            cL++;
        }
        switch (numOfPlayers){
            case 2:
                gamePieces = 3;
                break;
            case 4:
                gamePieces = 6;
                break;
            default:
                gamePieces = 6;
        }

        notFirstPlay(player);



        /*
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
        */

    }

    //METODO PARA SALVAR JOGO
    public void SaveGame(String fileNum) throws FileNotFoundException {

        String res = "";
        ArrayList<Piece> listOfPieces;
        //LINHA 1 NUMERO DE JOGADORES
        int plNum = player.size();
        String line1 = Integer.toString(plNum) + "\n";
        //LINHA 2 PEÇAS DA MESA
        //PEGAR DADOS DA MESA
        String gameTable = "";
        listOfPieces = t.getPiecesInTable();
        for (Piece p : listOfPieces
                ) {
            gameTable = gameTable + p.sideA + ";" + p.sideB + ";";
        }
        gameTable = gameTable + "\n";
        res = res + line1 + gameTable;
        //DADOS DE CADA JOGADOR
        String playerName, playerHand, playerPoints;
        for (Player p: player
                ) {
            playerName = p.Description() + "\n";
            playerPoints = Integer.toString(p.getPoints())+ "\n";
            playerHand = "";
            listOfPieces = p.ReturnHand();
            for (Piece piece: listOfPieces
                    ) {
                playerHand = playerHand + piece.sideA + ";" + piece.sideB + ";";
            }
            playerHand = playerHand + "\n";
            res = res + playerName + playerHand + playerPoints;
        }

        //Salva o arquivo num arquivo txt
        String file = "save" + fileNum + ".txt";

        PrintWriter out = new PrintWriter(file);

        out.println(res);

        out.close();

    }


    //METODO QUE ENCERRA O JOGO
    private void endGame() {
        System.out.println("Termina o jogo aqui em Porto Alegre! (voz do Galvão Bueno)\nPontuação final:");

        for (Player pl: player
             ) {
            System.out.printf("%s: %d pontos\n",pl.Description(), pl.getPoints());
        }

        if (numOfPlayers == 4){
            int d1Points;
            int d2Points;
            System.out.println("Pontuação parcial das duplas:");
            d1Points = player.get(0).getPoints() + player.get(2).getPoints();
            System.out.printf("***Dupla %s e %s: %d pontos\n", player.get(0).Description(), player.get(2).Description(), d1Points);
            d2Points = player.get(1).getPoints() + player.get(3).getPoints();
            System.out.printf("***Dupla %s e %s: %d pontos\n", player.get(1).Description(), player.get(3).Description(), d2Points);

            if (d1Points > d2Points){
                System.out.printf("A dupla %s e %s é a vencedora\n", player.get(0).Description(), player.get(2).Description(), d1Points);
            }
            if (d2Points > d1Points){
                System.out.printf("A dupla %s e %s é a vencedora\n", player.get(1).Description(), player.get(3).Description(), d1Points);
            }

            if (d1Points == d2Points){
                System.out.printf("As duplas empataram!\n", player.get(0).Description(), player.get(2).Description(), d1Points);
            }

        }

        System.exit(0);
    }

}
