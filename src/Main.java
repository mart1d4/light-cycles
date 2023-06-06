import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private final static char blockedChar = 'H';
    private final static char bonusChar = 'F';
    private final static char firstPlayerChar = 'X';
    private final static char firstPlayerColor = 'B';
    private final static char secondPlayerChar = 'O';
    private final static char secondPlayerColor = 'R';

    private final static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        // Create table and instantiate it with blank characters
        int tableSize = 10;
        char[][] table = new char[tableSize][tableSize];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                table[i][j] = ' ';
            }
        }

        addPropToTable(table, 7, blockedChar);
        addPropToTable(table, 3, bonusChar);

        // Add the bikes to the table
        int[] firstPlayerCoords = { 0, 0 };
        int[] secondPlayerCoords = { table.length -1 , table.length -1 };

        table[firstPlayerCoords[0]][firstPlayerCoords[1]] = firstPlayerChar;
        table[secondPlayerCoords[0]][secondPlayerCoords[1]] = secondPlayerChar;

        int currentPlayer = 1;
        int isGameEnded;

        do {
            int[] currentCords;
            if (currentPlayer == 1) {
                currentCords = firstPlayerCoords;
            } else {
                currentCords = secondPlayerCoords;
            }

            printTable(table);
            LinkedList<String> directions = getAvailableDirections(table, currentCords);

            char symbol;
            if (currentPlayer == 1) {
                symbol = firstPlayerChar;
            } else {
                symbol = secondPlayerChar;
            }

            System.out.println("Player " + currentPlayer + ", where do you want to go? (Your symbol is " + symbol + ")");
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

            makeMove(table, currentPlayer, currentCords, capitalize(userInput));

            isGameEnded = isGameEnded(table, currentPlayer, currentCords);

            if (currentPlayer == 1) {
                currentPlayer = 2;
            } else {
                currentPlayer = 1;
            }
        }
        while (isGameEnded == 0);

        printTable(table);
        System.out.println("Congratulations! Player " + isGameEnded + " won this game.\nDo you want to play again?");
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static void makeMove(char[][] table, int player, int[] coords, String move) {
        int i = coords[0];
        int j = coords[1];

        char charToAdd;
        char colorToAdd;
        if (player == 1) {
            charToAdd = firstPlayerChar;
            colorToAdd = firstPlayerColor;
        } else {
            charToAdd = secondPlayerChar;
            colorToAdd = secondPlayerColor;
        }

        table[i][j] = colorToAdd;

        if (Objects.equals(move, "Left")) {
            table[i][j-1] = charToAdd;
            coords[1] = j - 1;
        } else if (Objects.equals(move, "Right")) {
            table[i][j+1] = charToAdd;
            coords[1] = j + 1;
        } else if (Objects.equals(move, "Up")) {
            table[i-1][j] = charToAdd;
            coords[0] = i - 1;
        } else if (Objects.equals(move, "Down")) {
            table[i+1][j] = charToAdd;
            coords[0] = i + 1;
        } else {
            System.out.println("Wrong move.");
        }
    }

    public static void addPropToTable(char[][] table, int amount, char prop) {
        // Adds the specified amount of prop to the table
        for (int i = 0; i < amount; i++) {
            int iRand = (int)(Math.random() * table.length);
            int jRand = (int)(Math.random() * table.length);

            while (
                    table[iRand][jRand] == blockedChar
                    || table[iRand][jRand] == bonusChar
                    || (iRand == 0 && jRand == 0)
                    || (iRand == table.length - 1 && jRand == table.length - 1)
            ) {
                iRand = (int)(Math.random() * table.length);
                jRand = (int)(Math.random() * table.length);
            }

            table[iRand][jRand] = prop;
        }
    }

    public static LinkedList<String> getAvailableDirections(char[][] table, int[] coords) {
        int i = coords[0];
        int j = coords[1];

        LinkedList<String> directions = new LinkedList<>();

        if (i < table.length - 1 && table[i+1][j] == ' ') {
            directions.add("Down");
        }

        if (i > 0 && table[i-1][j] == ' ') {
            directions.add("Up");
        }

        if (j < table.length - 1 && table[i][j+1] == ' ') {
            directions.add("Right");
        }

        if (j > 0 && table[i][j-1] == ' ') {
            directions.add("Left");
        }

        return directions;
    }

    public static void printTable(char[][] table)
    {
        for (char[] chars : table) {
            // Display the line separators
            for (int i = 0; i < chars.length; i++){
                System.out.print("+---");
            }
            System.out.println("+");

            // Display the table's content
            for (int j = 0; j < table.length; j++) {
                if (chars[j] == 'B') {
                    // Make background appear blue - https://stackoverflow.com/questions/5762491
                    System.out.print("|" + "\u001B[44m" + "   " + "\u001B[0m");
                } else if (chars[j] == 'R') {
                    // Make background appear red
                    System.out.print("|" + "\u001B[41m" + "   " + "\u001B[0m");
                } else {
                    System.out.print("| " + chars[j] + " ");
                }
            }
            System.out.println("|");
        }

        for (int i = 0; i < table.length; i++){
            System.out.print("+---");
        }
        System.out.println("+\n");
    }

    public static int isGameEnded(char[][] table, int player, int[] coords) {
        // Returns 0 if the game needs to still be going
        // Returns 1 if player 1 won (game ended)
        // Returns 2 if player 2 won (game ended)

        // Game ends if a player can't move,
        // so let's check the specified player's coords
        // to see if he can make a move
        LinkedList<String> directions = getAvailableDirections(table, coords);

        if (directions.isEmpty()) {
            // The player can't move, he lost and the game needs to end
            if (player == 1) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }
}
