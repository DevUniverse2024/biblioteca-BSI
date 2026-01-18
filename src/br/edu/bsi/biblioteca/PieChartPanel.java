package br.edu.bsi.biblioteca;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PieChartPanel extends JPanel {

    // ===== Dados simulados =====
    private int emAndamento = 12;
    private int emAtraso = 5;
    private int finalizados = 8;

    public PieChartPanel() {
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int total = emAndamento + emAtraso + finalizados;
        if (total == 0) {
            return;
        }

        int largura = Math.min(getWidth(), getHeight()) - 40;
        int x = (getWidth() - largura) / 2;
        int y = 20;

        // Conversão para ângulos
        int anguloAndamento = Math.round((float) emAndamento / total * 360);
        int anguloAtraso = Math.round((float) emAtraso / total * 360);
        int anguloFinalizados = 360 - anguloAndamento - anguloAtraso;

        int inicio = 0;

        // ===== Em Andamento (Verde) =====
        g2.setColor(new Color(0, 153, 0));
        g2.fillArc(x, y, largura, largura, inicio, anguloAndamento);
        inicio += anguloAndamento;

        // ===== Em Atraso (Laranja) =====
        g2.setColor(new Color(255, 102, 0));
        g2.fillArc(x, y, largura, largura, inicio, anguloAtraso);
        inicio += anguloAtraso;

        // ===== Finalizados (Azul) =====
        g2.setColor(new Color(0, 0, 255));
        g2.fillArc(x, y, largura, largura, inicio, anguloFinalizados);
    }
}
