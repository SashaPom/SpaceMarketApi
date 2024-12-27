package com.cosmo.cats.api.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_entry")
public class OrderEntryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_entry_id_seq")
  @SequenceGenerator(name = "orders_entry_id_seq", sequenceName = "order_entry_id_seq")
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;
}