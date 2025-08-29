package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

// Codigo feito por CrZFelipe - IBMR
public class ConsoleUI {

    private Scanner in = new Scanner(System.in);
    private EventService service = new EventService();
    private FileStorage storage = new FileStorage("events.data");
    private Cliente clienteAtual;

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    public void start() {
        storage.load(service);
        while (true) {
            limpar();
            tituloMatrix("CrZFeLipE");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Cadastrar evento");
            System.out.println("3 - Listar eventos");
            System.out.println("4 - Participar de evento");
            System.out.println("5 - Cancelar participação");
            System.out.println("6 - Meus eventos");
            System.out.println("7 - Salvar e sair");
            System.out.print("opcao: ");
            String op = in.nextLine().trim();

            if (op.equals("1")) cadastrarCliente();
            else if (op.equals("2")) cadastrarEvento();
            else if (op.equals("3")) listarEventos();
            else if (op.equals("4")) participar();
            else if (op.equals("5")) cancelar();
            else if (op.equals("6")) meusEventos();
            else if (op.equals("7")) { storage.save(service); System.out.println("salvo."); break; }
            else pausa("opcao invalida");
        }
    }

    private void tituloMatrix(String txt) {
        Random r = new Random();
        int w = 64;
        for (int i=0;i<3;i++) {
            String s = "";
            for (int j=0;j<w;j++) s += randomGlyph(r);
            System.out.println(ANSI_GREEN + s + ANSI_RESET);
        }
        String meio = " " + txt + " ";
        int pad = Math.max(0, (w - 2 - meio.length()) / 2);
        String top = "╔" + "═".repeat(w-2) + "╗";
        String mid = "║" + " ".repeat(pad) + meio + " ".repeat(Math.max(0,(w-2)-pad-meio.length())) + "║";
        String bot = "╚" + "═".repeat(w-2) + "╝";
        System.out.println(ANSI_GREEN + top + ANSI_RESET);
        System.out.println(ANSI_GREEN + mid + ANSI_RESET);
        System.out.println(ANSI_GREEN + bot + ANSI_RESET);
        for (int i=0;i<2;i++) {
            String s = "";
            for (int j=0;j<w;j++) s += randomGlyph(r);
            System.out.println(ANSI_GREEN + s + ANSI_RESET);
        }
        System.out.println("Bem-vindo! cadastre o cliente para começar");
        System.out.println();
    }

    private char randomGlyph(Random r) {
        char[] g = "01ｱｲｳｴｵｶｷｸｹｺﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓ".toCharArray();
        return g[r.nextInt(g.length)];
    }

    private void cadastrarCliente() {
        limpar();
        tituloMatrix("CrZFeLipE");
        System.out.println("Cadastro do cliente");
        System.out.print("nome: ");
        String n = in.nextLine();
        System.out.print("endereco: ");
        String e = in.nextLine();
        System.out.print("categoria preferida (pode deixar vazio): ");
        String p = in.nextLine();
        if (p.isBlank()) p = null;
        clienteAtual = new Cliente(n, e, p);
        pausa("cliente cadastrado");
    }

    private void cadastrarEvento() {
        precisaCliente();
        limpar();
        System.out.println("Novo evento");
        System.out.print("nome: ");
        String nome = in.nextLine();
        System.out.print("endereco: ");
        String end = in.nextLine();
        Category cat = escolherCategoria();
        LocalDateTime dt = lerDataHora("data e hora (dd/MM/yyyy HH:mm): ");
        System.out.print("descricao: ");
        String desc = in.nextLine();
        Event ev = new Event(nome, end, cat, dt, desc);
        service.addEvent(ev);
        pausa("ok");
    }

    private void listarEventos() {
        limpar();
        List<Event> lista = service.listOrdered();
        if (lista.size()==0) { pausa("nenhum evento"); return; }
        int i=1;
        for (Event e : lista) {
            System.out.printf("%02d) %s%n", i++, e.pretty());
        }
        pausa("");
    }

    private void participar() {
        precisaCliente();
        List<Event> lista = service.listOrdered();
        if (lista.isEmpty()) { pausa("nao ha eventos"); return; }
        listarEventos();
        System.out.print("numero do evento: ");
        int idx = lerIndice(lista.size());
        Event e = lista.get(idx);
        boolean ok = service.join(e, clienteAtual);
        pausa(ok ? "presenca confirmada" : "ja estava participando");
    }

    private void cancelar() {
        precisaCliente();
        List<Event> lista = service.listOrdered();
        if (lista.isEmpty()) { pausa("nao ha eventos"); return; }
        listarEventos();
        System.out.print("numero do evento: ");
        int idx = lerIndice(lista.size());
        Event e = lista.get(idx);
        boolean ok = service.leave(e, clienteAtual);
        pausa(ok ? "cancelado" : "voce nao estava nesse");
    }

    private void meusEventos() {
        precisaCliente();
        limpar();
        System.out.println("Eventos do cliente");
        List<Event> meus = service.eventsOf(clienteAtual);
        if (meus.isEmpty()) { pausa("nenhum"); return; }
        int i=1;
        for (Event e : meus) System.out.printf("%02d) %s%n", i++, e.pretty());
        pausa("");
    }

    private void precisaCliente() {
        if (clienteAtual == null) {
            System.out.println("precisa cadastrar o cliente antes");
            cadastrarCliente();
        }
    }

    private Category escolherCategoria() {
        System.out.println("Categorias:");
        for (Category c : Category.values()) System.out.println(" - " + c.name());
        System.out.print("escolha: ");
        while (true) {
            String v = in.nextLine().trim().toUpperCase(Locale.ROOT);
            try { return Category.valueOf(v); }
            catch (Exception ex) { System.out.print("invalida, tente de novo: "); }
        }
    }

    private LocalDateTime lerDataHora(String prompt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return LocalDateTime.parse(s, fmt); }
            catch (DateTimeParseException e) { System.out.println("formato errado"); }
        }
    }

    private int lerIndice(int size) {
        while (true) {
            try {
                int pos = Integer.parseInt(in.nextLine().trim());
                if (pos < 1 || pos > size) throw new NumberFormatException();
                return pos - 1;
            } catch (NumberFormatException e) {
                System.out.print("numero invalido, de novo: ");
            }
        }
    }

    private void pausa(String msg) {
        if (!msg.isBlank()) System.out.println(msg);
        System.out.print("ENTER...");
        in.nextLine();
    }

    private void limpar() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
