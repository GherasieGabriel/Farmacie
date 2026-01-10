// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelClient extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public PanelClient() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Nume", "Adresa", "Telefon", "Email"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelButoane = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelButoane.add(createButton("Adauga", e -> { new DialogClient(null).setVisible(true); reincarca(); }));
        panelButoane.add(createButton("Modifica", e -> modifica()));
        panelButoane.add(createButton("Sterge", e -> sterge()));
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
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Client ORDER BY Nume_Prenume");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)
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
        new DialogClient(null, (Integer) model.getValueAt(row, 0)).setVisible(true);
        reincarca();
    }

    private void sterge() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un rand!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Confirmati stergerea?") == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Client WHERE ID_Client = ?");
                ps.setInt(1, (Integer) model.getValueAt(row, 0));
                ps.executeUpdate();
                reincarca();
            } catch (SQLException e) {
                showError(e);
            }
        }
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
    }
}
