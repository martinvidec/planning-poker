package at.videc.opensource.scrum.broadcast;

import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Broadcaster {

    static Executor executor = Executors.newSingleThreadExecutor();
    static LinkedList<Consumer<String>> listeners = new LinkedList<>();
    static Map<String, Integer> values = new HashMap<>();

    public static synchronized Registration register(Consumer<String> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(String message) {
        String[] payloads = message.split(":");
        String username = payloads[0];
        Integer value = Integer.valueOf(payloads[1]);

        if(value > 0) {
            values.put(username, Integer.valueOf(value));
            for (Consumer<String> listener : listeners) {
                executor.execute(() -> listener.accept(values.entrySet().stream().map(entry -> { return entry.getKey() + ":[DONE]"; }).collect(Collectors.joining(","))));
            }
        } else if (value == -4) {
            values.clear();
            for (Consumer<String> listener : listeners) {
                executor.execute(() -> listener.accept("clear"));
            }
        } else if (value == -5) {
            for (Consumer<String> listener : listeners) {
                executor.execute(() -> listener.accept(values.entrySet().stream().map(entry -> { return entry.getKey() + ":" + entry.getValue(); }).collect(Collectors.joining(","))));
            }
        }


    }
}
