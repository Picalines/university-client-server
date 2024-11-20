package com.kostenko.repository

import com.kostenko.model.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileEntityRepository : JpaRepository<FileEntity, Long>