// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelBonFiscal extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> combClient;

    public PanelBonFiscal() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltru = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltru.add(new JLabel("Client:"));
        combClient = new JComboBox<>();
        combClient.addItem("Toti");
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT Nume_Prenume FROM Client ORDER BY Nume_Prenume");
            while (rs.next()) combClient.addItem(rs.getString(1));
        } catch (SQLException ignored) {}
        combClient.addActionListener(e -> reincarca());
        panelFiltru.add(combClient);
        add(panelFiltru, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Data", "Client", "Angajat", "Total"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelButoane = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelButoane.add(createButton("Adauga", e -> { new DialogBonFiscal(null).setVisible(true); reincarca(); }));
        panelButoane.add(createButton("Modifica", e -> modifica()));
        panelButoane.add(createButton("Reincarca", e -> reincarca()));
        add(panelButoane, BorderLayout.SOUTH);

        reincarca();
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void reincarca() {
        model.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getConnection();
            String baseSql = "SELECT bf.ID_Bon, bf.Data, c.Nume_Prenume, a.Nume || ' ' || a.Prenume, bf.Total " +
                    "FROM Bon_Fiscal bf INNER JOIN Client c ON bf.ID_Client = c.ID_Client " +
                    "INNER JOIN Angajat a ON bf.ID_Angajat = a.ID_Angajat";
            String selected = (String) combClient.getSelectedItem();

            PreparedStatement ps;
            if (!"Toti".equals(selected)) {
                baseSql += " WHERE c.Nume_Prenume = ? ORDER BY bf.Data DESC";
                ps = conn.prepareStatement(baseSql);
                ps.setString(1, selected);
            } else {
                baseSql += " ORDER BY bf.Data DESC";
                ps = conn.prepareStatement(baseSql);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getDate(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDouble(5)
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void modifica() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un rand!");
            return;
        }
        new DialogBonFiscal(null, (Integer) model.getValueAt(row, 0)).setVisible(true);
        reincarca();
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
    }
}
