package com.distcomp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Label that may be attached to many issues, stored in {@code distcomp.tbl_tag}.
 *
 * <p>Names are unique: an issue is allowed to reference tags by name, and such
 * a name then resolves to the existing tag or creates a new one.</p>
 */
@Entity
@Table(name = "tbl_tag", schema = "distcomp",
        uniqueConstraints = @UniqueConstraint(name = "uk_tag_name", columnNames = "name"))
public class Tag extends AbstractEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag{id=" + getId() + ", name='" + name + "'}";
    }
}
