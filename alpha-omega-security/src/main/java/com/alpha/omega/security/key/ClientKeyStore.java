package com.globalpayments.security.key;

import com.globalpayments.security.jpa.AuditingEntityClassic;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Cacheable
@Table(name="client_keystore",uniqueConstraints={@UniqueConstraint(columnNames = {"client_id" })})
@EntityListeners(AuditingEntityListener.class)
public class ClientKeyStore extends AuditingEntityClassic implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100,name = "client_id")
    private String clientId;


    @NotNull
    @Lob
    @Column(name="key_store")
    private String keyStore;

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }



    /*
    @NotNull
    @Lob
    @Column(name="key_store")
    private byte[] keyStore;

    public byte[] getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(byte[] keyStore) {
        this.keyStore = keyStore;
    }

     */

    public ClientKeyStore() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientKeyStore keyStore = (ClientKeyStore) o;
        return Objects.equals(id, keyStore.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyStore{");
        sb.append("id=").append(id);
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", keyStore='").append(keyStore).append('\'');
        sb.append(", createdDate=").append(createdDate);
        sb.append(", modifiedDate=").append(modifiedDate);
        sb.append(", createdBy='").append(createdBy).append('\'');
        sb.append(", modifiedBy='").append(modifiedBy).append('\'');
        sb.append(", disabled=").append(disabled);
        sb.append('}');
        return sb.toString();
    }
}
