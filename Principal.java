import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Principal {
    private static SistemaEventos sistema;
    private static Scanner scanner;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        sistema = new SistemaEventos();
        scanner = new Scanner(System.in);
        
        // Garante que o usuário seja cadastrado/logado no início
        if (!sistema.usuarioEstaLogado()) {
            System.out.println("BEM-VINDO AO SISTEMA DE EVENTOS DA CIDADE!");
            cadastrarUsuarioInicial();
        }

        int opcao;
        do {
            exibirMenuPrincipal();
            try {
                System.out.print("Escolha uma opção: ");
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir a nova linha

                switch (opcao) {
                    case 1:
                        sistema.exibirTodosEventos();
                        break;
                    case 2:
                        menuCadastroEvento();
                        break;
                    case 3:
                        sistema.exibirTodosEventos();
                        menuParticipacao();
                        break;
                    case 4:
                        menuMeusEventos();
                        break;
                    case 0:
                        System.out.println("Saindo do sistema. Até mais!");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine(); // Limpar buffer do scanner
                opcao = -1;
            }
        } while (opcao != 0);
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n========== MENU PRINCIPAL ==========");
        System.out.println("Usuário Logado: " + (sistema.usuarioEstaLogado() ? sistema.getUsuarioLogado().getNome() : "N/A"));
        System.out.println("1. Visualizar todos os eventos");
        System.out.println("2. Cadastrar novo evento");
        System.out.println("3. Confirmar participação em um evento");
        System.out.println("4. Meus eventos confirmados");
        System.out.println("0. Sair");
        System.out.println("====================================");
    }
    
    private static void cadastrarUsuarioInicial() {
        System.out.println("\n--- CADASTRO INICIAL DE USUÁRIO ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        sistema.cadastrarUsuario(nome, email, telefone);
        System.out.println("-----------------------------------\n");
    }

    private static void menuCadastroEvento() {
        System.out.println("\n--- CADASTRO DE EVENTO ---");
        System.out.print("Nome do evento: ");
        String nome = scanner.nextLine();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();

        CategoriaEvento categoria = escolherCategoria();

        LocalDateTime horario = null;
        boolean horarioValido = false;
        while (!horarioValido) {
            System.out.print("Horário (Formato dd/MM/yyyy HH:mm, ex: 31/12/2025 20:00): ");
            String horarioStr = scanner.nextLine();
            try {
                horario = LocalDateTime.parse(horarioStr, DATETIME_FORMATTER);
                horarioValido = true;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Formato de data e hora inválido. Tente novamente.");
            }
        }

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        Evento novoEvento = new Evento(nome, endereco, categoria, horario, descricao);
        sistema.adicionarEvento(novoEvento);
    }
    
    private static CategoriaEvento escolherCategoria() {
        CategoriaEvento[] categorias = CategoriaEvento.values();
        int escolha = -1;
        while (escolha < 1 || escolha > categorias.length) {
            System.out.println("Categorias disponíveis:");
            for (int i = 0; i < categorias.length; i++) {
                System.out.println((i + 1) + ". " + categorias[i]);
            }
            System.out.print("Escolha a categoria (número): ");
            try {
                escolha = scanner.nextInt();
                scanner.nextLine();
                if (escolha >= 1 && escolha <= categorias.length) {
                    return categorias[escolha - 1];
                } else {
                    System.out.println("Número de categoria inválido.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite o número da categoria.");
                scanner.nextLine(); // Limpar buffer
            }
        }
        return CategoriaEvento.OUTROS; // Não deve ser alcançado, mas é um fallback
    }

    private static void menuParticipacao() {
        if (!sistema.usuarioEstaLogado()) return;
        
        System.out.print("\nDigite o ID do evento para confirmar presença (ou 0 para voltar): ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();
            if (id > 0) {
                sistema.confirmarParticipacao(id);
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Digite apenas o número do ID.");
            scanner.nextLine();
        }
    }

    private static void menuMeusEventos() {
        if (!sistema.usuarioEstaLogado()) return;
        
        sistema.exibirEventosConfirmados();
        
        if (sistema.getUsuarioLogado().getEventosConfirmados().isEmpty()) {
            System.out.println("Pressione Enter para voltar.");
            scanner.nextLine();
            return;
        }
        
        System.out.println("1. Cancelar participação em um evento");
        System.out.println("0. Voltar ao menu principal");
        System.out.print("Escolha uma opção: ");
        
        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            
            if (opcao == 1) {
                System.out.print("Digite o ID do evento que deseja cancelar a participação: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                sistema.cancelarParticipacao(id);
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Digite apenas números.");
            scanner.nextLine();
        }
    }
}
