package com.distcomp.repository

import com.distcomp.entity.Marker
import org.springframework.data.jpa.repository.JpaRepository

interface MarkerRepository : JpaRepository<Marker, Long>
