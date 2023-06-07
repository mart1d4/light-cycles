import java.util.*;

public class Main {
    private final static Scanner scan = new Scanner(System.in); // To get string inputs
    private final static char blockedChar = 'H';
    private final static char bonusChar = 'F';

    public static String[] colors = {
            "Black",
            "Red",
            "Green",
            "Yellow",
            "Blue",
            "Purple",
            "Cyan",
            "White"
    };

    public static LinkedList<Player> players = new LinkedList<>();
    public static LinkedList<String> playerSymbols = new LinkedList<>();
    public static LinkedList<String> playerColors = new LinkedList<>();

    public static void main(String[] args) {
        char[][] board = createBoard(); // Initialise game's board
        addPlayersToGame(board.length); // Initialise games' players

        // Add players' position to the board
        for (Player player: players) {
            board[player.getIPos()][player.getJPos()] = player.getSymbol().charAt(0);
        }

        int isGameEnded;
        int currentPlayer = 0;

        do {
            System.out.print("\033[H\033[2J");
            System.out.flush();

            Player player = players.get(currentPlayer);
            printBoard(board);
            LinkedList<String> directions = getAvailableDirections(board, player.getPosition(), player.getBonuses() > 0);

            System.out.println(player.getName() + ", where do you want to go? (Your symbol is " + player.getSymbol() + ")");
            System.out.print("Available directions [");

            for (int i = 0; i < directions.size(); i++) {
                if (i == directions.size() - 1) {
                    System.out.print(directions.get(i) + "]: ");
                } else {
                    System.out.print(directions.get(i) + " - ");
                }
            }

            String userInput = scan.next();
            while (!directions.contains(capitalize(userInput))) {
                System.out.print("Wrong direction given. Please enter it again: ");
                userInput = scan.next();
            }
            System.out.print("\n");

            makeMove(board, player, capitalize(userInput));
            isGameEnded = isGameEnded(board);

            if (currentPlayer == players.size() - 1) {
                currentPlayer = 0;
            } else {
                currentPlayer++;
            }
        }
        while (isGameEnded == -1);

        printBoard(board);
        System.out.println("Congratulations!\nUnfortunately, " + players.get(isGameEnded).getName() + " lost this game.\nDo you want to play again?");
    }

    public static char[][] createBoard() {
        System.out.print("Enter a size for the board (5-100): ");

        int boardSize = scan.nextInt();
        while (boardSize < 5 || boardSize > 100) {
            System.out.print("Invalid size. Please enter a correct board size (5-100): ");
            boardSize = scan.nextInt();
        }

        // Create board and instantiate it with blank characters
        char[][] board = new char[boardSize][boardSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = ' ';
            }
        }

        System.out.print("Enter the number of blocked places (0-" + boardSize + "): ");
        int blocked = scan.nextInt();
        while (blocked < 0 || blocked > boardSize) {
            System.out.print("Invalid number. Please enter a correct number (0-" + boardSize + "): ");
            blocked = scan.nextInt();
        }

        System.out.print("Enter the number of bonuses (0-" + boardSize + "): ");
        int bonuses = scan.nextInt();
        while (bonuses < 0 || bonuses > boardSize) {
            System.out.print("Invalid number. Please enter a correct number (0-" + boardSize + "): ");
            bonuses = scan.nextInt();
        }

        addPropBoard(board, blocked, blockedChar);
        addPropBoard(board, bonuses, bonusChar);

        return board;
    }

    public static void addPlayersToGame(int boardLength) {
        System.out.print("Enter the number of players (2-4): ");

        int numberOfPlayers = scan.nextInt();
        while (numberOfPlayers < 0 || numberOfPlayers > 4) {
            System.out.print("Invalid number. Please enter a correct number (2-4): ");
            numberOfPlayers = scan.nextInt();
        }

        createPlayers(numberOfPlayers, boardLength);
    }

    public static void createPlayers(int amount, int boardLength) {
        int[][] defaultPositions = {
                { 0, 0 },
                { boardLength - 1, 0 },
                { 0, boardLength - 1 },
                { boardLength - 1, boardLength - 1 }
        };

        for (int i = 1; i <= amount; i++) {
            System.out.print("\033[H\033[2J");
            System.out.flush();

            System.out.print("Player " + i + ", enter your name: ");

            String name = capitalize(scan.next());
            while (name.length() == 0 || nameTaken(name)) {
                System.out.print("Invalid name, enter it again: ");
                name = capitalize(scan.next());
            }

            System.out.print("Choose a symbol to be displayed as (1 char): ");

            String symbol = scan.next();
            while (symbol.length() != 1 || symbolTaken(symbol)) {
                System.out.print("Invalid name, enter it again: ");
                symbol = scan.next();
            }
            playerSymbols.add(symbol);

            System.out.print("Choose the color of your light [Black-Red-Green-Yellow-Blue-Purple-Cyan-White]: ");

            String color = capitalize(scan.next());
            while (!Arrays.asList(colors).contains(color) || colorTaken(color)) {
                System.out.print("Invalid color, enter it again: ");
                color = capitalize(scan.next());
            }
            playerColors.add(color);

            Player newPlayer = new Player(
                    name,
                    symbol,
                    color,
                    defaultPositions[i],
                    0,
                    0,
                    0
            );

            players.add(newPlayer);
            playerSymbols.add(newPlayer.getSymbol());
            playerColors.add(newPlayer.getColor());
        }
    }

    public static boolean nameTaken(String name) {
        for (Player player: players) {
            if (Objects.equals(player.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean symbolTaken(String symbol) {
        if (symbol.charAt(0) == blockedChar || symbol.charAt(0) == bonusChar) {
            return true;
        }

        return playerSymbols.contains(symbol);
    }

    public static boolean colorTaken(String color) {
        return playerColors.contains(color);
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static void makeMove(char[][] board, Player player, String move) {
        int i = player.getIPos();
        int j = player.getJPos();

        int index = Arrays.asList(colors).indexOf(player.getColor());
        board[i][j] = String.valueOf(index).charAt(0);

        if (Objects.equals(move, "Left")) {
            j--;
        } else if (Objects.equals(move, "Right")) {
            j++;
        } else if (Objects.equals(move, "Up")) {
            i--;
        } else if (Objects.equals(move, "Down")) {
            i++;
        }

        if (board[i][j] == bonusChar) {
            player.addBonus();
        } else if (board[i][j] == blockedChar) {
            if (player.getBonuses() > 0) {
                player.removeBonus();
            } else {
                return;
            }
        }

        int[] pos = { i, j };

        board[i][j] = player.getSymbol().charAt(0);
        player.setPosition(pos);
    }

    public static void addPropBoard(char[][] board, int amount, char prop) {
        // Adds the specified amount of prop to the board
        for (int i = 0; i < amount; i++) {
            int iRand = (int)(Math.random() * board.length - 8) + 8;
            int jRand = (int)(Math.random() * board.length - 8) + 8;

            while (board[iRand][jRand] == blockedChar || board[iRand][jRand] == bonusChar) {
                iRand = (int)(Math.random() * board.length - 8) + 8;
                jRand = (int)(Math.random() * board.length - 8) + 8;
            }

            board[iRand][jRand] = prop;
        }
    }

    public static LinkedList<String> getAvailableDirections(char[][] board, int[] pos, boolean hasBonus) {
        int i = pos[0];
        int j = pos[1];

        LinkedList<String> directions = new LinkedList<>();

        if (i < board.length - 1 && (board[i+1][j] == ' ' || board[i+1][j] == bonusChar || (hasBonus && !playerSymbols.contains(String.valueOf(board[i+1][j]))))) {
            directions.add("Down");
        }

        if (i > 0 && (board[i-1][j] == ' ' || board[i-1][j] == bonusChar || (hasBonus && !playerSymbols.contains(String.valueOf(board[i-1][j]))))) {
            directions.add("Up");
        }

        if (j < board.length - 1 && (board[i][j+1] == ' ' || board[i][j+1] == bonusChar || (hasBonus && !playerSymbols.contains(String.valueOf(board[i][j+1]))))) {
            directions.add("Right");
        }

        if (j > 0 && (board[i][j-1] == ' ' || board[i][j-1] == bonusChar || (hasBonus && !playerSymbols.contains(String.valueOf(board[i][j-1]))))) {
            directions.add("Left");
        }

        return directions;
    }

    public static void printBoard(char[][] board) {
        HashMap<Character, String> colorHashes = new HashMap<>() {{
            put('0', "\u001B[40m");
            put('1', "\u001B[41m");
            put('2', "\u001B[42m");
            put('3', "\u001B[43m");
            put('4', "\u001B[44m");
            put('5', "\u001B[45m");
            put('6', "\u001B[46m");
        }};

        for (char[] chars : board) {
            // Display the line separators
            for (int i = 0; i < chars.length; i++){
                System.out.print("+---");
            }
            System.out.println("+");

            // Display the board's content
            for (int j = 0; j < board.length; j++) {
                if (colorHashes.containsKey(chars[j])) {
                    System.out.print("|" + colorHashes.get(chars[j]) + "   " + "\u001B[0m");
                } else {
                    System.out.print("| " + chars[j] + " ");
                }
            }
            System.out.println("|");
        }

        for (int i = 0; i < board.length; i++){
            System.out.print("+---");
        }
        System.out.println("+\n");
    }

    public static int isGameEnded(char[][] board) {
        // Returns the index of the player who lost, if any
        // Returns -1 if the game can continue

        // Game ends if a player can't move,
        // so let's check if any player can't make a move.
        for (Player player : players) {
            LinkedList<String> directions = getAvailableDirections(board, player.getPosition(), player.getBonuses() > 0);

            if (directions.isEmpty()) {
                return players.indexOf(player);
            }
        }

        return -1;
    }

    public static char stringToChar(String string) {
        return string.charAt(0);
    }

    public static String charToString(char character) {
        return String.valueOf(character);
    }
}
