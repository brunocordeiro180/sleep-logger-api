package com.noom.interview.fullstack.sleep.model

import javax.persistence.*

@Entity
@Table(name = "\"user\"")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
)