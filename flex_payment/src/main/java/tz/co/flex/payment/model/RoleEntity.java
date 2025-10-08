package tz.co.flex.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "roles")
@SQLDelete(sql = "UPDATE roles SET deleted = true, deleted_at = NOW() WHERE name = ?")
@Where(clause = "deleted = false")
public class RoleEntity extends BaseEntity {
    
    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role name;

    public RoleEntity(Role name) {
        this.name = name;
    }
}
