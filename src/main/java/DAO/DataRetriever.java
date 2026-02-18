package DAO;
import Services.InvoiceStatusTotals;
import Services.InvoiceTaxSummary;
import Services.InvoiceTotale;
import Utils.DBconnection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.*;
import java.util.List;
public class DataRetriever {
    public List<InvoiceTotale> findInvoiceTotals() {
        List<InvoiceTotale> totals = new ArrayList<>();

        String sql = """
            SELECT i.id, i.customer_name, i.status,
                   SUM(il.quantity * il.unit_price) as total_amount
            FROM invoice i
            LEFT JOIN invoice_line il ON i.id = il.invoice_id
            GROUP BY i.id, i.customer_name, i.status
            ORDER BY i.id;
            """;

        try (Connection conn = new DBconnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                totals.add(new InvoiceTotale(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("status"),
                        rs.getDouble("total_amount")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul des totaux", e);
        }
        return totals;
    }
    public List<InvoiceTotale> findConfirmedAndPaidInvoiceTotals() {
        List<InvoiceTotale> totals = new ArrayList<>();
        // SQL avec filtre sur les types ENUM
        String sql = """
        SELECT i.id, i.customer_name, i.status, 
               SUM(il.quantity * il.unit_price) as total_amount
        FROM invoice i
        LEFT JOIN invoice_line il ON i.id = il.invoice_id
        WHERE i.status IN ('CONFIRMED', 'PAID')
        GROUP BY i.id, i.customer_name, i.status
        ORDER BY i.id;
    """;

        try (Connection conn = new DBconnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                totals.add(new InvoiceTotale(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("status"),
                        rs.getDouble("total_amount")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return totals;
    }
    public InvoiceStatusTotals computeStatusTotals() {
        String sql = """
        SELECT 
            SUM(CASE WHEN i.status = 'PAID' THEN il.quantity * il.unit_price ELSE 0 END) as paid,
            SUM(CASE WHEN i.status = 'CONFIRMED' THEN il.quantity * il.unit_price ELSE 0 END) as confirmed,
            SUM(CASE WHEN i.status = 'DRAFT' THEN il.quantity * il.unit_price ELSE 0 END) as draft
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id;
    """;

        try (Connection conn = new DBconnection().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new InvoiceStatusTotals(
                        rs.getDouble("paid"),
                        rs.getDouble("confirmed"),
                        rs.getDouble("draft")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new InvoiceStatusTotals(0, 0, 0);
    }
    public Double computeWeightedTurnover() {
        String sql = """
        SELECT SUM(
            (il.quantity * il.unit_price) * CASE 
                WHEN i.status = 'PAID' THEN 1.0
                WHEN i.status = 'CONFIRMED' THEN 0.5
                ELSE 0.0
            END
        ) as weighted_total
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id;
    """;

        try (Connection conn = new DBconnection().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("weighted_total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }
    public void displayInvoiceTaxes() {
        String sql = """
        SELECT 
            i.id,
            SUM(il.quantity * il.unit_price) as total_ht,
            SUM(il.quantity * il.unit_price) * (t.rate / 100) as tva,
            SUM(il.quantity * il.unit_price) * (1 + t.rate / 100) as total_ttc
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        CROSS JOIN tax_config t 
        WHERE t.label = 'TVA STANDARD'
        GROUP BY i.id, t.rate
        ORDER BY i.id;
    """;

        try (Connection conn = new DBconnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("ID , Total HT , TVA , Total TTC");
            while (rs.next()) {
                int id = rs.getInt("id");
                double ht = rs.getDouble("total_ht");
                double tva = rs.getDouble("tva");
                double ttc = rs.getDouble("total_ttc");

                System.out.printf("%d | %.2f Ar | %.2f Ar | %.2f Ar%n", id, ht, tva, ttc);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de la TVA : " + e.getMessage(), e);
        }
    }
    public List<InvoiceTaxSummary> findInvoiceTaxSummaries() {
        List<InvoiceTaxSummary> summaries = new ArrayList<>();
        String sql = """
        SELECT 
            i.id,
            SUM(il.quantity * il.unit_price) as total_ht,
            SUM(il.quantity * il.unit_price) * (t.rate / 100) as tva,
            SUM(il.quantity * il.unit_price) * (1 + t.rate / 100) as total_ttc
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        CROSS JOIN tax_config t 
        WHERE t.label = 'TVA STANDARD'
        GROUP BY i.id, t.rate
        ORDER BY i.id;
    """;

        try (Connection conn = new DBconnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                summaries.add(new InvoiceTaxSummary(
                        rs.getInt("id"),
                        rs.getDouble("total_ht"),
                        rs.getDouble("tva"),
                        rs.getDouble("total_ttc")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return summaries;
    }
    public BigDecimal computeWeightedTurnoverTtc() {
        String sql = """
            SELECT SUM(
                (il.quantity * il.unit_price * (1 + t.rate / 100)) * CASE 
                    WHEN i.status = 'PAID' THEN 1.0
                    WHEN i.status = 'CONFIRMED' THEN 0.5
                    ELSE 0.0
                END
            ) as weighted_ttc
            FROM invoice i
            JOIN invoice_line il ON i.id = il.invoice_id
            CROSS JOIN tax_config t
            WHERE t.label = 'TVA STANDARD';
        """;

        try (Connection conn = new DBconnection().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("weighted_ttc");
                return (result != null) ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur calcul CA TTC pondéré", e);
        }
        return BigDecimal.ZERO;
    }
}
