/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.edu.bsi.biblioteca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Carlos
 */
public class DashboardView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardView.class.getName());

    /**
     * Creates new form DashboardView
     */
    public DashboardView() {

        initComponents();
        carregarCardsEmBackground();
        carregarAlertasEmBackground();
        carregarGraficoPizza();

    }

    private void carregarGraficoPizza() {
        panelGraficoPizza.removeAll();
        panelGraficoPizza.setLayout(new java.awt.BorderLayout());

        GraficoPizzaPanel grafico = new GraficoPizzaPanel();
        panelGraficoPizza.add(grafico, java.awt.BorderLayout.CENTER);

        panelGraficoPizza.revalidate();
        panelGraficoPizza.repaint();
    }
private void carregarAlertasEmBackground() {

    final String SQL_NAO_DEVOLVIDOS =
            "SELECT COUNT(*) AS qtd FROM emprestimo WHERE status_emprestimo = 'EM ANDAMENTO'";

    final String SQL_USO_ACERVO =
            "SELECT COUNT(*) AS qtd FROM acervo WHERE status = 'EMPRESTADO'";

    final String SQL_RESERVAS_ATIVAS =
            "SELECT COUNT(*) AS qtd FROM reserva WHERE status = 'ATIVA'";

    final String SQL_TOTAL_ACERVO =
            "SELECT COUNT(*) AS qtd FROM acervo";

    try (Connection conn = Conexao.getConnection()) {

        int naoDevolvidos = 0;
        int usoAcervo = 0;
        int reservasAtivas = 0;
        int totalAcervo = 0;

        // ---- Empréstimos em andamento ----
        try (PreparedStatement ps = conn.prepareStatement(SQL_NAO_DEVOLVIDOS);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                naoDevolvidos = rs.getInt("qtd");
            }
        }

        // ---- Uso do acervo ----
        try (PreparedStatement ps = conn.prepareStatement(SQL_USO_ACERVO);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                usoAcervo = rs.getInt("qtd");
            }
        }

        // ---- Reservas ativas ----
        try (PreparedStatement ps = conn.prepareStatement(SQL_RESERVAS_ATIVAS);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                reservasAtivas = rs.getInt("qtd");
            }
        }

        // ---- Total de livros do acervo ----
        try (PreparedStatement ps = conn.prepareStatement(SQL_TOTAL_ACERVO);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalAcervo = rs.getInt("qtd");
            }
        }

        // Atualiza valores numéricos
        jblNaoDevolvidos.setText(String.valueOf(naoDevolvidos));
        jblUsoAcervo.setText(String.valueOf(usoAcervo));
        jblAguardandoEmprestimo.setText(String.valueOf(reservasAtivas));

        // ===============================
        // ALERTA 1 – Não devolvidos
        // ===============================
        aplicarStatusAlerta(
                naoDevolvidos,
                totalAcervo,
                jblAlertaCor1,
                jblAlertaTexto1
        );

        // ===============================
        // ALERTA 2 – Uso do acervo
        // ===============================
        aplicarStatusAlerta(
                usoAcervo,
                totalAcervo,
                jblAlertaCor2,
                jblAlertaTexto2
        );

        // ===============================
        // ALERTA 3 – Reservas ativas
        // ===============================
        aplicarStatusAlerta(
                usoAcervo,
                reservasAtivas,
                jblAlertaCor3,
                jblAlertaTexto3
        );

    } catch (Exception e) {
        logger.log(java.util.logging.Level.SEVERE,
                "Erro ao carregar alertas do dashboard", e);

        JOptionPane.showMessageDialog(this,
                "Erro ao carregar alertas: " + e.getMessage());

        jblNaoDevolvidos.setText("0");
        jblUsoAcervo.setText("0");
        jblAguardandoEmprestimo.setText("0");
    }
}
private void aplicarStatusAlerta(int valor, int total,
                                 javax.swing.JLabel lblCor,
                                 javax.swing.JLabel lblTexto) {

    if (total == 0) {
        lblCor.setBackground(java.awt.Color.BLUE);
        lblTexto.setText("Normal");
        return;
    }

    double percentual = (valor * 100.0) / total;

    if (percentual < 50) {
        lblCor.setBackground(java.awt.Color.BLUE);
        lblTexto.setText("Normal");
    } else if (percentual <= 70) {
        lblCor.setBackground(java.awt.Color.YELLOW);
        lblTexto.setText("Atenção");
    } else {
        lblCor.setBackground(java.awt.Color.RED);
        lblTexto.setText("Crítico");
    }

    lblCor.setOpaque(true);
}


private void carregarCardsEmBackground() {
        // SQLs dos 4 cards
        final String SQL_TOTAL_TITULOS
                = "SELECT COUNT(*) AS qtd FROM titulo";
        final String SQL_TOTAL_EXEMPLARES
                = "SELECT COUNT(*) AS qtd FROM acervo";
        final String SQL_EXEMPLARES_DISPONIVEIS
                = "SELECT COUNT(*) AS qtd FROM acervo WHERE status = 'DISPONIVEL'";
        final String SQL_EMPRESTIMOS_ATIVOS
                = "SELECT COUNT(*) AS qtd FROM emprestimo "
                + "WHERE status_emprestimo IN ('EM ANDAMENTO','EM ATRASO')";

        try (Connection conn = Conexao.getConnection()) {

            // ---- Card 1: Total de Títulos Catalogados ----
            try (PreparedStatement ps = conn.prepareStatement(SQL_TOTAL_TITULOS); ResultSet rs = ps.executeQuery()) {
                int qtd = 0;
                if (rs.next()) {
                    qtd = rs.getInt("qtd");
                }
                lblCard13.setText(String.valueOf(qtd));
            }

            // ---- Card 2: Total de Exemplares no Acervo ----
            try (PreparedStatement ps = conn.prepareStatement(SQL_TOTAL_EXEMPLARES); ResultSet rs = ps.executeQuery()) {
                int qtd = 0;
                if (rs.next()) {
                    qtd = rs.getInt("qtd");
                }
                lblCard23.setText(String.valueOf(qtd));
            }

            // ---- Card 3: Exemplares Disponíveis ----
            try (PreparedStatement ps = conn.prepareStatement(SQL_EXEMPLARES_DISPONIVEIS); ResultSet rs = ps.executeQuery()) {
                int qtd = 0;
                if (rs.next()) {
                    qtd = rs.getInt("qtd");
                }
                lblCard33.setText(String.valueOf(qtd));
            }

            // ---- Card 4: Empréstimos Ativos (Em andamento + Em atraso) ----
            try (PreparedStatement ps = conn.prepareStatement(SQL_EMPRESTIMOS_ATIVOS); ResultSet rs = ps.executeQuery()) {
                int qtd = 0;
                if (rs.next()) {
                    qtd = rs.getInt("qtd");
                }
                lblCard43.setText(String.valueOf(qtd));
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao carregar cards do dashboard", e);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro ao carregar cards do dashboard: " + e.getMessage());
            // fallback visual
            lblCard13.setText("0");
            lblCard23.setText("0");
            lblCard33.setText("0");
            lblCard43.setText("0");
        }
    }

  
       

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panelMain = new javax.swing.JPanel();
        panelCards = new javax.swing.JPanel();
        panelCard1 = new javax.swing.JPanel();
        lblCard11 = new javax.swing.JLabel();
        lblCard12 = new javax.swing.JLabel();
        lblCard13 = new javax.swing.JLabel();
        panelCard2 = new javax.swing.JPanel();
        lblCard21 = new javax.swing.JLabel();
        lblCard22 = new javax.swing.JLabel();
        lblCard23 = new javax.swing.JLabel();
        panelCard3 = new javax.swing.JPanel();
        lblCard31 = new javax.swing.JLabel();
        lblCard32 = new javax.swing.JLabel();
        lblCard33 = new javax.swing.JLabel();
        panelCard4 = new javax.swing.JPanel();
        lblCard41 = new javax.swing.JLabel();
        lblCard42 = new javax.swing.JLabel();
        lblCard43 = new javax.swing.JLabel();
        panelGrafico = new javax.swing.JPanel();
        jblAndamento = new javax.swing.JLabel();
        jblVerde = new javax.swing.JLabel();
        jblLaraja = new javax.swing.JLabel();
        jblAtraso = new javax.swing.JLabel();
        jblAzul = new javax.swing.JLabel();
        jblFinalizado = new javax.swing.JLabel();
        panelGraficoPizza = new javax.swing.JPanel();
        panelAlertas = new javax.swing.JPanel();
        panelAlerta1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jblNaoDevolvidos = new javax.swing.JLabel();
        jblAlertaCor1 = new javax.swing.JLabel();
        jblAlertaTexto1 = new javax.swing.JLabel();
        panelAlerta2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jblUsoAcervo = new javax.swing.JLabel();
        jblAlertaCor2 = new javax.swing.JLabel();
        jblAlertaTexto2 = new javax.swing.JLabel();
        panelAlerta3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jblAguardandoEmprestimo = new javax.swing.JLabel();
        jblAlertaCor3 = new javax.swing.JLabel();
        jblAlertaTexto3 = new javax.swing.JLabel();
        btnVoltar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Sistema Biblioteca");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("Dashboard");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(459, Short.MAX_VALUE)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2))
                    .addComponent(jLabel1))
                .addGap(416, 416, 416))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel2))
        );

        getContentPane().add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelCards.setLayout(new java.awt.GridLayout(1, 0));

        panelCard1.setBackground(new java.awt.Color(153, 153, 255));
        panelCard1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelCard1.setPreferredSize(new java.awt.Dimension(850, 102));

        lblCard11.setText("ICON");

        lblCard12.setText("Total de Títulos Catalogados");

        lblCard13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblCard13.setText("0");

        javax.swing.GroupLayout panelCard1Layout = new javax.swing.GroupLayout(panelCard1);
        panelCard1.setLayout(panelCard1Layout);
        panelCard1Layout.setHorizontalGroup(
            panelCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard1Layout.createSequentialGroup()
                .addGroup(panelCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCard1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panelCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCard12)
                            .addComponent(lblCard11)))
                    .addGroup(panelCard1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(lblCard13)))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        panelCard1Layout.setVerticalGroup(
            panelCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCard11)
                .addGap(12, 12, 12)
                .addComponent(lblCard12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCard13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCards.add(panelCard1);

        panelCard2.setBackground(new java.awt.Color(153, 153, 255));
        panelCard2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelCard2.setPreferredSize(new java.awt.Dimension(850, 102));

        lblCard21.setText("ICON");

        lblCard22.setText("Exemplares no Acervo");

        lblCard23.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblCard23.setText("0");

        javax.swing.GroupLayout panelCard2Layout = new javax.swing.GroupLayout(panelCard2);
        panelCard2.setLayout(panelCard2Layout);
        panelCard2Layout.setHorizontalGroup(
            panelCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard2Layout.createSequentialGroup()
                .addGroup(panelCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCard2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panelCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCard22)
                            .addComponent(lblCard21)))
                    .addGroup(panelCard2Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(lblCard23)))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        panelCard2Layout.setVerticalGroup(
            panelCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCard21)
                .addGap(18, 18, 18)
                .addComponent(lblCard22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCard23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCards.add(panelCard2);

        panelCard3.setBackground(new java.awt.Color(153, 153, 255));
        panelCard3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelCard3.setPreferredSize(new java.awt.Dimension(850, 102));

        lblCard31.setText("ICON");

        lblCard32.setText("Exemplares Disponíveis");

        lblCard33.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblCard33.setText("0");

        javax.swing.GroupLayout panelCard3Layout = new javax.swing.GroupLayout(panelCard3);
        panelCard3.setLayout(panelCard3Layout);
        panelCard3Layout.setHorizontalGroup(
            panelCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard3Layout.createSequentialGroup()
                .addGroup(panelCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCard3Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(lblCard33))
                    .addGroup(panelCard3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panelCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCard32)
                            .addComponent(lblCard31))))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        panelCard3Layout.setVerticalGroup(
            panelCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCard31)
                .addGap(18, 18, 18)
                .addComponent(lblCard32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCard33)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCards.add(panelCard3);

        panelCard4.setBackground(new java.awt.Color(153, 153, 255));
        panelCard4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelCard4.setPreferredSize(new java.awt.Dimension(850, 102));

        lblCard41.setText("ICON");

        lblCard42.setText("Exemplares Emprestados");

        lblCard43.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblCard43.setText("0");

        javax.swing.GroupLayout panelCard4Layout = new javax.swing.GroupLayout(panelCard4);
        panelCard4.setLayout(panelCard4Layout);
        panelCard4Layout.setHorizontalGroup(
            panelCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard4Layout.createSequentialGroup()
                .addGroup(panelCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCard4Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panelCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCard42)
                            .addComponent(lblCard41)))
                    .addGroup(panelCard4Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(lblCard43)))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        panelCard4Layout.setVerticalGroup(
            panelCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCard4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCard41)
                .addGap(18, 18, 18)
                .addComponent(lblCard42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCard43)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCards.add(panelCard4);

        panelGrafico.setBackground(new java.awt.Color(255, 255, 255));
        panelGrafico.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Situação dos Empréstimos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 0, 255))); // NOI18N

        jblAndamento.setText("Em Andamento");

        jblVerde.setBackground(new java.awt.Color(0, 153, 0));
        jblVerde.setOpaque(true);

        jblLaraja.setBackground(new java.awt.Color(255, 102, 0));
        jblLaraja.setOpaque(true);

        jblAtraso.setText("Em Atraso");

        jblAzul.setBackground(new java.awt.Color(0, 0, 255));
        jblAzul.setOpaque(true);

        jblFinalizado.setText("Finalizados");

        panelGraficoPizza.setBackground(new java.awt.Color(255, 255, 255));
        panelGraficoPizza.setPreferredSize(new java.awt.Dimension(300, 200));

        javax.swing.GroupLayout panelGraficoPizzaLayout = new javax.swing.GroupLayout(panelGraficoPizza);
        panelGraficoPizza.setLayout(panelGraficoPizzaLayout);
        panelGraficoPizzaLayout.setHorizontalGroup(
            panelGraficoPizzaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        panelGraficoPizzaLayout.setVerticalGroup(
            panelGraficoPizzaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 206, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelGraficoLayout = new javax.swing.GroupLayout(panelGrafico);
        panelGrafico.setLayout(panelGraficoLayout);
        panelGraficoLayout.setHorizontalGroup(
            panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGraficoLayout.createSequentialGroup()
                .addGroup(panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGraficoLayout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jblVerde, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jblAndamento)
                        .addGap(85, 85, 85)
                        .addComponent(jblLaraja, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jblAtraso)
                        .addGap(54, 54, 54)
                        .addComponent(jblAzul, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jblFinalizado))
                    .addGroup(panelGraficoLayout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(panelGraficoPizza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        panelGraficoLayout.setVerticalGroup(
            panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGraficoLayout.createSequentialGroup()
                .addComponent(panelGraficoPizza, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jblFinalizado)
                    .addComponent(jblAzul, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jblLaraja, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jblVerde, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jblAndamento)
                        .addComponent(jblAtraso)))
                .addContainerGap())
        );

        panelAlertas.setBackground(new java.awt.Color(204, 255, 204));
        panelAlertas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Alertas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 0, 255))); // NOI18N

        panelAlerta1.setBackground(new java.awt.Color(51, 153, 255));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Itens não Devolvidos");

        jblNaoDevolvidos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jblNaoDevolvidos.setText("0");

        jblAlertaCor1.setBackground(new java.awt.Color(255, 0, 0));
        jblAlertaCor1.setOpaque(true);

        jblAlertaTexto1.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jblAlertaTexto1.setText("Crítico");

        javax.swing.GroupLayout panelAlerta1Layout = new javax.swing.GroupLayout(panelAlerta1);
        panelAlerta1.setLayout(panelAlerta1Layout);
        panelAlerta1Layout.setHorizontalGroup(
            panelAlerta1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlerta1Layout.createSequentialGroup()
                .addGroup(panelAlerta1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAlerta1Layout.createSequentialGroup()
                        .addContainerGap(18, Short.MAX_VALUE)
                        .addComponent(jLabel15))
                    .addGroup(panelAlerta1Layout.createSequentialGroup()
                        .addGroup(panelAlerta1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelAlerta1Layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(jblNaoDevolvidos, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelAlerta1Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jblAlertaCor1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jblAlertaTexto1)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelAlerta1Layout.setVerticalGroup(
            panelAlerta1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlerta1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jblNaoDevolvidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelAlerta1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jblAlertaTexto1)
                    .addComponent(jblAlertaCor1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        panelAlerta2.setBackground(new java.awt.Color(51, 153, 255));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setText("Uso do Acervo");

        jblUsoAcervo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jblUsoAcervo.setText("0");

        jblAlertaCor2.setBackground(new java.awt.Color(255, 255, 0));
        jblAlertaCor2.setOpaque(true);

        jblAlertaTexto2.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jblAlertaTexto2.setText("Atenção");

        javax.swing.GroupLayout panelAlerta2Layout = new javax.swing.GroupLayout(panelAlerta2);
        panelAlerta2.setLayout(panelAlerta2Layout);
        panelAlerta2Layout.setHorizontalGroup(
            panelAlerta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlerta2Layout.createSequentialGroup()
                .addGroup(panelAlerta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAlerta2Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jblUsoAcervo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAlerta2Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(panelAlerta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(panelAlerta2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jblAlertaCor2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jblAlertaTexto2)))))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        panelAlerta2Layout.setVerticalGroup(
            panelAlerta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlerta2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jblUsoAcervo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelAlerta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jblAlertaCor2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jblAlertaTexto2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelAlerta3.setBackground(new java.awt.Color(51, 153, 255));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setText("Aguardando Empréstimo");

        jblAguardandoEmprestimo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jblAguardandoEmprestimo.setText("0");

        jblAlertaCor3.setBackground(new java.awt.Color(255, 0, 0));
        jblAlertaCor3.setOpaque(true);

        jblAlertaTexto3.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jblAlertaTexto3.setText("Crítico");

        javax.swing.GroupLayout panelAlerta3Layout = new javax.swing.GroupLayout(panelAlerta3);
        panelAlerta3.setLayout(panelAlerta3Layout);
        panelAlerta3Layout.setHorizontalGroup(
            panelAlerta3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlerta3Layout.createSequentialGroup()
                .addGroup(panelAlerta3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAlerta3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19))
                    .addGroup(panelAlerta3Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jblAlertaCor3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelAlerta3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jblAguardandoEmprestimo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jblAlertaTexto3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelAlerta3Layout.setVerticalGroup(
            panelAlerta3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlerta3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(12, 12, 12)
                .addComponent(jblAguardandoEmprestimo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAlerta3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jblAlertaTexto3)
                    .addComponent(jblAlertaCor3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelAlertasLayout = new javax.swing.GroupLayout(panelAlertas);
        panelAlertas.setLayout(panelAlertasLayout);
        panelAlertasLayout.setHorizontalGroup(
            panelAlertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlertasLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(panelAlerta1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(panelAlerta2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelAlerta3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        panelAlertasLayout.setVerticalGroup(
            panelAlertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlertasLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(panelAlertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelAlerta3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelAlerta1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelAlerta2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(this::btnVoltarActionPerformed);

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(panelCards, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                .addContainerGap(235, Short.MAX_VALUE)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addComponent(panelGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(186, 186, 186))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addComponent(panelAlertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addComponent(btnVoltar)
                        .addGap(408, 408, 408))))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(panelCards, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelAlertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVoltar)
                .addGap(112, 112, 112))
        );

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        this.dispose();
        RelatorioForm telaRelatorio = new RelatorioForm();
        telaRelatorio.setVisible(true);
    }//GEN-LAST:event_btnVoltarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new DashboardView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jblAguardandoEmprestimo;
    private javax.swing.JLabel jblAlertaCor1;
    private javax.swing.JLabel jblAlertaCor2;
    private javax.swing.JLabel jblAlertaCor3;
    private javax.swing.JLabel jblAlertaTexto1;
    private javax.swing.JLabel jblAlertaTexto2;
    private javax.swing.JLabel jblAlertaTexto3;
    private javax.swing.JLabel jblAndamento;
    private javax.swing.JLabel jblAtraso;
    private javax.swing.JLabel jblAzul;
    private javax.swing.JLabel jblFinalizado;
    private javax.swing.JLabel jblLaraja;
    private javax.swing.JLabel jblNaoDevolvidos;
    private javax.swing.JLabel jblUsoAcervo;
    private javax.swing.JLabel jblVerde;
    private javax.swing.JLabel lblCard11;
    private javax.swing.JLabel lblCard12;
    private javax.swing.JLabel lblCard13;
    private javax.swing.JLabel lblCard21;
    private javax.swing.JLabel lblCard22;
    private javax.swing.JLabel lblCard23;
    private javax.swing.JLabel lblCard31;
    private javax.swing.JLabel lblCard32;
    private javax.swing.JLabel lblCard33;
    private javax.swing.JLabel lblCard41;
    private javax.swing.JLabel lblCard42;
    private javax.swing.JLabel lblCard43;
    private javax.swing.JPanel panelAlerta1;
    private javax.swing.JPanel panelAlerta2;
    private javax.swing.JPanel panelAlerta3;
    private javax.swing.JPanel panelAlertas;
    private javax.swing.JPanel panelCard1;
    private javax.swing.JPanel panelCard2;
    private javax.swing.JPanel panelCard3;
    private javax.swing.JPanel panelCard4;
    private javax.swing.JPanel panelCards;
    private javax.swing.JPanel panelGrafico;
    private javax.swing.JPanel panelGraficoPizza;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelMain;
    // End of variables declaration//GEN-END:variables
}
