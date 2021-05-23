package at.videc.opensource.scrum.broadcast.helper;

import at.videc.opensource.scrum.broadcast.BroadcastMessage;
import at.videc.opensource.scrum.state.ApplicationStateDto;
import at.videc.opensource.scrum.state.ApplicationStateHolder;
import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BroadcastHelper {

    private static Executor executor = Executors.newSingleThreadExecutor();
    private static LinkedList<Consumer<ApplicationStateDto>> listeners = new LinkedList<>();
    private static ApplicationStateHolder applicationStateHolder = new ApplicationStateHolder();

    private BroadcastHelper() {}

    public static synchronized Registration register(Consumer<ApplicationStateDto> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (BroadcastHelper.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(BroadcastMessage broadcastMessage) {
        // modify Application State
        applicationStateHolder.modify(broadcastMessage);

        // propagate ApplicationState
        for (Consumer<ApplicationStateDto> listener : listeners) {
            executor.execute(() -> listener.accept(applicationStateHolder.getApplicationStateDto()));
        }
    }
}
