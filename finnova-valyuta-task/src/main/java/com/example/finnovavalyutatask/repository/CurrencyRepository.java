package com.example.finnovavalyutatask.repository;

import com.example.finnovavalyutatask.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Integer> {
}
