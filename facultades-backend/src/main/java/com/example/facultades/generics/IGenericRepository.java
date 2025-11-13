package com.example.facultades.generics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IGenericRepository<E extends BaseEntity, ID extends  Number> extends JpaRepository<E, ID> {
}
