package net.ice.relic.core;

import java.util.ArrayDeque;
import java.util.Deque;

@Deprecated
public class Timer {

    private long startTime;
    private long lastTime;
    private float deltaTime;

    private int scale = 0;

    private final Deque<Double> frameTimestamps = new ArrayDeque<>();
    private final double fpsWindowSize = 5.0;

    public void init() {
        startTime = System.nanoTime();
        lastTime = System.nanoTime();
    }

    public void newFrame() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) * 1E-9f;
        deltaTime = deltaTime * scale;
        lastTime = currentTime;

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

    public void setScale(int scale) {
        this.scale = scale;
    }
}
