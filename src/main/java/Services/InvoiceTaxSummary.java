package Services;

public record InvoiceTaxSummary(
        int id,
        double totalHt,
        double totalTva,
        double totalTtc
) {
    @Override
    public String toString() {
        return String.format("%d | HT %.2f | TVA %.2f | TTC %.2f", id, totalHt, totalTva, totalTtc);
    }
}
