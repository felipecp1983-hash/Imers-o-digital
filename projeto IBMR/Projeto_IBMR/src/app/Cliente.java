package app;

import java.util.Objects;

// Codigo feito por CrZFelipe - IBMR
public class Cliente {
    private String nome;
    private String endereco;
    private String categoriaPreferida; // pode ser nulo

    public Cliente(String nome, String endereco, String categoriaPreferida) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoriaPreferida = categoriaPreferida;
    }

    public String nome() { return nome; }
    public String endereco() { return endereco; }
    public String categoriaPreferida() { return categoriaPreferida; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente c)) return false;
        return Objects.equals(nome, c.nome) && Objects.equals(endereco, c.endereco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, endereco);
    }

    @Override
    public String toString() {
        return nome + " (" + endereco + ")";
    }
}
