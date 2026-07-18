package net.ice.heirloom.event;

public abstract class Event {

    private boolean canceled = false;

    public void cancel() {
        this.canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
