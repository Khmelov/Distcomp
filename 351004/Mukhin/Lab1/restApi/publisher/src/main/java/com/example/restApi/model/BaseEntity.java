    package com.example.restApi.model;

    import jakarta.persistence.Column;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.MappedSuperclass;
    import java.time.LocalDateTime;

    @MappedSuperclass
    public abstract class BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        protected Long id;

        @Column(name = "created")
        protected LocalDateTime created;

        @Column(name = "modified")
        protected LocalDateTime modified;

        public BaseEntity() {
            this.created = LocalDateTime.now();
            this.modified = LocalDateTime.now();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LocalDateTime getCreated() {
            return created;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }

        public LocalDateTime getModified() {
            return modified;
        }

        public void setModified(LocalDateTime modified) {
            this.modified = modified;
        }
    }
