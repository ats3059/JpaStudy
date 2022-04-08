package hellojpa;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
public class Address {
    @Column(name="TEST_ADDRESS")
    private String address;
    @Column(name="TEST_CODE")
    private String code;

    public Address() {
    }

    public Address(String address, String code) {
        this.address = address;
        this.code = code;
    }
}
