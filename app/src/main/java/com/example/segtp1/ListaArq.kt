package com.example.segtp1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segtp1.adapters.ArquivoAdapter
import com.example.segtp1.models.Arquivo
import kotlinx.android.synthetic.main.activity_lista_arq.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


class ListaArq : AppCompatActivity() {

    val REQUEST_PERMISSIONS_CODE = 128

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_arq)

        var arquivos = buscarListaArq() as List<Arquivo>
        configurarRecyclerView(arquivos)
    }

    fun configurarRecyclerView(arquivos : List<Arquivo>)
    {
        listagem_arquivos.layoutManager = LinearLayoutManager(this)
        listagem_arquivos.adapter = ArquivoAdapter(arquivos)
    }

    fun buscarListaArq() : ArrayList<Arquivo>
    {
        var lista : ArrayList<Arquivo> = ArrayList()

        var path = getExternalFilesDir(null)
        var dir = File(path, "/ARQSLOC")
        if (dir.exists() || dir.isDirectory) {
            File("/storage/emulated/0/Android/data/com.example.segtp1/files/ARQSLOC").listFiles()
                .forEach {
                    var texto = callReadFromSDCard(null, it.name.toString())
                    if (!texto!!.isNullOrBlank())
                        lista.add(Arquivo(it.name.toString(), texto.toString()))
                }
        }

        return lista
    }

    private fun callReadFromSDCard(view: View?, nomeArquivo: String) : String? {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                )) {
                callDialog(
                    "É preciso a liberar READ_EXTERNAL_STORAGE",
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                )
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS_CODE)
            }
        } else {
            var texto = readFile(nomeArquivo)
            if(texto.isNotEmpty())
                return texto.toString()
        }
        return null
    }

    private fun readFile(nomeArquivo : String) : StringBuilder{
        var path = getExternalFilesDir(null)
        var dir = File(path, "/ARQSLOC")
        val file = File(dir, nomeArquivo)

        if(!file.exists()) {
            Toast.makeText(this,
                "Arquivo não encontrado",
                Toast.LENGTH_SHORT).show()
            return StringBuilder()
        }
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return text
    }

    private fun callDialog(mensagem: String,
                           permissions: Array<String>) {
        var mDialog = AlertDialog.Builder(this)
            .setTitle("Permissão")
            .setMessage(mensagem)
            .setPositiveButton("Ok")
            { dialog, id ->
                ActivityCompat.requestPermissions(
                    this, permissions,
                    REQUEST_PERMISSIONS_CODE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel")
            { dialog, id ->
                dialog.dismiss()
            }
        mDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                var i = 0
                while (i < permissions.size) {
                    if (permissions[i].equals(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            ignoreCase = true)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                    }
                    i++
                }
            }
        }
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults)
    }
}
