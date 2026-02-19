package Services;

public class InvoiceTaxSummary {
    private final int id;
    private final double totalHt;
    private final double totalTva;
    private final double totalTtc;

    // Constructeur
    public InvoiceTaxSummary(int id, double totalHt, double totalTva, double totalTtc) {
        this.id = id;
        this.totalHt = totalHt;
        this.totalTva = totalTva;
        this.totalTtc = totalTtc;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getTotalHt() {
        return totalHt;
    }

    public double getTotalTva() {
        return totalTva;
    }

    public double getTotalTtc() {
        return totalTtc;
    }

    @Override
    public String toString() {
        return String.format("%d | HT %.2f | TVA %.2f | TTC %.2f", id, totalHt, totalTva, totalTtc);
    }

    // Optionnel : equals() et hashCode()
    // Les records les incluent par défaut, il est souvent utile de les remettre
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceTaxSummary that = (InvoiceTaxSummary) o;
        return id == that.id &&
                Double.compare(that.totalHt, totalHt) == 0 &&
                Double.compare(that.totalTva, totalTva) == 0 &&
                Double.compare(that.totalTtc, totalTtc) == 0;
    }

    @Override
    public int hashCode() {
        java.util.Objects.hash(id, totalHt, totalTva, totalTtc);
        return java.util.Objects.hash(id, totalHt, totalTva, totalTtc);
    }
}
