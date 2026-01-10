package com.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private PanelMedicament panelMedicament;
    private PanelClient panelClient;
    private PanelBonFiscal panelBon;
    private PanelRaport panelRaport;

    public MainWindow() {
        setTitle("Gestiune Farmacie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Inițializare taburi
        tabbedPane = new JTabbedPane();

        panelMedicament = new PanelMedicament();
        panelClient = new PanelClient();
        panelBon = new PanelBonFiscal();
        panelRaport = new PanelRaport();

        tabbedPane.addTab("Medicamente", panelMedicament);
        tabbedPane.addTab("Clienți", panelClient);
        tabbedPane.addTab("Bonuri Fiscale", panelBon);
        tabbedPane.addTab("Rapoarte", panelRaport);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
