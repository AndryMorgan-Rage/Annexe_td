import DAO.DataRetriever;
import Services.InvoiceStatusTotals;
import Services.InvoiceTaxSummary;
import Services.InvoiceTotale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnexeTest {
    private static DataRetriever retriever;

    @BeforeAll
    static void setup() {
        retriever = new DataRetriever();
    }

    @Test
    void testAllQuestions() {
        System.out.println("--- DÉBUT DES TESTS DU TD PUSH-DOWN ---\n");

        // Q1 - Total par facture
        List<InvoiceTotale> q1Results = retriever.findInvoiceTotals();
        System.out.println("Q1 - findInvoiceTotals() :");
        q1Results.forEach(System.out::println);
        assertEquals(3, q1Results.size());
        // Vérification Alice : (2*100) + (1*50) = 250
        assertEquals(250.0, q1Results.get(0).getTotalAmount());

        // Q2 - Factures CONFIRMED et PAID
        List<InvoiceTotale> q2Results = retriever.findConfirmedAndPaidInvoiceTotals();
        System.out.println("\nQ2 - findConfirmedAndPaidInvoiceTotals() :");
        q2Results.forEach(System.out::println);
        assertEquals(2, q2Results.size()); // Uniquement Alice et Bob

        // Q3 - Totaux cumulés par statut
        InvoiceStatusTotals q3Stats = retriever.computeStatusTotals();
        System.out.println("\nQ3 - computeStatusTotals() :");
        System.out.println("PAID : " + q3Stats.getTotalPaid());
        System.out.println("CONFIRMED : " + q3Stats.getTotalConfirmed());
        System.out.println("DRAFT : " + q3Stats.getTotalDraft());
        assertEquals(700.0, q3Stats.getTotalPaid());

        // Q4 - CA Pondéré HT
        Double q4Weighted = retriever.computeWeightedTurnover();
        System.out.println("\nQ4 - computeWeightedTurnover() : " + q4Weighted);
        assertEquals(825.0, q4Weighted);
        // Q5-A - Totaux HT, TVA et TTC
        List<InvoiceTaxSummary> q5aResults = retriever.findInvoiceTaxSummaries();
        System.out.println("\nQ5-A - findInvoiceTaxSummaries() :");
        q5aResults.forEach(System.out::println);
        // Vérification Alice TTC : 250 * 1.2 = 300
        assertEquals(300.0, q5aResults.get(0).getTotalTtc());

        // Q5-B - CA TTC Pondéré
        BigDecimal q5bWeightedTtc = retriever.computeWeightedTurnoverTtc();
        System.out.println("\nQ5-B - computeWeightedTurnoverTtc() : " + q5bWeightedTtc);
        // Le résultat attendu est 990.00
        assertEquals(0, new BigDecimal("990.00").compareTo(q5bWeightedTtc));

        System.out.println("\n--- TOUS LES TESTS SONT RÉUSSIS ---");
    }
}
