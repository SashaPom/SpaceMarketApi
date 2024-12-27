package com.cosmo.cats.api.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_gen")
  @SequenceGenerator(name = "category_id_gen", sequenceName = "category_id_seq")
  private Long id;

  private String name;

  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
  private List<ProductEntity> products;
}