package domain;

import java.time.LocalDate;

/**
 * Created by Ihar_Niakhlebau on 17-Aug-15.
 */
public class SubscriberSession {

    private int id;

    private LocalDate connectionDate;

    private LocalDate disconnectionDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getConnectionDate() {
        return connectionDate;
    }

    public void setConnectionDate(LocalDate connectionDate) {
        this.connectionDate = connectionDate;
    }

    public LocalDate getDisconnectionDate() {
        return disconnectionDate;
    }

    public void setDisconnectionDate(LocalDate disconnectionDate) {
        this.disconnectionDate = disconnectionDate;
    }
}
