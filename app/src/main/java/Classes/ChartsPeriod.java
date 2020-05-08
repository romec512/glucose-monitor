package Classes;

public enum ChartsPeriod {
    LAST_DAY(1),
    LAST_WEEK(7),
    LAST_MONTH(30);

    private int period;

    ChartsPeriod(int numOfDays) {
        this.period = numOfDays;
    }

    public int getPeriod() { return this.period; }
}
