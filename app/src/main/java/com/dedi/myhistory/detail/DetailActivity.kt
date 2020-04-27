package com.dedi.myhistory.detail

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.dedi.myhistory.R
import com.dedi.myhistory.model.HistoryModel
import com.dedi.myhistory.model.ParceModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.input_dialog.view.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    val viewmodel : DetailActivityViewModel by inject()

    private val list_data by lazy {
        intent.getParcelableExtra<ParceModel>("list_data")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Log.i("DetailActivity", "datanya "+list_data)
        txttitle.text = list_data.title
        txt_location.text = list_data.location
        txt_date.text = list_data.date
        txt_des.text = list_data.description
    }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete -> {
                viewmodel.delete(
                    HistoryModel(
                        list_data.title,
                        list_data.date,
                        list_data.location,
                        "",
                        "",
                        list_data.description,
                        list_data.id
                    )
                )
                Toasty.success(this,"Delete Data",Toasty.LENGTH_LONG).show()
                finish()
                true
            }

            R.id.action_edit -> {
                edit_Data()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun edit_Data() {
        val dialogBuilder = AlertDialog.Builder(this)
        val vieww = layoutInflater.inflate(R.layout.input_dialog, null)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(vieww)
        dialogBuilder.setTitle("Masukkan data baru")
        val title = vieww.edt_titile
        dialogBuilder.setPositiveButton("Tambahkan") { _: DialogInterface, _: Int ->
            val title = title.text.toString()
            val location = vieww.edt_location.text.toString()
            val description = vieww.edt_description.text.toString()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
            val currentDate = sdf.format(Date())
            viewmodel.update(
                HistoryModel(
                    title,
                    currentDate,
                    location,
                    "",
                    "",
                    description,
                    list_data.id
                )
            )

            Toasty.success(this,"Success Edit Data",Toasty.LENGTH_LONG).show()
            finish()
        }
        dialogBuilder.setNegativeButton("Batal") { _: DialogInterface, _: Int -> }

        dialogBuilder.show()
    }
}
