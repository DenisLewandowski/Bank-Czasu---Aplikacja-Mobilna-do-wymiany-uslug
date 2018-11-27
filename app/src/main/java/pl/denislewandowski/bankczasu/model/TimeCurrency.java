package pl.denislewandowski.bankczasu.model;

public class TimeCurrency {
    private int timeCurrencyValue;
    private int timeInMinutes;

    public TimeCurrency(int timeCurrencyValue, int timeInMinutes) {
        this.timeCurrencyValue = timeCurrencyValue;
        this.timeInMinutes = timeInMinutes;
    }

    public int getTimeCurrencyValue() {
        return timeCurrencyValue;
    }

    public void setTimeCurrencyValue(int timeCurrencyValue) {
        this.timeCurrencyValue = timeCurrencyValue;
    }

    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(int timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }
}
