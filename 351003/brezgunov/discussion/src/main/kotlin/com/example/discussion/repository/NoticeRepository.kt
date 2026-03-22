package com.example.discussion.repository

import com.example.discussion.entity.Notice
import org.springframework.data.cassandra.repository.CassandraRepository
import java.util.UUID

interface NoticeRepository : CassandraRepository<Notice, UUID>