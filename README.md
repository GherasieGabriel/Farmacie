# Gestiune Farmacie - Proiect Baze de Date

Sistem de gestionare a unei rețele de farmacii, implementat în Java Swing cu bază de date PostgreSQL.

## Caracteristici Principale

### Operații CRUD
- Medicament: Adăugare, modificare, ștergere, căutare
- Client: Gestionare date pacienți
- Angajat: Administrare personal farmacie
- Bonuri Fiscale: Gestionare vânzări

### Căutare Multi-Criterii
- Medicament după denumire, categorie, interval preț
- Bonuri după client

### Rapoarte
- Vânzări per medicament
- Medicamente stoc scăzut
- Vânzări per angajat
- Vânzări per categorie

## Baza de Date - Structură

### Relații
- 1:N: Farmacie → Angajat, Furnizor → Medicament
- M:N: Medicament ↔ Rețeta, Medicament ↔ Bon_Fiscal
- 12 chei externe

### Cerințe
- Java 11+
- PostgreSQL 12+
- JDBC Driver PostgreSQL

## Interogări SQL

### JOIN-uri (4+)
- INNER JOIN 5 tabele (Bon cu Client, Medicament, Furnizor)
- LEFT OUTER JOIN (Medicamente fără vânzări)
- FULL OUTER JOIN (Vândut vs Prescris)

### Agregări cu GROUP BY (4+)
- Vânzări per medicament (COUNT, SUM, AVG)
- Vânzări per categorie
- Bonuri cu > 2 medicamente
- Vânzări per angajat
- Rețete per client

### Subinterogări (5+)
- IN: Medicamente peste preț mediu
- EXISTS: Clienți cu rețete completate
- ANY: Medicamente peste minim categoria
- Corelată 3 tabele: Medicamente vândute și prescrise
- Necorelată 3 tabele: Furnizori cu vânzări

## Autor

Gherasie Gabriel Cătălin | Grupa CR 3.2A | 2025-2026
