package com.example.safetrace

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactModel(

    val name: String,
    @PrimaryKey
    val number: String
)
