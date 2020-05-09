package com.example.segtp1.models

import java.io.Serializable

class Arquivo : Serializable {
    var nomeArq: String
    var textoArq: String

    constructor(nomeArq: String, textoArq: String) {
        this.nomeArq = nomeArq
        this.textoArq = textoArq
    }
}