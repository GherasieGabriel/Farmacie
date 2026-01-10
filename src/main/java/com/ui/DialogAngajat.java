// java
package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;

public class DialogAngajat extends JDialog {
    private Integer id;

    public DialogAngajat(JFrame parent) {
        this(parent, null);
    }

    public DialogAngajat(JFrame parent, Integer id) {
        super(parent, id == null ? "Adauga Angajat" : "Modifica Angajat", true);
        this.id = id;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNume = new JTextField();
        JTextField txtPrenume = new JTextField();
        JTextField txtPost = new JTextField();
        JTextField txtSalariu = new JTextField();
        JTextField txtFarmacie = new JTextField("1");

        if (id != null) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT Nume, Prenume, Post, Salariu, ID_Farmacie FROM Angajat WHERE ID_Angajat = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtNume.setText(rs.getString(1));
                    txtPrenume.setText(rs.getString(2));
                    txtPost.setText(rs.getString(3));
                    txtSalariu.setText(String.valueOf(rs.getBigDecimal(4)));
                    txtFarmacie.setText(String.valueOf(rs.getInt(5)));
                }
            } catch (SQLException ignored) {}
        }

        p.add(new JLabel("Nume:")); p.add(txtNume);
        p.add(new JLabel("Prenume:")); p.add(txtPrenume);
        p.add(new JLabel("Post:")); p.add(txtPost);
        p.add(new JLabel("Salariu:")); p.add(txtSalariu);
        p.add(new JLabel("ID Farmacie:")); p.add(txtFarmacie);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.add(createButton("Salveaza", e -> salveaza(txtNume, txtPrenume, txtPost, txtSalariu, txtFarmacie)));
        buttons.add(createButton("Anuleaza", e -> dispose()));

        add(p, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void salveaza(JTextField txtNume, JTextField txtPrenume, JTextField txtPost,
                          JTextField txtSalariu, JTextField txtFarmacie) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (id == null) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Angajat (Nume, Prenume, Post, Salariu, ID_Farmacie) VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, txtNume.getText());
                ps.setString(2, txtPrenume.getText());
                ps.setString(3, txtPost.getText());
                ps.setBigDecimal(4, new BigDecimal(txtSalariu.getText()));
                ps.setInt(5, Integer.parseInt(txtFarmacie.getText()));
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Angajat SET Nume = ?, Prenume = ?, Post = ?, Salariu = ?, ID_Farmacie = ? WHERE ID_Angajat = ?");
                ps.setString(1, txtNume.getText());
                ps.setString(2, txtPrenume.getText());
                ps.setString(3, txtPost.getText());
                ps.setBigDecimal(4, new BigDecimal(txtSalariu.getText()));
                ps.setInt(5, Integer.parseInt(txtFarmacie.getText()));
                ps.setInt(6, id);
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
