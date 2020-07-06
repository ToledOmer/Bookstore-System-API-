package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderSchedule implements Serializable {
    private Integer tick;
    private String bookTitle;

    public OrderSchedule(Integer tick, String bookTitle) {
        this.tick = tick;
        this.bookTitle = bookTitle;
    }
    public Integer getTick() {
        return tick;
    }

    public String getBookTitle() {
        return bookTitle;
    }

}
