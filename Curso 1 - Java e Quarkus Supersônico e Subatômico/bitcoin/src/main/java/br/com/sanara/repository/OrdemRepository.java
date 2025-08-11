package br.com.sanara.repository;

import br.com.sanara.model.Ordem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped //cria uma instancia e compartilha para todos que precisarem usar esse repository
public class OrdemRepository implements PanacheRepository<Ordem> {

}
