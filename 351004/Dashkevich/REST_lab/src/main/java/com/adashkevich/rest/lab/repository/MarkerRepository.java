package com.adashkevich.rest.lab.repository;

import com.adashkevich.rest.lab.model.Marker;
import org.springframework.stereotype.Repository;

@Repository
public class MarkerRepository extends InMemoryCrudRepository<Marker> {}
