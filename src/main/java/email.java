import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class email {
    private Integer email_id;
    private String subject;
    private String message;
    private User sender;
    private User receiver;
    private Timestamp created_at;
    private boolean isDeleted;
    private boolean isRead;

}
