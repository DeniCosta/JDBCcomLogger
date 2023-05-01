package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Formatter;

public class BancoDados implements InterfaceBancoDados {
	Scanner sc = new Scanner(System.in);
	private Connection conexao;
	Logger meuLogger = Logger.getLogger("MeuLogger");
    FileHandler fileHandler;
    
    public BancoDados() {
        try {
            fileHandler = new FileHandler("LoggerArquivos/Logtext.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            meuLogger.addHandler(fileHandler);
            meuLogger.setLevel(Level.ALL);
            meuLogger.log(Level.INFO, "\n O programa iniciou a chamada do banco de dados!");
        } catch (Exception e) {
            meuLogger.log(Level.SEVERE, "Erro ao iniciar banco de dados!:", e);
            e.printStackTrace();
        }
    }

	@Override
	public void conectar(String db_url, String db_user, String db_password) {
		try {
			// realiza a conexão com o banco de dados
			conexao = DriverManager.getConnection(db_url, db_user, db_password);
			System.out.println("Conexão bem sucedida!");
			meuLogger.log(Level.INFO, "\n Banco de dados conectado!");

		} catch (SQLException e) {
			System.out.println("Não foi possível conectar ao banco de dados.");
			meuLogger.log(Level.SEVERE, "Erro ao conectar ao banco de dados.", e);
	        
			e.printStackTrace();
		}
	}

	@Override
	public void desconectar() {
		try {
			conexao.close();
			System.out.println("Conexão encerrada com sucesso.");
			meuLogger.log(Level.INFO, "\n Banco de dados desconectado!");
		} catch (SQLException e) {
			System.out.println("Erro ao desconectar do banco de dados: " + e.getMessage());
			meuLogger.log(Level.SEVERE, "Erro ao desconectar o banco de dados.", e);
		}
	}

	@Override
	public void consultar(String db_query) {
		try {
			// cria o statement
			PreparedStatement stmt = conexao.prepareStatement(db_query="SELECT * FROM pessoa;");
			// executa a operação consulta
			ResultSet rs = stmt.executeQuery();
			meuLogger.log(Level.INFO, "\n Consulta realizada com sucesso!");

			// processar os resultados da consulta
			while (rs.next()) {
			    Formatter formatter = new Formatter();
			    formatter.format("%-10s %-20s %-30s %-20s", rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			    System.out.println(formatter);
			    formatter.close();
			}

			// fecha o statement e o resultado da consulta
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			meuLogger.log(Level.SEVERE, "Erro ao realizar consulta.", e);
			e.printStackTrace();
		}
	}

	@Override
	public int inserirAlterarExcluir(String db_query) {
		
		int linhasAfetadas = 0;
		int opcao = sc.nextInt();
		switch (opcao) {
		case 1:
			// consulta todos os usuários cadastrados
			consultar(db_query);
			break;
		case 2:

			// solicita as informações do novo usuário
			System.out.println("Informe o nome do novo usuário:");
			String nome = sc.next();

			System.out.println("Informe o e-mail do novo usuário:");
			String email = sc.next();

			System.out.println("Informe o cargo do novo usuário:");
			String cargo = sc.next();

			// cria a query SQL para inserir o novo usuário
			db_query = "INSERT INTO pessoa (nome, email, cargo) VALUES (?, ?, ?)";

			try {
				// prepara a query SQL para inserção
				PreparedStatement stmt = conexao.prepareStatement(db_query);

				// atribui os valores aos parâmetros da query
				stmt.setString(1, nome);
				stmt.setString(2, email);
				stmt.setString(3, cargo);

				//incrementa linhasAfetadas
				int rowsAffected = stmt.executeUpdate();
				linhasAfetadas += rowsAffected;
				meuLogger.log(Level.INFO, "\n Cadastro realizado com sucesso!");

				// fecha o statement
				stmt.close();

			} catch (SQLException e) {
				System.out.println("Erro ao inserir usuário: " + e.getMessage());
				meuLogger.log(Level.SEVERE, "Erro ao realizar cadastro.", e);
				
			}

			if (linhasAfetadas > 0) {
				System.out.println("Usuário inserido com sucesso!");
				System.out.println("Total de linhas afetadas: " + linhasAfetadas); // exibe o total de linhas afetadas
				
			} else {
				System.out.println("Não foi possível inserir o usuário.");
				
			}
			break;
		case 3:
			// solicita as informações do usuário a ser alterado
			System.out.println("Informe o ID do usuário a ser alterado:");
			// exibe tabela de usuários cadastrados
			consultar(db_query);
			int ID = sc.nextInt();

			// solicita as novas informações do usuário
			System.out.println("Informe o novo nome do usuário:");
			String novoNome = sc.next();
			System.out.println("Informe o novo email do usuário: ");
			String novoEmail = sc.next();
			System.out.println("Informe o novo cargo do usuário: ");
			String novoCargo = sc.next();

			// atualiza as informações do usuário no banco de dados
			db_query = "UPDATE pessoa SET nome = ?,email = ? , cargo = ? WHERE ID = ?";
			try {
				PreparedStatement stmt = conexao.prepareStatement(db_query);
				stmt.setString(1, novoNome);
				stmt.setString(2, novoEmail);
				stmt.setString(3, novoCargo);
				stmt.setInt(4, ID);
				
				//incrementa linhasAfetadas
				int rowsAffected = stmt.executeUpdate();
				linhasAfetadas += rowsAffected;
				meuLogger.log(Level.INFO, "\n Atualização realizada com sucesso!");
				
				if (rowsAffected > 0) {
					System.out.println("Usuário atualizado com sucesso!");
					System.out.println("Total de linhas afetadas: " + linhasAfetadas); // exibe o total de linhas afetadas
					
				} else {
					System.out.println("O ID do usuário informado não foi encontrado.");
				}
			} catch (SQLException e) {
				System.out.println("Erro ao atualizar usuário no banco de dados: " + e.getMessage());
				meuLogger.log(Level.SEVERE, "Erro ao realizar atualização.", e);
			}
			break;

		case 4:
			// solicita o ID do usuário a ser excluído
			System.out.println("Informe o ID do usuário a ser excluído:");

			// exibe tabela de usuários cadastrados
			consultar(db_query);
			int ID_exclusao = sc.nextInt();

			// exclui o usuário do banco de dados
			db_query = "DELETE FROM pessoa WHERE ID = ?";
			try {
				PreparedStatement stmt = conexao.prepareStatement(db_query);
				stmt.setInt(1, ID_exclusao);
				
				//incrementa linhasAfetadas
				int rowsAffected = stmt.executeUpdate();
				linhasAfetadas += rowsAffected;
				meuLogger.log(Level.INFO, "\n Exclusão realizada com sucesso!");
				
				if (rowsAffected > 0) {
					System.out.println("Usuário excluído com sucesso!");
					System.out.println("Total de linhas afetadas: " + linhasAfetadas); // exibe o total de linhas afetadas
				} else {
					System.out.println("O ID do usuário informado não foi encontrado.");
				}
			} catch (SQLException e) {
				System.out.println("Erro ao excluir usuário do banco de dados: " + e.getMessage());
				meuLogger.log(Level.SEVERE, "Erro ao realizar exclusão.", e);
			}
			break;

		}
		return linhasAfetadas;
		
	}
	
}
