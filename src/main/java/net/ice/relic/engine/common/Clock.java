package net.ice.relic.engine.common;

import java.util.ArrayDeque;
import java.util.Deque;

public class Clock {

    private long startTime;
    private long lastTime;
    private float deltaTime;

    private final Deque<Double> frameTimestamps = new ArrayDeque<>();
    private final double fpsWindowSize = 5.0;

    public void timerInit() {
        startTime = System.nanoTime();
        lastTime = System.nanoTime();
    }

    public void updateTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) * 1E-9f;
        lastTime = currentTime;

        deltaTime = Math.min(deltaTime, 0.1f);

        double nowSeconds = currentTime * 1E-9;
        frameTimestamps.addLast(nowSeconds);
        while (!frameTimestamps.isEmpty() && nowSeconds - frameTimestamps.getFirst() > fpsWindowSize) {
            frameTimestamps.removeFirst();
        }
    }

    public double getAverageFrameTimes() {
        if (frameTimestamps.size() < 2) return 0.0;
        double duration = frameTimestamps.getLast() - frameTimestamps.getFirst();
        return duration > 0.0 ? frameTimestamps.size() / duration : 0.0;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

}
