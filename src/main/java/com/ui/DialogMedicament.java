package com.ui;

import com.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DialogMedicament extends JDialog {
    private Integer id;

    public DialogMedicament(JFrame parent) {
        this(parent, null);
    }

    public DialogMedicament(JFrame parent, Integer id) {
        super(parent, id == null ? "Adauga Medicament" : "Modifica Medicament", true);
        this.id = id;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNume = new JTextField();
        JTextField txtProducator = new JTextField();
        JTextField txtPret = new JTextField();
        JTextField txtStoc = new JTextField();
        JTextField txtCategorie = new JTextField();
        JComboBox<String> combFurnizor = new JComboBox<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT Denumire FROM Furnizor ORDER BY Denumire");
            while (rs.next()) combFurnizor.addItem(rs.getString(1));
        } catch (SQLException ignored) {}

        if (id != null) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT m.Denumire, m.Producator, m.Pret, m.Stoc, m.Categorie, f.Denumire FROM Medicament m JOIN Furnizor f ON m.ID_Furnizor = f.ID_Furnizor WHERE ID_Medicament = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtNume.setText(rs.getString(1));
                    txtProducator.setText(rs.getString(2));
                    txtPret.setText(String.valueOf(rs.getDouble(3)));
                    txtStoc.setText(String.valueOf(rs.getInt(4)));
                    txtCategorie.setText(rs.getString(5));
                    combFurnizor.setSelectedItem(rs.getString(6));
                }
            } catch (SQLException ignored) {}
        }

        p.add(new JLabel("Denumire:")); p.add(txtNume);
        p.add(new JLabel("Producator:")); p.add(txtProducator);
        p.add(new JLabel("Pret:")); p.add(txtPret);
        p.add(new JLabel("Stoc:")); p.add(txtStoc);
        p.add(new JLabel("Categorie:")); p.add(txtCategorie);
        p.add(new JLabel("Furnizor:")); p.add(combFurnizor);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnOK = new JButton("Salveaza");
        btnOK.addActionListener(e -> salveaza(txtNume, txtProducator, txtPret, txtStoc, txtCategorie, combFurnizor));
        buttons.add(btnOK);

        JButton btnCancel = new JButton("Anuleaza");
        btnCancel.addActionListener(e -> dispose());
        buttons.add(btnCancel);

        add(p, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void salveaza(JTextField txtNume, JTextField txtProducator, JTextField txtPret,
                          JTextField txtStoc, JTextField txtCategorie, JComboBox<String> combFurnizor) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            int idFurnizor = 1;
            PreparedStatement psFurnizor = conn.prepareStatement("SELECT ID_Furnizor FROM Furnizor WHERE Denumire = ?");
            psFurnizor.setString(1, (String) combFurnizor.getSelectedItem());
            ResultSet rs = psFurnizor.executeQuery();
            if (rs.next()) idFurnizor = rs.getInt(1);

            if (id == null) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Medicament (Denumire, Producator, Pret, Stoc, ID_Furnizor, Categorie) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, txtNume.getText());
                ps.setString(2, txtProducator.getText());
                ps.setDouble(3, Double.parseDouble(txtPret.getText()));
                ps.setInt(4, Integer.parseInt(txtStoc.getText()));
                ps.setInt(5, idFurnizor);
                ps.setString(6, txtCategorie.getText());
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement("UPDATE Medicament SET Denumire = ?, Producator = ?, Pret = ?, Stoc = ?, ID_Furnizor = ?, Categorie = ? WHERE ID_Medicament = ?");
                ps.setString(1, txtNume.getText());
                ps.setString(2, txtProducator.getText());
                ps.setDouble(3, Double.parseDouble(txtPret.getText()));
                ps.setInt(4, Integer.parseInt(txtStoc.getText()));
                ps.setInt(5, idFurnizor);
                ps.setString(6, txtCategorie.getText());
                ps.setInt(7, id);
                ps.executeUpdate();
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
        }
    }
}
