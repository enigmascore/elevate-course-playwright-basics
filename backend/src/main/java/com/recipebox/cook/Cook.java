package com.recipebox.cook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table( name = "cooks" )
public class Cook {

    public enum Level { HOME, PRO }

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "cooks_seq" )
    @SequenceGenerator( name = "cooks_seq", sequenceName = "cooks_seq", allocationSize = 1 )
    private Long id;

    @Column( nullable = false )
    private String name;

    @Column( nullable = false )
    private String email;

    @Enumerated( EnumType.STRING )
    @Column( nullable = false )
    private Level level;

    protected Cook() {
    }

    public Cook( String name, String email, Level level ) {
        this.name = name;
        this.email = email;
        this.level = level;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Level getLevel() { return level; }
}
