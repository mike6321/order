package com.example.catalogservice.casandra.repository;

import com.example.catalogservice.casandra.entity.Product;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ProductRepository extends CassandraRepository<Product, Long> {
}
