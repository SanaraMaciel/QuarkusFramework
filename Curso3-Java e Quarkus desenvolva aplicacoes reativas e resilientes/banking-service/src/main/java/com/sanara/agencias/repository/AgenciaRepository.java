package com.sanara.agencias.repository;

import com.sanara.agencias.domain.Agencia;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class AgenciaRepository implements PanacheRepository<Agencia> {


}