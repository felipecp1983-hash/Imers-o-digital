package app;

import java.time.LocalDateTime;
import java.util.*;

// Codigo feito por CrZFelipe - IBMR
public class EventService {
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event e) { events.add(e); }

    public List<Event> listOrdered() {
        List<Event> copy = new ArrayList<>(events);
        Collections.sort(copy, new Comparator<Event>() {
            public int compare(Event a, Event b) {
                boolean ap = a.dateTime().isBefore(LocalDateTime.now());
                boolean bp = b.dateTime().isBefore(LocalDateTime.now());
                if (ap != bp) return ap ? 1 : -1; // passados depois
                return a.dateTime().compareTo(b.dateTime());
            }
        });
        return copy;
    }

    public boolean join(Event e, Cliente c) { return e.participants().add(c); }
    public boolean leave(Event e, Cliente c) { return e.participants().remove(c); }

    public List<Event> eventsOf(Cliente c) {
        List<Event> r = new ArrayList<>();
        for (Event e : listOrdered()) {
            if (e.participants().contains(c)) r.add(e);
        }
        return r;
    }

    public List<Event> all() { return events; }
    public void clearAndAddAll(List<Event> list) {
        events.clear();
        events.addAll(list);
    }
}
