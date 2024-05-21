package two.two_user.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;

    public BudException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }

}
