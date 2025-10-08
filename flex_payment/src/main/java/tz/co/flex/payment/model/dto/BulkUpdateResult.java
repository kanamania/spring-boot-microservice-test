package tz.co.flex.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateResult {
    private int totalProcessed;
    private int successfulUpdates;
    private int failedUpdates;
    private List<String> errors = new ArrayList<>();

    public void incrementSuccessful() {
        this.successfulUpdates++;
        this.totalProcessed++;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.failedUpdates++;
        this.totalProcessed++;
    }
}
