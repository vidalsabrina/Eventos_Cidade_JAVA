import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Usuario implements Serializable {
    // Usado para persistência de objetos em arquivo (events.data)
    private static final long serialVersionUID = 1L; 
    
    private String nome;
    private String email;
    private String telefone; // Mínimo 3 atributos
    
    // Conjunto de IDs dos eventos que o usuário confirmou participação
    private Set<Integer> eventosConfirmados; 

    // Construtor
    public Usuario(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.eventosConfirmados = new HashSet<>();
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
    
    public Set<Integer> getEventosConfirmados() {
        return eventosConfirmados;
    }

    // Métodos de participação
    public void confirmarParticipacao(int eventoId) {
        this.eventosConfirmados.add(eventoId);
    }

    public void cancelarParticipacao(int eventoId) {
        this.eventosConfirmados.remove(eventoId);
    }
    
    // toString para exibição
    @Override
    public String toString() {
        return "Nome: " + nome + ", E-mail: " + email;
    }
}
