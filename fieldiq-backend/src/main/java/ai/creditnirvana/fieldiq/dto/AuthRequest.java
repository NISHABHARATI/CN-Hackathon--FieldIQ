package ai.creditnirvana.fieldiq.dto;

public class AuthRequest {
    private String phone;
    private String pin;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}