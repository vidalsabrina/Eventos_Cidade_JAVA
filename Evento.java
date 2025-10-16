import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Evento implements Serializable {
    private static final long serialVersionUID = 1L; 
    
    // CONTADOR DE ID: Tornamos este campo público para ser ajustado no SistemaEventos 
    // após o carregamento dos dados, garantindo que novos eventos tenham IDs únicos.
    public static final AtomicInteger ID_COUNTER = new AtomicInteger(0); 
    
    private int id; 
    private String nome;
    private String endereco;
    private CategoriaEvento categoria;
    private LocalDateTime horario; // Uso de DateTime
    private String descricao;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Construtor
    public Evento(String nome, String endereco, CategoriaEvento categoria, LocalDateTime horario, String descricao) {
        this.id = ID_COUNTER.incrementAndGet(); // Define um ID único
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDateTime getHorario() {
        return horario;
    }
    
    // Método de status do evento (Ajustado)
    public String getStatus(LocalDateTime agora) {
        // Define uma duração fictícia de 2 horas para classificar como "Ocorrendo Agora"
        LocalDateTime fimDoEvento = horario.plusHours(2); 

        if (agora.isBefore(horario)) {
            // Evento ainda não começou
            if (agora.plusHours(1).isAfter(horario)) {
                return "[Ocorrendo em Breve]";
            }
            return "[Próximo]";
        } else if (agora.isAfter(horario) && agora.isBefore(fimDoEvento)) {
            // Evento começou, mas não terminou
            return "[OCORRENDO AGORA]"; // Informa se um evento está ocorrendo no momento
        } else {
            // Já terminou
            return "[Já Ocorreu]"; // Informa eventos que já ocorreram
        }
    }

    // toString para exibição
    @Override
    public String toString() {
        LocalDateTime agora = LocalDateTime.now();
        return String.format(
            "ID: %d | Nome: %s | Horário: %s | Categoria: %s | Endereço: %s | Status: %s\n" +
            "   Descrição: %s",
            id, nome, horario.format(FORMATTER), categoria, endereco, getStatus(agora), descricao
        );
    }
}
