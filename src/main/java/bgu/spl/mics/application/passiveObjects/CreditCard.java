package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class CreditCard implements Serializable {

    private Integer amount;

    public CreditCard(Integer amount, Integer number) {
        this.amount = amount;
        this.number = number;
    }

    private Integer number;

    public Integer getAmount() {
        return amount;
    }

    public Integer getNumber() {
        return number;
    }
    public void chargeAmount(Integer amount) {
        this.amount = this.amount - amount;
    }
}
