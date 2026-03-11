package com.distcomp.repository

import com.distcomp.entity.Notice
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<Notice, Long>
