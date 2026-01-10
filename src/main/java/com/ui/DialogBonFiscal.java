// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class DialogBonFiscal extends JDialog {
    private Integer id;

    public DialogBonFiscal(JFrame parent) {
        this(parent, null);
    }

    public DialogBonFiscal(JFrame parent, Integer id) {
        super(parent, id == null ? "Creeaza Bon" : "Modifica Bon", true);
        this.id = id;
        setSize(500, 400);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtData = new JTextField(LocalDate.now().toString());
        JComboBox<String> combClient = new JComboBox<>();
        JComboBox<String> combAngajat = new JComboBox<>();
        JTextField txtTotal = new JTextField("0");

        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT Nume_Prenume FROM Client ORDER BY Nume_Prenume");
            while (rs.next()) combClient.addItem(rs.getString(1));
            rs = conn.createStatement().executeQuery("SELECT Nume || ' ' || Prenume FROM Angajat ORDER BY Nume, Prenume");
            while (rs.next()) combAngajat.addItem(rs.getString(1));
        } catch (SQLException ignored) {}

        if (id != null) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT bf.Data, c.Nume_Prenume, a.Nume || ' ' || a.Prenume, bf.Total " +
                                "FROM Bon_Fiscal bf JOIN Client c ON bf.ID_Client = c.ID_Client " +
                                "JOIN Angajat a ON bf.ID_Angajat = a.ID_Angajat WHERE ID_Bon = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtData.setText(rs.getDate(1).toString());
                    combClient.setSelectedItem(rs.getString(2));
                    combAngajat.setSelectedItem(rs.getString(3));
                    txtTotal.setText(String.valueOf(rs.getDouble(4)));
                }
            } catch (SQLException ignored) {}
        }

        p.add(new JLabel("Data:")); p.add(txtData);
        p.add(new JLabel("Client:")); p.add(combClient);
        p.add(new JLabel("Angajat:")); p.add(combAngajat);
        p.add(new JLabel("Total:")); p.add(txtTotal);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.add(createButton("Salveaza", e -> salveaza(txtData, combClient, combAngajat, txtTotal)));
        buttons.add(createButton("Anuleaza", e -> dispose()));

        add(p, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void salveaza(JTextField txtData, JComboBox<String> combClient,
                          JComboBox<String> combAngajat, JTextField txtTotal) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            int idClient = lookupId(conn.prepareStatement(
                    "SELECT ID_Client FROM Client WHERE Nume_Prenume = ?"), (String) combClient.getSelectedItem());

            int idAngajat = lookupId(conn.prepareStatement(
                    "SELECT ID_Angajat FROM Angajat WHERE (Nume || ' ' || Prenume) = ?"), (String) combAngajat.getSelectedItem());

            if (id == null) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Bon_Fiscal (Data, ID_Client, ID_Angajat, Total) VALUES (?, ?, ?, ?)");
                ps.setDate(1, java.sql.Date.valueOf(txtData.getText()));
                ps.setInt(2, idClient);
                ps.setInt(3, idAngajat);
                ps.setDouble(4, Double.parseDouble(txtTotal.getText()));
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Bon_Fiscal SET Data = ?, ID_Client = ?, ID_Angajat = ?, Total = ? WHERE ID_Bon = ?");
                ps.setDate(1, java.sql.Date.valueOf(txtData.getText()));
                ps.setInt(2, idClient);
                ps.setInt(3, idAngajat);
                ps.setDouble(4, Double.parseDouble(txtTotal.getText()));
                ps.setInt(5, id);
                ps.executeUpdate();
            }
            dispose();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private int lookupId(PreparedStatement ps, String value) throws SQLException {
        ps.setString(1, value);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 1;
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
    }
}
