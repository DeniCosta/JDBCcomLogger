package jdbc;

import java.util.Scanner;


public class Principal {
	public static void main(String[] args) {
		
		// cria um objeto da classe BancoDados
		BancoDados db = new BancoDados();
		Scanner sc = new Scanner(System.in);

		// informações de conexão com o banco de dados
		String db_url = "jdbc:mysql://localhost:3306/reuniao";
		String db_user = "root";
		String db_password = "";
		String db_query = "";

		// cria a conexão com o banco de dados
		System.out.println("Conectando ao banco de dados...");
		db.conectar(db_url, db_user, db_password);
		
		boolean continuar = true;
		int totalLinhasAfetadas = 0; // variável para armazenar o total de linhas afetadas

		while (continuar) {

			// solicita a operação desejada
			System.out.println("Escolha uma opção:");
			System.out.println("1 - Consultar usuários");
			System.out.println("2 - Inserir novo usuário");
			System.out.println("3 - Alterar usuário");
			System.out.println("4 - Excluir usuário");

			totalLinhasAfetadas += db.inserirAlterarExcluir(db_query);
			
			// pergunta se deseja realizar outra operação
			System.out.println("Deseja realizar outra operação? (s/n)");

			// laço para garantir que o usuário digite a resposta correta
			boolean respostaValida = false;
			while (!respostaValida) {
			    String resposta = sc.next().toLowerCase();
			    if (resposta.startsWith("s")) {
			        continuar = true;
			        respostaValida = true;
			    } else if (resposta.startsWith("n")) {
			        continuar = false;
			        respostaValida = true;
			    } else {
			        System.out.println("Opção inválida. Digite 's' para Sim ou 'n' para Não.");
			    }
			}

		}
		System.out.println("Total de linhas afetadas: " + totalLinhasAfetadas); // exibe o total de linhas afetadas
		db.desconectar();
		sc.close();
	}
}
