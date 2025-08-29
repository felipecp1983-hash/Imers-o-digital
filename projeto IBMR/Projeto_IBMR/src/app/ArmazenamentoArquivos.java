package app;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Codigo feito por CrZFelipe - IBMR
public class FileStorage {
    private String fileName;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileStorage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() { return fileName; }

    public void save(EventService service) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Event e : service.all()) {
                String parts = "";
                for (Cliente c : e.participants()) {
                    if (!parts.isEmpty()) parts += ",";
                    parts += c.nome();
                }
                pw.printf("%s|%s|%s|%s|%s|%s%n",
                        esc(e.name()), esc(e.address()), e.category().name(),
                        e.dateTime().format(FMT), esc(e.description()), esc(parts));
            }
        } catch (IOException ex) {
            System.out.println("erro ao salvar: " + ex.getMessage());
        }
    }

    public void load(EventService service) {
        File f = new File(fileName);
        if (!f.exists()) return;

        List<Event> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = split(line);
                if (a.length < 6) continue;
                String name = unesc(a[0]);
                String addr = unesc(a[1]);
                Category cat = Category.valueOf(a[2]);
                LocalDateTime dt = LocalDateTime.parse(a[3], FMT);
                String desc = unesc(a[4]);
                String participants = unesc(a[5]);

                Event e = new Event(name, addr, cat, dt, desc);
                if (!participants.isBlank()) {
                    for (String n : participants.split(",")) {
                        if (!n.isBlank()) e.participants().add(new Cliente(n.trim(), "desconhecido", null));
                    }
                }
                list.add(e);
            }
            service.clearAndAddAll(list);
        } catch (IOException ex) {
            System.out.println("erro ao carregar: " + ex.getMessage());
        }
    }

    private String esc(String s) { return s.replace("\\", "\\\\").replace("|", "\\|"); }
    private String unesc(String s) { return s.replace("\\|", "|").replace("\\\\", "\\"); }
    private String[] split(String s) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean esc = false;
        for (char c : s.toCharArray()) {
            if (esc) { cur.append(c); esc = false; }
            else if (c == '\\') esc = true;
            else if (c == '|') { out.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
