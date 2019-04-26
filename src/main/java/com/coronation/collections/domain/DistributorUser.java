package com.coronation.collections.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/5/19.
 */
@Entity
@Table(name = "distributor_users", uniqueConstraints=
    @UniqueConstraint(columnNames={"user_id", "distributor_id"}))
public class DistributorUser implements IEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;
    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name="modified_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistributorUser that = (DistributorUser) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
