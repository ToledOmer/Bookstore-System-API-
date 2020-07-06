package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.services.TimeService;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast {


    private int currTick ;
    private int duration ;
    public TickBroadcast(int tick, int duration) {
        currTick = tick;
        this.duration = duration;
    }

    public int getCurrTick() {
        return currTick;
    }

    public void setCurrTick(int currTick) {
        this.currTick = currTick;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
