package com.example.finnovavalyutatask.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currencies")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "currency_code")
    private String currencyCode; // "Ccy" maydoniga mos

    @Column(name = "name_uz")
    private String nameUz; // "CcyNm_UZ" maydoniga mos

    @Column(name = "name_ru")
    private String nameRu; // "CcyNm_RU" maydoniga mos

    @Column(name = "name_en")
    private String nameEn; // "CcyNm_EN" maydoniga mos

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "difference")
    private BigDecimal difference;

    @Column(name = "date")
    private LocalDate date;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CurrencyEntity that = (CurrencyEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
