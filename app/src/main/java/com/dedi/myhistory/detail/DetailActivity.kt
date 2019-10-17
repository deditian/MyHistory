package com.dedi.myhistory.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.dedi.myhistory.R
import com.dedi.myhistory.data.HistoryModel
import com.dedi.myhistory.model.ParceModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_content.*
import org.koin.android.ext.android.inject

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
            R.id.action_settings -> {
                viewmodel.delete(HistoryModel(list_data.title,list_data.date,list_data.location,list_data.description,list_data.id) )
                Toasty.success(this,"Delete Data",Toasty.LENGTH_LONG).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
