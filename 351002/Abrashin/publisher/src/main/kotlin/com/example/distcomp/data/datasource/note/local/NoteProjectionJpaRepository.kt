package com.example.distcomp.data.datasource.note.local

import com.example.distcomp.data.dbo.NoteProjectionDbo
import org.springframework.data.jpa.repository.JpaRepository

interface NoteProjectionJpaRepository : JpaRepository<NoteProjectionDbo, Long>
