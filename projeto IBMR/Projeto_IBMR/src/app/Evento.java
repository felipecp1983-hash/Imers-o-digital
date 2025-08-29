package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

// Codigo feito por CrZFelipe - IBMR
public class Event {
    private String name;
    private String address;
    private Category category;
    private LocalDateTime dateTime;
    private String description;
    private Set<Cliente> participants = new LinkedHashSet<>();

    public Event(String name, String address, Category category, LocalDateTime dateTime, String description) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.dateTime = dateTime;
        this.description = description;
    }

    public String name() { return name; }
    public String address() { return address; }
    public Category category() { return category; }
    public LocalDateTime dateTime() { return dateTime; }
    public String description() { return description; }
    public Set<Cliente> participants() { return participants; }

    public String pretty() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String when = dateTime.format(fmt);
        return String.format("[%s] %s @ %s | %s | %d participante(s) | %s",
                category, name, address, when, participants.size(), description);
    }
}
