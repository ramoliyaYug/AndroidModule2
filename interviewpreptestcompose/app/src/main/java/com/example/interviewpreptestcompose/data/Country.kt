package com.example.interviewpreptestcompose.data

data class Country(
    val name: Name,
    val capital: List<String>? = null,
    val region: String,
    val population: Long,
    val area: Double? = null,
    val flags: Flags,
    val currencies: Map<String, Currency>? = null,
    val languages: Map<String, String>? = null
)

data class Name(
    val common: String,
    val official: String
)

data class Flags(
    val png: String,
    val svg: String? = null
)

data class Currency(
    val name: String,
    val symbol: String? = null
)
