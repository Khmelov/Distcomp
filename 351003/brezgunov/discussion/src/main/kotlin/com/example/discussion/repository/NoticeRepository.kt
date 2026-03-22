package com.example.discussion.repository

import com.example.discussion.entity.Notice
import org.springframework.data.cassandra.repository.CassandraRepository

interface NoticeRepository : CassandraRepository<Notice, Long>