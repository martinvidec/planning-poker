package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.state.ApplicationState;
import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BroadcastHelper {

    private static Executor executor = Executors.newSingleThreadExecutor();
    private static LinkedList<Consumer<String>> listeners = new LinkedList<>();
    private static ApplicationState applicationState = new ApplicationState();

    public static synchronized Registration register(Consumer<String> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (BroadcastHelper.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(String message) {
        // modify Application State
        applicationState.modify(message);

        // propagate ApplicationState
        for (Consumer<String> listener : listeners) {
            executor.execute(() -> listener.accept(applicationState.toString()));
        }
    }
}
