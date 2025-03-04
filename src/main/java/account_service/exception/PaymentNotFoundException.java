package account_service.exception;

public class PaymentNotFoundException extends RuntimeException
{
    public PaymentNotFoundException() {
        super("Payment not found");
    }
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
