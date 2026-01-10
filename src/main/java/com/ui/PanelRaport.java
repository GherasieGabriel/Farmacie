// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelRaport extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> combRapoarte;

    public PanelRaport() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTop.add(new JLabel("Raport:"));
        combRapoarte = new JComboBox<>(new String[]{
                "Vanzari per Medicament",
                "Medicamente Stoc Scazut",
                "Vanzari per Angajat",
                "Vanzari per Categorie"
        });
        panelTop.add(combRapoarte);

        JButton btn = new JButton("Genereaza");
        btn.addActionListener(e -> genereaza());
        panelTop.add(btn);
        add(panelTop, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void genereaza() {
        String raport = (String) combRapoarte.getSelectedItem();
        model.setRowCount(0);
        model.setColumnCount(0);

        try {
            Connection conn = DatabaseConnection.getConnection();

            if (raport.equals("Vanzari per Medicament")) {
                model.setColumnIdentifiers(new String[]{"Medicament", "Cantitate", "Valoare"});
                String sql = "SELECT m.Denumire, SUM(bm.Cantitate) as cant, SUM(bm.Cantitate * bm.Pret_Unitar) as val " +
                        "FROM Medicament m LEFT JOIN Bon_Medicament bm ON m.ID_Medicament = bm.ID_Medicament " +
                        "GROUP BY m.ID_Medicament, m.Denumire ORDER BY val DESC";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("Denumire"),
                            rs.getObject("cant") != null ? rs.getInt("cant") : 0,
                            rs.getObject("val") != null ? rs.getDouble("val") : 0
                    });
                }
            } else if (raport.equals("Medicamente Stoc Scazut")) {
                model.setColumnIdentifiers(new String[]{"Medicament", "Stoc", "Pret"});
                String sql = "SELECT Denumire, Stoc, Pret FROM Medicament WHERE Stoc < 50 ORDER BY Stoc ASC";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getDouble(3)});
                }
            } else if (raport.equals("Vanzari per Angajat")) {
                model.setColumnIdentifiers(new String[]{"Angajat", "Nr. Bonuri", "Valoare Total"});
                String sql = "SELECT a.Nume || ' ' || a.Prenume as angajat, COUNT(bf.ID_Bon) as nrbonuri, COALESCE(SUM(bf.Total), 0) as valoare " +
                        "FROM Angajat a LEFT JOIN Bon_Fiscal bf ON a.ID_Angajat = bf.ID_Angajat " +
                        "GROUP BY a.ID_Angajat, a.Nume, a.Prenume ORDER BY valoare DESC";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("angajat"), rs.getInt("nrbonuri"), rs.getDouble("valoare")});
                }
            } else if (raport.equals("Vanzari per Categorie")) {
                model.setColumnIdentifiers(new String[]{"Categorie", "Cantitate", "Valoare"});
                String sql = "SELECT m.Categorie, SUM(bm.Cantitate) as cant, SUM(bm.Cantitate * bm.Pret_Unitar) as val " +
                        "FROM Medicament m LEFT JOIN Bon_Medicament bm ON m.ID_Medicament = bm.ID_Medicament " +
                        "GROUP BY m.Categorie ORDER BY val DESC";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("Categorie"),
                            rs.getObject("cant") != null ? rs.getInt("cant") : 0,
                            rs.getObject("val") != null ? rs.getDouble("val") : 0
                    });
                }
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
    }
}
