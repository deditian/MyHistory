package com.dedi.myhistory.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dedi.myhistory.R
import com.dedi.myhistory.model.HistoryModel
import com.dedi.myhistory.ui.map.MapLocation
import com.dedi.myhistory.util.Utility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import es.dmoral.toasty.Toasty

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var homeActivityAdapter: HomeActivityAdapter? = null
    val viewModel:HomeActivityViewModel by inject()

    private val historyModel : ArrayList<HistoryModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        observeViewModelRequest()



        homeActivityAdapter = HomeActivityAdapter(this)

        rv_history?.layoutManager = LinearLayoutManager(this)
        rv_history?.setHasFixedSize(true)
        rv_history?.adapter = homeActivityAdapter

//        fab.setOnClickListener {
//            addData()
//
//        }

        var sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })

        bottom_sheet.setOnClickListener {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

                val title = edt_bs_title.text.toString()
                val currentDate = SimpleDateFormat("dd/M/yyyy hh:mm").format(Date())
                val location = edt_bs_location.text.toString()
                val description = edt_bs_description.text.toString()
                var latlong = Utility(this).getValueString("label_address").toString()
                edt_bs_location.text = latlong


                edt_bs_location.setOnClickListener {

                }
                btn_bs_add.setOnClickListener {
                    Log.i("his","hasil : "+edt_bs_title.text)
                    viewModel.saveHis(
                        HistoryModel(
                            title,
                            currentDate,
                            location,
                            description
                        )
                    )
                    observeViewModelRequest()
                    Toasty.success(this,"Success Add Data",Toasty.LENGTH_LONG).show()
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }

                img_location.setOnClickListener {
                    val mIntent = Intent(this, MapLocation::class.java)
                    startActivity(mIntent)
                }

            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        }


    }

//    private fun addData() {
//        val dialogBuilder = AlertDialog.Builder(this)
//        val vieww = layoutInflater.inflate(R.layout.input_dialog, null)
//        dialogBuilder.setCancelable(false)
//        dialogBuilder.setView(vieww)
//        dialogBuilder.setTitle("Masukkan data baru")
//        val title = vieww.edt_titile
//        dialogBuilder.setPositiveButton("Tambahkan") { _: DialogInterface, _: Int ->
//            val title = title.text.toString()
//            val location = vieww.edt_location.text.toString()
//            val description = vieww.edt_description.text.toString()
//            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
//            val currentDate = sdf.format(Date())
//            viewModel.saveHis(
//                HistoryModel(title,currentDate,location,description)
//            )
//            observeViewModelRequest()
//
//
//        }
//        dialogBuilder.setNegativeButton("Batal") { _: DialogInterface, _: Int -> }
//
//        dialogBuilder.show()
//    }

    private fun observeViewModelRequest() {
        viewModel.getAllHistory().observe(this, Observer {data ->
            if (!data.isNullOrEmpty()){
                txt_empty.visibility = View.GONE

            }else{
                txt_empty.visibility = View.VISIBLE
            }
            homeActivityAdapter?.submitList(data)
            homeActivityAdapter?.notifyDataSetChanged()
        })
    }


}
