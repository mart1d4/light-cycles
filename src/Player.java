public class Player {
    private final String name;
    private final String symbol;
    private final String color;
    private int[] position;
    private int bonuses;
    private int victories;
    private int defeats;

    public Player(
        String name,
        String symbol,
        String color,
        int[] position,
        int bonuses,
        int victories,
        int defeats
    ) {
        this.name = name;
        this.symbol = symbol;
        this.color = color;
        this.position = position;
        this.bonuses = bonuses;
        this.victories = victories;
        this.defeats = defeats;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getColor() {
        return this.color;
    }

    public int[] getPosition() {
        return this.position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int getIPos() {
        return this.position[0];
    }

    public int getJPos() {
        return this.position[1];
    }

    public int getBonuses() {
        return this.bonuses;
    }

    public void addBonus() {
        this.bonuses++;
    }

    public void removeBonus() {
        this.bonuses--;
    }

    public int getVictories() {
        return this.victories;
    }

    public void addVictory() {
        this.victories++;
    }

    public int defeats() {
        return this.defeats;
    }

    public void addDefeat() {
        this.defeats++;
    }
}
