package Seoul_Milk.sm_server.login.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "REFRESH_REPOSITORY")
@Getter
@Setter
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "REPOSITORY_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity memberEntity;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;
    @Column(name = "EXPIRATION")
    private Date expiration;
}
