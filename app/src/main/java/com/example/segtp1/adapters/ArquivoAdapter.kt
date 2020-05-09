package com.example.segtp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.segtp1.R
import com.example.segtp1.models.Arquivo

class ArquivoAdapter(listaArquivos : List<Arquivo>) : RecyclerView.Adapter<ArquivoAdapter.ArquivoViewHolder>() {

    var listaArq = listaArquivos

    class ArquivoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var nomeArquivo = itemView.findViewById<TextView>(R.id.nomeArquivoLista)
        var dadosArq = itemView.findViewById<TextView>(R.id.textoArquivoLista)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArquivoViewHolder {
        val card = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.arquivoitem, parent, false)

        return ArquivoViewHolder(card)
    }

    override fun getItemCount() = listaArq.size

    override fun onBindViewHolder(holder: ArquivoViewHolder, position: Int) {

        holder.nomeArquivo.text = "Arquivo: " + listaArq[position].nomeArq
        holder.dadosArq.text = listaArq[position].textoArq
    }
}