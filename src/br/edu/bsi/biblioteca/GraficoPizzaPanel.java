package br.edu.bsi.biblioteca;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GraficoPizzaPanel extends JPanel {

    private int emAndamento;
    private int emAtraso;
    private int finalizados;

    public GraficoPizzaPanel() {
        setOpaque(false);
        carregarDadosEmprestimos();
    }

    // ==========================================
    // BUSCA DADOS COM REGRA DE NEGÓCIO CORRETA
    // ==========================================
    private void carregarDadosEmprestimos() {

        String sql = """
            SELECT
                SUM(CASE 
                    WHEN status_emprestimo = 'EM ANDAMENTO'
                         AND data_prevista_devolucao >= CURRENT_DATE
                    THEN 1 ELSE 0 END) AS em_andamento,

                SUM(CASE 
                    WHEN status_emprestimo = 'EM ANDAMENTO'
                         AND data_prevista_devolucao < CURRENT_DATE
                    THEN 1 ELSE 0 END) AS em_atraso,

                SUM(CASE 
                    WHEN status_emprestimo IN ('FINALIZADO SEM ATRASO',
                                               'FINALIZADO COM ATRASO')
                    THEN 1 ELSE 0 END) AS finalizados
            FROM emprestimo
        """;

        try (Connection conn = Conexao.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                emAndamento = rs.getInt("em_andamento");
                emAtraso = rs.getInt("em_atraso");
                finalizados = rs.getInt("finalizados");
            }

        } catch (Exception e) {
            System.err.println("Erro ao carregar gráfico de empréstimos: " + e.getMessage());
        }
    }

    // ==========================================
    // DESENHO DO GRÁFICO DE PIZZA
    // ==========================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int total = emAndamento + emAtraso + finalizados;
        if (total == 0) {
            return;
        }

        int tamanho = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - tamanho) / 2;
        int y = (getHeight() - tamanho) / 2;
        int raio = tamanho / 2;

        int centroX = x + raio;
        int centroY = y + raio;

        int anguloAndamento = (int) Math.round(360.0 * emAndamento / total);
        int anguloAtraso = (int) Math.round(360.0 * emAtraso / total);
        int anguloFinalizados = 360 - anguloAndamento - anguloAtraso;

        int inicio = 0;

        // ===============================
        // EM ANDAMENTO
        // ===============================
        g2.setColor(new Color(0, 153, 0));
        g2.fillArc(x, y, tamanho, tamanho, inicio, anguloAndamento);
        desenharValor(g2, emAndamento, inicio, anguloAndamento, centroX, centroY, raio);
        inicio += anguloAndamento;

        // ===============================
        // EM ATRASO
        // ===============================
        g2.setColor(new Color(255, 102, 0));
        g2.fillArc(x, y, tamanho, tamanho, inicio, anguloAtraso);
        desenharValor(g2, emAtraso, inicio, anguloAtraso, centroX, centroY, raio);
        inicio += anguloAtraso;

        // ===============================
        // FINALIZADOS
        // ===============================
        g2.setColor(Color.BLUE);
        g2.fillArc(x, y, tamanho, tamanho, inicio, anguloFinalizados);
        desenharValor(g2, finalizados, inicio, anguloFinalizados, centroX, centroY, raio);
    }

    private void desenharValor(Graphics2D g2,
            int valor,
            int anguloInicio,
            int angulo,
            int centroX,
            int centroY,
            int raio) {

        if (valor == 0) {
            return;
        }

        double anguloMedio = Math.toRadians(anguloInicio + angulo / 2.0);

        int distancia = (int) (raio * 0.6);

        int xTexto = centroX + (int) (distancia * Math.cos(anguloMedio));
        int yTexto = centroY - (int) (distancia * Math.sin(anguloMedio));

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 14f));

        String texto = String.valueOf(valor);

        int larguraTexto = g2.getFontMetrics().stringWidth(texto);
        int alturaTexto = g2.getFontMetrics().getAscent();

        g2.drawString(texto, xTexto - larguraTexto / 2, yTexto + alturaTexto / 2);
    }

}
