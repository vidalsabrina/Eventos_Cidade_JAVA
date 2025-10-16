import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SistemaEventos {
    private static final String ARQUIVO_DADOS = "events.data"; // Nome do arquivo (Obrigatório)
    
    private List<Evento> eventos;
    private Usuario usuarioLogado; // Apenas 1 usuário para simplificar a entrega

    public SistemaEventos() {
        this.eventos = new ArrayList<>();
        carregarEventos(); // Carrega ao iniciar (Obrigatório)
    }

    // --- CADASTRO E LOGIN DE USUÁRIO ---
    public void cadastrarUsuario(String nome, String email, String telefone) {
        this.usuarioLogado = new Usuario(nome, email, telefone);
        System.out.println("Usuário cadastrado/logado com sucesso: " + usuarioLogado.getNome());
    }
    
    public boolean usuarioEstaLogado() {
        return usuarioLogado != null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    // --- CADASTRO DE EVENTOS ---
    public void adicionarEvento(Evento evento) {
        eventos.add(evento);
        salvarEventos(); // Salva após adicionar
        System.out.println("Evento '" + evento.getNome() + "' cadastrado com sucesso!");
    }

    // --- CONSULTA DE EVENTOS ---
    public void exibirTodosEventos() {
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }

        // Ordena por horário (Obrigatório: eventos mais próximos)
        List<Evento> eventosOrdenados = eventos.stream()
            .sorted(Comparator.comparing(Evento::getHorario))
            .collect(Collectors.toList());

        System.out.println("\n--- EVENTOS CADASTRADOS (Ordenados por Horário) ---");
        eventosOrdenados.forEach(evento -> {
            System.out.println(evento);
        });
        System.out.println("--------------------------------------------------\n");
    }

    // --- PARTICIPAÇÃO EM EVENTOS ---
    public Optional<Evento> buscarEventoPorId(int id) {
        return eventos.stream().filter(e -> e.getId() == id).findFirst();
    }

    public void confirmarParticipacao(int eventoId) {
        if (!usuarioEstaLogado()) {
            System.out.println("Erro: Nenhum usuário logado.");
            return;
        }
        Optional<Evento> evento = buscarEventoPorId(eventoId);
        if (evento.isPresent()) {
            usuarioLogado.confirmarParticipacao(eventoId);
            System.out.println("Presença confirmada no evento: " + evento.get().getNome());
        } else {
            System.out.println("Evento com ID " + eventoId + " não encontrado.");
        }
    }
    
    public void cancelarParticipacao(int eventoId) {
        if (!usuarioEstaLogado()) {
            System.out.println("Erro: Nenhum usuário logado.");
            return;
        }
        Optional<Evento> evento = buscarEventoPorId(eventoId);
        if (evento.isPresent() && usuarioLogado.getEventosConfirmados().contains(eventoId)) {
            usuarioLogado.cancelarParticipacao(eventoId);
            System.out.println("Participação cancelada no evento: " + evento.get().getNome());
        } else if (!evento.isPresent()) {
            System.out.println("Evento com ID " + eventoId + " não encontrado.");
        } else {
            System.out.println("Você não estava confirmado neste evento.");
        }
    }

    public void exibirEventosConfirmados() {
        if (!usuarioEstaLogado()) {
            System.out.println("Erro: Nenhum usuário logado.");
            return;
        }
        
        System.out.println("\n--- MEUS EVENTOS CONFIRMADOS ---");
        List<Evento> confirmados = eventos.stream()
            .filter(e -> usuarioLogado.getEventosConfirmados().contains(e.getId()))
            .collect(Collectors.toList());

        if (confirmados.isEmpty()) {
            System.out.println("Você não confirmou presença em nenhum evento.");
        } else {
            confirmados.forEach(System.out::println);
        }
        System.out.println("----------------------------------\n");
    }

    // --- PERSISTÊNCIA DE DADOS (Obrigatório) ---
    // Salva a lista de eventos em um arquivo de texto 'events.data'
    private void salvarEventos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(eventos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    // Carrega a lista de eventos do arquivo de texto 'events.data' e corrige o contador de ID
    @SuppressWarnings("unchecked") 
    private void carregarEventos() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists() && arquivo.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                this.eventos = (List<Evento>) ois.readObject();
                System.out.println("Eventos carregados com sucesso de " + ARQUIVO_DADOS);
                
                // AJUSTE CRÍTICO: Redefine o contador de ID para o ID máximo existente.
                int maxId = eventos.stream().mapToInt(Evento::getId).max().orElse(0);
                Evento.ID_COUNTER.set(maxId);
                System.out.println("Contador de ID de Eventos ajustado para: " + maxId);
                
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erro ao carregar eventos. Criando nova lista. Detalhe: " + e.getMessage());
                this.eventos = new ArrayList<>();
            }
        } else {
            System.out.println("Arquivo de eventos não encontrado ou vazio. Iniciando com lista vazia.");
            this.eventos = new ArrayList<>();
        }
    }
}
