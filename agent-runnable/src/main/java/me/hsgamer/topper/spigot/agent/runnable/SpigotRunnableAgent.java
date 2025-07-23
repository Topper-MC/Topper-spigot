package me.hsgamer.topper.spigot.agent.runnable;

import io.github.projectunified.minelib.scheduler.common.scheduler.Scheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import me.hsgamer.topper.agent.core.Agent;

public class SpigotRunnableAgent implements Agent {
    private final Runnable runnable;
    private final Scheduler scheduler;
    private final long interval;
    private Task task;

    public SpigotRunnableAgent(Runnable runnable, Scheduler scheduler, long interval) {
        this.runnable = runnable;
        this.scheduler = scheduler;
        this.interval = interval;
    }

    @Override
    public void start() {
        task = scheduler.runTimer(runnable, interval, interval);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
