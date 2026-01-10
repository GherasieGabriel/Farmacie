// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanelMedicament extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    // Essential filters
    private JTextField txtNume;
    private JTextField txtCategorie;
    private JTextField txtPretMin;
    private JTextField txtPretMax;

    public PanelMedicament() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top filter panel (only essentials)
        JPanel filters = new JPanel(new GridLayout(2, 5, 8, 8));
        filters.add(new JLabel("Denumire:"));
        txtNume = new JTextField();
        filters.add(txtNume);

        filters.add(new JLabel("Categorie:"));
        txtCategorie = new JTextField();
        filters.add(txtCategorie);

        JButton btnCauta = createButton("Cauta", e -> cautaMulti());
        filters.add(btnCauta);

        filters.add(new JLabel("Pret Min:"));
        txtPretMin = new JTextField();
        filters.add(txtPretMin);

        filters.add(new JLabel("Pret Max:"));
        txtPretMax = new JTextField();
        filters.add(txtPretMax);

        JButton btnReset = createButton("Reseteaza", e -> { clearFilters(); reincarca(); });
        filters.add(btnReset);

        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Denumire", "Producator", "Pret", "Stoc", "Categorie", "Furnizor"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelButoane = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelButoane.add(createButton("Adauga", e -> { new DialogMedicament(null).setVisible(true); reincarca(); }));
        panelButoane.add(createButton("Modifica", e -> modificaRand()));
        panelButoane.add(createButton("Sterge", e -> stergeRand()));
        panelButoane.add(createButton("Reincarca", e -> reincarca()));
        add(panelButoane, BorderLayout.SOUTH);

        reincarca();
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void clearFilters() {
        txtNume.setText("");
        txtCategorie.setText("");
        txtPretMin.setText("");
        txtPretMax.setText("");
    }

    private void reincarca() {
        model.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT m.ID_Medicament, m.Denumire, m.Producator, m.Pret, m.Stoc, m.Categorie, f.Denumire " +
                    "FROM Medicament m INNER JOIN Furnizor f ON m.ID_Furnizor = f.ID_Furnizor ORDER BY m.Denumire";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4),
                        rs.getInt(5), rs.getString(6), rs.getString(7)
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    // Simplified multi-criteria search (name, category, price range)
    private void cautaMulti() {
        model.setRowCount(0);

        String nume = txtNume.getText().trim();
        String categorie = txtCategorie.getText().trim();
        String pretMin = txtPretMin.getText().trim();
        String pretMax = txtPretMax.getText().trim();

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT m.ID_Medicament, m.Denumire, m.Producator, m.Pret, m.Stoc, m.Categorie, f.Denumire " +
                        "FROM Medicament m INNER JOIN Furnizor f ON m.ID_Furnizor = f.ID_Furnizor WHERE 1=1 ");

        if (!nume.isEmpty()) {
            sql.append("AND LOWER(m.Denumire) LIKE LOWER(?) ");
            params.add("%" + nume + "%");
        }
        if (!categorie.isEmpty()) {
            sql.append("AND LOWER(m.Categorie) LIKE LOWER(?) ");
            params.add("%" + categorie + "%");
        }
        if (!pretMin.isEmpty()) {
            sql.append("AND m.Pret >= ? ");
            params.add(parseDoubleSafe(pretMin));
        }
        if (!pretMax.isEmpty()) {
            sql.append("AND m.Pret <= ? ");
            params.add(parseDoubleSafe(pretMax));
        }

        sql.append("ORDER BY m.Denumire");

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else if (p instanceof Double) {
                    ps.setDouble(i + 1, (Double) p);
                } else {
                    ps.setObject(i + 1, p);
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4),
                        rs.getInt(5), rs.getString(6), rs.getString(7)
                });
            }
        } catch (SQLException e) {
            showError(e);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Eroare: valori numerice invalide.");
        }
    }

    private double parseDoubleSafe(String s) {
        return Double.parseDouble(s.replace(',', '.'));
    }

    private void modificaRand() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un rand!");
            return;
        }
        new DialogMedicament(null, (Integer) model.getValueAt(row, 0)).setVisible(true);
        reincarca();
    }

    private void stergeRand() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un rand!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Confirmati stergerea?") == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Medicament WHERE ID_Medicament = ?");
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
