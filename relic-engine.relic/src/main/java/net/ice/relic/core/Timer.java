package net.ice.relic.core;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class Timer {

    private long lastTime;
    private float deltaTime;

    private int scale = 0;

    private final Queue<Double> frameTimes = new LinkedList<>();

    public void init() {
        lastTime = System.nanoTime();
    }

    public void newFrame() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) * 1E-9f;
        deltaTime = deltaTime * scale;
        lastTime = currentTime;

        frameTimes.add(currentTime * 1E-9);
    }

    public double getAverageFrameTime() {
        while(!frameTimes.isEmpty() && (lastTime - frameTimes.peek() > 5000)) {
            frameTimes.poll();
        }

        int count = frameTimes.size();
        if(count <= 1) {
            return 0;
        }

        long timeSpanMs = (long) (lastTime - frameTimes.peek());

        if(timeSpanMs <= 0) {
            return count;
        }

        return ((double) count / timeSpanMs) * 1000;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
