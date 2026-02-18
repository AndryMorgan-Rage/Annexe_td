package Services;

public class InvoiceStatusTotals {
    double totalPaid;
    double totalConfirmed;
    double totalDraft;

    @Override
    public String toString() {
        return "InvoiceStatusTotals{" +
                "totalPaid=" + totalPaid +
                ", totalConfirmed=" + totalConfirmed +
                ", totalDraft=" + totalDraft +
                '}';
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(double totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public double getTotalDraft() {
        return totalDraft;
    }

    public void setTotalDraft(double totalDraft) {
        this.totalDraft = totalDraft;
    }

    public InvoiceStatusTotals(double totalPaid, double totalConfirmed, double totalDraft) {
        this.totalPaid = totalPaid;
        this.totalConfirmed = totalConfirmed;
        this.totalDraft = totalDraft;
    }
}
