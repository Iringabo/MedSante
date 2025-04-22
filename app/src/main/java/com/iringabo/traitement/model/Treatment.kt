package com.iringabo.traitement.model

data class Treatment(
    var id: String = "",
    var medName: String = "",
    var dosage: Int = 0,
    var quantity: Int = 0,
    var times: List<String> = emptyList(),
    var startDate: String = "", // format ISO yyyy-MM-dd
    var endDate: String = "",   // format ISO yyyy-MM-dd
    var remarks: String = ""
)
