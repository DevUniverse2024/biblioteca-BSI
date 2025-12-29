/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.edu.bsi.biblioteca;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;

import java.awt.Image;
import javax.swing.ImageIcon;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import java.awt.Dimension;
import javax.swing.JDialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

/**
 *
 * @author Carlos
 */

public class FazerReservaForm extends javax.swing.JFrame {

    private JDateChooser jcldDataReserva;
    private Date hoje = new Date();
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FazerReservaForm.class.getName());

    /**
     * Creates new form ConsultaAcervoForm
     */
    public FazerReservaForm() {
        initComponents();
        ocultarCamposDataReserva(); // 👈 ESCONDE AO ABRIR A TELA
        carregarAcervo();
        trataIcioneCalendario();
        initDateChooser();  // inicializa o JDateChooser
        limitaDataCalendar();
        disponibilidadeBotoes();
    }

    private void ocultarCamposDataReserva() {
        lblDataReserva.setVisible(false);
        btnCalendar.setVisible(false);
        lblDataReservada.setVisible(false);
        txtDataReservada.setVisible(false);
        btnReservar.setVisible(false);
         btnOutraData.setVisible(false);
    }

    private void mostrarCamposDataReserva() {
        lblDataReserva.setVisible(true);
        btnCalendar.setVisible(true);
        lblDataReservada.setVisible(false);
        txtDataReservada.setVisible(false);
    }

    private void mostrar2CamposDataReserva() {
        lblDataReserva.setVisible(false);
        btnCalendar.setVisible(false);
        lblDataReservada.setVisible(true);
        txtDataReservada.setVisible(true);
        btnReservar.setVisible(true);
        btnOutraData.setVisible(true);
    }

    private void disponibilidadeBotoes() {
        btnReservar.setEnabled(false);
    }

    private void carregarAcervo() {

    DefaultTableModel model = (DefaultTableModel) jtblTitulos.getModel();
    model.setRowCount(0); // limpa a tabela
    configurarTabela();

    String sql = "SELECT ac.id AS acervo_id, t.titulo, t.editora, " +
                 "GROUP_CONCAT(a.nome ORDER BY a.nome SEPARATOR ', ') AS autores, " +
                 "t.ano, ac.tombo, ac.status " +
                 "FROM acervo ac " +
                 "JOIN titulo t ON t.id = ac.titulo_id " +
                 "LEFT JOIN titulo_autor ta ON ta.titulo_id = t.id " +
                 "LEFT JOIN autor a ON a.id = ta.autor_id " +
                 "GROUP BY ac.id, t.titulo, t.editora, t.ano, ac.tombo, ac.status " +
                 "ORDER BY ac.status, t.titulo, ac.tombo";

    try (Connection conn = Conexao.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("acervo_id"), // id
                rs.getString("titulo"),
                rs.getString("editora"),
                rs.getString("autores"),
                rs.getInt("ano"),
                rs.getString("tombo"),
                rs.getString("status")
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao carregar acervo: " + e.getMessage());
    }

    // Configura seleção para bloquear linhas não disponíveis
    jtblTitulos.setRowSelectionAllowed(true);
    jtblTitulos.getSelectionModel().addListSelectionListener(e -> {
        int linha = jtblTitulos.getSelectedRow();
        if (linha >= 0) {
            String status = jtblTitulos.getValueAt(linha, 6).toString();
            if (!"DISPONIVEL".equalsIgnoreCase(status)) {
                // desmarcar seleção se não estiver disponível
                jtblTitulos.clearSelection();
            }
        }
    });
}

    private void configurarTabela() {

        TableColumnModel colModel = jtblTitulos.getColumnModel();

        // Ocultar coluna ID
        colModel.getColumn(0).setMinWidth(0);
        colModel.getColumn(0).setMaxWidth(0);

        // Largura da coluna Título
        colModel.getColumn(1).setPreferredWidth(250);

        // Centralizar coluna Ano
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        colModel.getColumn(4).setCellRenderer(center);

        // Centralizar coluna Status
        colModel.getColumn(5).setCellRenderer(center);
    }

    private void initDateChooser() {
        jcldDataReserva = new JDateChooser();
        jcldDataReserva.setDate(hoje); // data inicial = hoje

        // Limites
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoje);
        cal.add(Calendar.MONTH, 1);
        jcldDataReserva.setMinSelectableDate(hoje);
        jcldDataReserva.setMaxSelectableDate(cal.getTime());

        // Define tamanho
        jcldDataReserva.setPreferredSize(new Dimension(120, 25));

        // Adiciona ao painel no lugar do btnCalendar (ou ao lado)
        jPanel1.add(jcldDataReserva);
        jcldDataReserva.setVisible(true);
    }

    private void trataIcioneCalendario() {
        btnCalendar.setText(""); // remove texto
        ImageIcon iconOriginal = new ImageIcon(
                getClass().getResource("/br/edu/bsi/biblioteca/imagens/calendar.png")
        );
        // Redimensiona
        Image imgRedimensionada = iconOriginal.getImage().getScaledInstance(
                20, 20, // largura x altura
                Image.SCALE_SMOOTH // suaviza a imagem
        );

// Aplica no botão
        btnCalendar.setIcon(new ImageIcon(imgRedimensionada));
        btnCalendar.setText(""); // remove texto
        btnCalendar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCalendar.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
    }

    private void limitaDataCalendar() {
        // Calendário para calcular +1 mês
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoje);
        cal.add(Calendar.MONTH, 1);

// Data máxima (1 mês à frente)
        Date umMesDepois = cal.getTime();

// Aplicar limites ao JDateChooser
        //     jcldDataReserva.setMinSelectableDate(hoje);
        //     jcldDataReserva.setMaxSelectableDate(umMesDepois);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnVolta = new javax.swing.JButton();
        ScrollPaneReserva = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        txtTitulo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnReservar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        lblDataReserva = new javax.swing.JLabel();
        btnCalendar = new javax.swing.JButton();
        lblDataReservada = new javax.swing.JLabel();
        txtDataReservada = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtAutores = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTombo = new javax.swing.JTextField();
        btnOutraData = new javax.swing.JButton();
        txtIdAcervo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jpnlTitulos = new javax.swing.JPanel();
        jScrollPaneTitulos = new javax.swing.JScrollPane();
        jtblTitulos = new javax.swing.JTable();
        btnVoltar = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setText("Área do ALuno");

        jLabel1.setText("Biblioteca BSI");

        jLabel3.setText("Fazer Reserva");

        btnVolta.setText("Volta");
        btnVolta.addActionListener(this::btnVoltaActionPerformed);

        ScrollPaneReserva.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Fazer Reserva"));

        txtTitulo.setEditable(false);
        txtTitulo.setColumns(50);
        txtTitulo.setText(" ");

        jLabel4.setText("Titulo: ");

        btnReservar.setText("Reservar");
        btnReservar.addActionListener(this::btnReservarActionPerformed);

        btnLimpar.setText("Limpar");

        lblDataReserva.setText("Data da Reserva");

        btnCalendar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCalendar.addActionListener(this::btnCalendarActionPerformed);

        lblDataReservada.setText("Data Reservada:");

        txtDataReservada.setColumns(7);
        txtDataReservada.setText(" ");

        jLabel7.setText("Autores:");

        txtAutores.setEditable(false);
        txtAutores.setColumns(25);
        txtAutores.setText(" ");

        jLabel8.setText("Tombo: ");

        txtTombo.setEditable(false);
        txtTombo.setColumns(15);
        txtTombo.setText(" ");

        btnOutraData.setText("Escolher Outra Data");
        btnOutraData.addActionListener(this::btnOutraDataActionPerformed);

        txtIdAcervo.setColumns(10);
        txtIdAcervo.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnLimpar)
                        .addGap(70, 70, 70))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDataReservada)
                            .addComponent(lblDataReserva))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataReservada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(390, 390, 390))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnOutraData)
                        .addGap(252, 252, 252))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                        .addGap(70, 70, 70))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnReservar)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAutores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(txtIdAcervo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtIdAcervo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtAutores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtTombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDataReserva)
                    .addComponent(btnCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDataReservada)
                    .addComponent(txtDataReservada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(btnReservar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOutraData)
                .addGap(83, 83, 83)
                .addComponent(btnLimpar)
                .addGap(57, 57, 57))
        );

        ScrollPaneReserva.setViewportView(jPanel1);

        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Acervo da Biblioteca"));

        jtblTitulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Titulo", "Editora", "autores", "ano", "Tombo", "Status"
            }
        ));
        jtblTitulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTitulosmouseCliked(evt);
            }
        });
        jScrollPaneTitulos.setViewportView(jtblTitulos);

        javax.swing.GroupLayout jpnlTitulosLayout = new javax.swing.GroupLayout(jpnlTitulos);
        jpnlTitulos.setLayout(jpnlTitulosLayout);
        jpnlTitulosLayout.setHorizontalGroup(
            jpnlTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlTitulosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneTitulos, javax.swing.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpnlTitulosLayout.setVerticalGroup(
            jpnlTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlTitulosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jpnlTitulos);

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(this::btnVoltarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
                    .addComponent(ScrollPaneReserva))
                .addGap(14, 14, 14))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnVolta)
                .addGap(157, 157, 157))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(289, 289, 289)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(320, 320, 320)
                        .addComponent(btnVoltar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(ScrollPaneReserva, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVoltar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 250, Short.MAX_VALUE)
                .addComponent(btnVolta)
                .addGap(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVoltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltaActionPerformed
        // Fecha a janela atual
        this.dispose();
        AlunoForm telaAluno = new AlunoForm();
        telaAluno.setVisible(true);
    }//GEN-LAST:event_btnVoltaActionPerformed

    private void btnReservarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReservarActionPerformed

        int acervoId = Integer.parseInt(txtIdAcervo.getText());
        int usuarioId = SessaoUsuario.idUsuarioLogado;
        String dataReserva = txtDataReservada.getText(); // formato dd/MM/yyyy

        // Converte a data para java.sql.Timestamp
        java.sql.Timestamp timestampReserva;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            Date parsedDate = sdf.parse(dataReserva);
            timestampReserva = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Data inválida: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String sqlInsertReserva = "INSERT INTO reserva (usuario_id, acervo_id, data_reserva, status) VALUES (?, ?, ?, 'ATIVA')";
        String sqlUpdateAcervo = "UPDATE acervo SET status = 'RESERVADO' WHERE id = ?";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false); // Transação

            try (PreparedStatement psReserva = conn.prepareStatement(sqlInsertReserva); PreparedStatement psAcervo = conn.prepareStatement(sqlUpdateAcervo)) {

                // Inserir na tabela reserva
                psReserva.setInt(1, usuarioId);
                psReserva.setInt(2, acervoId);
                psReserva.setTimestamp(3, timestampReserva);
                psReserva.executeUpdate();

                // Atualizar status do acervo
                psAcervo.setInt(1, acervoId);
                psAcervo.executeUpdate();

                conn.commit(); // confirma a transação

                // Mensagem de sucesso
                JOptionPane.showMessageDialog(
                        this,
                        "Reserva efetuada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Fecha o formulário atual
                this.dispose();

                // Abre o próximo formulário
                ConsultaReservaForm consultaReservaForm = new ConsultaReservaForm();
                consultaReservaForm.setVisible(true);

            } catch (Exception e) {
                conn.rollback(); // desfaz se houver erro
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao efetuar reserva: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro de conexão: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }//GEN-LAST:event_btnReservarActionPerformed

    private void btnCalendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalendarActionPerformed

        // Cria o calendário
        JCalendar calendar = new JCalendar();

        // Define limites de datas
        Date hoje = new Date();
        calendar.setMinSelectableDate(hoje);

        Calendar cal = Calendar.getInstance();
        cal.setTime(hoje);
        cal.add(Calendar.MONTH, 1);
        calendar.setMaxSelectableDate(cal.getTime());

        // Cria o popup em JDialog
        JDialog dialog = new JDialog(this, "Escolha a data", true);
        dialog.getContentPane().add(calendar);
        dialog.pack();
        dialog.setLocationRelativeTo(this); // centraliza na janela

        // Listener para fechar o dialog quando o usuário escolher uma data
        calendar.addPropertyChangeListener("calendar", new PropertyChangeListener() {

            private Date dataAnterior = null;

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                Date dataAtual = calendar.getDate();

                // Primeira vez apenas guarda a data
                if (dataAnterior == null) {
                    dataAnterior = dataAtual;
                    return;
                }

                // Se apenas o mês/ano mudou, NÃO fecha
                Calendar c1 = Calendar.getInstance();
                c1.setTime(dataAnterior);

                Calendar c2 = Calendar.getInstance();
                c2.setTime(dataAtual);

                boolean mesmoDia
                        = c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);

                if (mesmoDia) {
                    // Só mudou o mês ou ano → ignora
                    dataAnterior = dataAtual;
                    return;
                }

                // 🔹 Aqui SIM o usuário clicou em um dia
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                txtDataReservada.setText(sdf.format(dataAtual));
                mostrar2CamposDataReserva();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);


    }//GEN-LAST:event_btnCalendarActionPerformed

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        // Fecha a janela atual
        this.dispose();
        AlunoForm telaAluno = new AlunoForm();
        telaAluno.setVisible(true);
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void tbTitulosmouseCliked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbTitulosmouseCliked
        // Se quiser exigir duplo clique, descomente:
        // if (evt.getClickCount() != 2) return;
        int linha = jtblTitulos.getSelectedRow();

        if (linha == -1) {
            return;
        }

        // Recupera dados da JTable
        txtIdAcervo.setText(jtblTitulos.getValueAt(linha, 0).toString());
        txtTitulo.setText(jtblTitulos.getValueAt(linha, 1).toString());
        txtAutores.setText(jtblTitulos.getValueAt(linha, 3).toString());
        txtTombo.setText(jtblTitulos.getValueAt(linha, 5).toString());

        // Habilita botões
        btnReservar.setEnabled(true);
        mostrarCamposDataReserva();
    }//GEN-LAST:event_tbTitulosmouseCliked

    private void btnOutraDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOutraDataActionPerformed
        lblDataReserva.setVisible(true);
        btnCalendar.setVisible(true);
        lblDataReservada.setVisible(false);
        txtDataReservada.setVisible(false);
        btnReservar.setVisible(false);
        btnOutraData.setVisible(false);
    }//GEN-LAST:event_btnOutraDataActionPerformed

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
    java.awt.EventQueue.invokeLater(() -> new FazerReservaForm().setVisible(true));
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrollPaneReserva;
    private javax.swing.JButton btnCalendar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnOutraData;
    private javax.swing.JButton btnReservar;
    private javax.swing.JButton btnVolta;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneTitulos;
    private javax.swing.JPanel jpnlTitulos;
    private javax.swing.JTable jtblTitulos;
    private javax.swing.JLabel lblDataReserva;
    private javax.swing.JLabel lblDataReservada;
    private javax.swing.JTextField txtAutores;
    private javax.swing.JTextField txtDataReservada;
    private javax.swing.JTextField txtIdAcervo;
    private javax.swing.JTextField txtTitulo;
    private javax.swing.JTextField txtTombo;
    // End of variables declaration//GEN-END:variables
}
