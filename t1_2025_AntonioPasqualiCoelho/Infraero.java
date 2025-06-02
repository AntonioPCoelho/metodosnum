import java.io.*;
import java.util.*;

/*
 * Trabalho 1 de Metodos Numericos do primeiro semestre de 2025
 * Professor Joao Oliveira
 * @author Antonio Pasquali Coelho
 * @version 5 de maio de 2025
 */

public class Infraero {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        String filePath = "caso150.txt"; // Arquivo de entrada

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Map<String, Integer> passageirosIniciais = new HashMap<>();
            Map<String, Map<String, Double>> fluxoPorcentagens = new HashMap<>();
            String linha;

            // Leitura do arquivo
            while ((linha = br.readLine()) != null) {
                String[] tokens = linha.split("\\s+");
                if (tokens.length == 2) {
                    // Linha com aeroporto e passageiros iniciais
                    String aeroporto = tokens[0];
                    int passageiros = Integer.parseInt(tokens[1]);
                    passageirosIniciais.put(aeroporto, passageiros);
                } else if (tokens.length == 3) {
                    // Linha com porcentagem de fluxo entre aeroportos
                    String de = tokens[0];
                    String para = tokens[1];
                    double porcentagem = Double.parseDouble(tokens[2]) / 100.0;

                    fluxoPorcentagens.putIfAbsent(de, new HashMap<>());
                    fluxoPorcentagens.get(de).put(para, porcentagem);
                }
            }

            // Construcao do sistema linear
            List<String> aeroportos = new ArrayList<>(passageirosIniciais.keySet());
            int n = aeroportos.size();
            double[][] coef = new double[n][n];
            double[] constantes = new double[n];

            System.out.println("Construcao do sistema linear:");
            for (int i = 0; i < n; i++) {
                String aeroporto = aeroportos.get(i);
                constantes[i] = passageirosIniciais.getOrDefault(aeroporto, 0);

                for (int j = 0; j < n; j++) {
                    String outroAero = aeroportos.get(j);
                    if (aeroporto.equals(outroAero)) {
                        coef[i][j] = 1.0;
                    } else {
                        coef[i][j] -= fluxoPorcentagens.getOrDefault(outroAero, new HashMap<>())
                                .getOrDefault(aeroporto, 0.0);
                    }
                }

                // Imprime a equacao correspondente ao aeroporto
                System.out.print("Equacao para " + aeroporto + ": ");
                for (int j = 0; j < n; j++) {
                    System.out.printf("%.2f * %s ", coef[i][j], aeroportos.get(j));
                    if (j < n - 1) System.out.print("+ ");
                }
                System.out.printf("= %.2f%n", constantes[i]);
                System.out.println("-------------------------------");
            }

            // Resolucao do sistema linear
            double[] res = decomposicaoLU(coef, constantes);

            System.out.println("\nQuantidade de passageiros nos aeroportos:");
            for (int i = 0; i < n; i++) {
                System.out.printf("%s: %.2f%n", aeroportos.get(i), res[i]);
                
            }
            System.out.println("-------------------------------");

            // Identificacao do menor e maior numero de passageiros
            double minPassageiros = Double.MAX_VALUE;
            double maxPassageiros = Double.MIN_VALUE;
            String minAeroporto = "", maxAeroporto = "";

            for (int i = 0; i < n; i++) {
                if (res[i] < minPassageiros) {
                    minPassageiros = res[i];
                    minAeroporto = aeroportos.get(i);
                }
                if (res[i] > maxPassageiros) {
                    maxPassageiros = res[i];
                    maxAeroporto = aeroportos.get(i);
                }
            }

            // Exibicao dos resultados mais importantes
            System.out.println("Aeroporto com menor numero de passageiros: " + minAeroporto + " (" + minPassageiros + ")");
            System.out.println("Aeroporto com maior numero de passageiros: " + maxAeroporto + " (" + maxPassageiros + ")");

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    // Metodo para resolver o sistema linear usando decomposicao LU 
    //Referencia Wikipedia -> https://en.wikipedia.org/wiki/LU_decomposition
    private static double[] decomposicaoLU(double[][] coef, double[] constantes) {
        int n = constantes.length;

        // Matrizes L e U
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];

        // Decomposicao LU
        for (int i = 0; i < n; i++) {
            // Preenchendo a matriz U
            for (int j = i; j < n; j++) {
                double soma = 0.0;
                for (int k = 0; k < i; k++) {
                    soma += L[i][k] * U[k][j];
                }
                U[i][j] = coef[i][j] - soma;
            }

            // Preenchendo a matriz L
            for (int j = i; j < n; j++) {
                if (i == j) {
                    L[i][i] = 1.0; // Diagonal principal de L eh 1
                } else {
                    double soma = 0.0;
                    for (int k = 0; k < i; k++) {
                        soma += L[j][k] * U[k][i];
                    }
                    L[j][i] = (coef[j][i] - soma) / U[i][i];
                }
            }
        }

        // Substituicao direta para resolver L * y = constantes
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double soma = 0.0;
            for (int j = 0; j < i; j++) {
                soma += L[i][j] * y[j];
            }
            y[i] = constantes[i] - soma;
        }

        // Substituicao regressiva para resolver U * x = y
        double[] res = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;
            for (int j = i + 1; j < n; j++) {
                soma += U[i][j] * res[j];
            }
            res[i] = (y[i] - soma) / U[i][i];
        }
        
        return res;
    }
}