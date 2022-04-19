package com.example.gpsreplay

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream


const val PICK_PDF_FILE = 2

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TEST","start")

        var button: Button? = null
        var txtView: TextView? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->


            val aaa: InputStream? = contentResolver.openInputStream(uri)
            val inputAsString = aaa?.bufferedReader().use { it?.readText() }
            Log.d("TEST",inputAsString.toString())

            //var fileName = uri.path.toString()
            //Log.d("TEST",aaa.toString())
            //fileName = "storage/emulated/0/Download/aa.gpx"
            //txtView?.text = fileName
            //val reader = FileReader(fileName)
            //val txt = reader.readText()
            //reader.close()


        }
        button = findViewById<Button>(R.id.btn)
        txtView = findViewById<TextView>(R.id.textView)
        button?.setOnClickListener {getContent.launch("*/*")}



//        (object: View.OnClickListener {
//            override fun onClick(p0: View?) {
//                //fun openFile(pickerInitialUri: Uri) {
//                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//                        .apply {
//                        addCategory(Intent.CATEGORY_OPENABLE)
//                            type = "*/*"
//                        //type = "*/gpx"
//
//
//                        // Optionally, specify a URI for the file that should appear in the
//                        // system file picker when it loads.
//                        //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//                         }
//                   // }
//                    startActivityForResult(intent, PICK_PDF_FILE)
//
//            }
//        }
//        )
    }

}