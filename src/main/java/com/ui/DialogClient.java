// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DialogClient extends JDialog {
    private Integer id;

    public DialogClient(JFrame parent) {
        this(parent, null);
    }

    public DialogClient(JFrame parent, Integer id) {
        super(parent, id == null ? "Adauga Client" : "Modifica Client", true);
        this.id = id;
        setSize(400, 250);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNume = new JTextField();
        JTextField txtAdresa = new JTextField();
        JTextField txtTelefon = new JTextField();
        JTextField txtEmail = new JTextField();

        if (id != null) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT Nume_Prenume, Adresa, Telefon, Email FROM Client WHERE ID_Client = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtNume.setText(rs.getString(1));
                    txtAdresa.setText(rs.getString(2));
                    txtTelefon.setText(rs.getString(3));
                    txtEmail.setText(rs.getString(4));
                }
            } catch (SQLException ignored) {}
        }

        p.add(new JLabel("Nume:")); p.add(txtNume);
        p.add(new JLabel("Adresa:")); p.add(txtAdresa);
        p.add(new JLabel("Telefon:")); p.add(txtTelefon);
        p.add(new JLabel("Email:")); p.add(txtEmail);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.add(createButton("Salveaza", e -> salveaza(txtNume, txtAdresa, txtTelefon, txtEmail)));
        buttons.add(createButton("Anuleaza", e -> dispose()));

        add(p, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void salveaza(JTextField txtNume, JTextField txtAdresa, JTextField txtTelefon, JTextField txtEmail) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (id == null) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Client (Nume_Prenume, Adresa, Telefon, Email) VALUES (?, ?, ?, ?)");
                ps.setString(1, txtNume.getText());
                ps.setString(2, txtAdresa.getText());
                ps.setString(3, txtTelefon.getText());
                ps.setString(4, txtEmail.getText());
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Client SET Nume_Prenume = ?, Adresa = ?, Telefon = ?, Email = ? WHERE ID_Client = ?");
                ps.setString(1, txtNume.getText());
                ps.setString(2, txtAdresa.getText());
                ps.setString(3, txtTelefon.getText());
                ps.setString(4, txtEmail.getText());
                ps.setInt(5, id);
                ps.executeUpdate();
            }
            dispose();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
    }
}
