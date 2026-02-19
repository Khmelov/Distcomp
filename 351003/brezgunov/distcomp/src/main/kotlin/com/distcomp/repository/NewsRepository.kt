package com.distcomp.repository

import com.distcomp.entity.News
import org.springframework.data.jpa.repository.JpaRepository

interface NewsRepository : JpaRepository<News, Long>
