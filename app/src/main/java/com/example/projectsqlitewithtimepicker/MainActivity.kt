package com.example.projectsqlitewithtimepicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        closeKeyBoard()

        setupListofDataIntoRecyclerView()
        btnAdd.setOnClickListener {
            addRecord()
            setupListofDataIntoRecyclerView()
        }
        //Untuk menyembunyikan keyboard ketika pertama kali dipilih
        etDate.inputType = InputType.TYPE_NULL
        etTime.inputType = InputType.TYPE_NULL

        //Aksi yang akan dijalankan ketika tanggal telah terpilih
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DATE, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etDate.setText(sdf.format(cal.time))
            }
        }
        //aksi yang akan dijalankan ketika timepicker di pilih
        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etTime.setText(sdf.format(cal.time))
            }

        }
        // when you click on the edit text, show DatePickerDialog that is set with OnDateSetListener
        etDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                closeKeyBoard()
                DatePickerDialog(this@MainActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
        etTime.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                closeKeyBoard()
                TimePickerDialog( this@MainActivity,
                timeSetListener,
                cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE),true).show()
            }

        })
    }
//    private fun updateDateInView() {
//
//    }
//    private fun updateTimeInView() {
//
//    }

    private fun addRecord() {
        val date = etDate.text.toString()
        val time = etTime.text.toString()
        val dateTime = "${date}(${time})"
        val description = etDescription.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if(!date.isEmpty() && !time.isEmpty() && !description.isEmpty()) {
            val status =
                databaseHandler.addActivity(MyActivityModel(0, dateTime, description))
            if(status > -1) {
                Toast.makeText(applicationContext, "Record Saved", Toast.LENGTH_LONG).show()
                etDate.text.clear()
                etTime.text.clear()
                etDescription.text.clear()
                closeKeyBoard()
            }
        }else{
            Toast.makeText(applicationContext, "Datetime or Description cannot be blank", Toast.LENGTH_LONG).show()
        }
    }

    private fun getItemsList(): ArrayList<MyActivityModel> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val activityList: ArrayList<MyActivityModel> = databaseHandler.viewActivity()

        return activityList
    }

    /**
     * Method to show data to recyclerView
     */
    private fun setupListofDataIntoRecyclerView() {
        if(getItemsList().size > 0){
            rvItemList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            rvItemList.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getItemsList())
            rvItemList.adapter = itemAdapter
        }else{
            rvItemList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    /**
     * Method is used to show the delete alert dialog
     */
    fun deleteRecordAlertDialog(myActivityModel: MyActivityModel) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")

        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, which ->
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteActivity(MyActivityModel(myActivityModel.id, "", ""))

            if (status > -1) {
                Toast.makeText(
                    applicationContext, "Record deleted successfully.", Toast.LENGTH_LONG
                ).show()
                setupListofDataIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No") {dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /**
     * Method is used to show the custome update dialog
     */
    fun updateRecordDialog(myActivityModel: MyActivityModel) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)

        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        //Memecah datetime berdasarkan karakter (
        val datetime = (myActivityModel.time).split("(")
        val date = datetime[0]
        var time = datetime[1]

        //Memecah date berdasarkan karakter /
        val dateList = date.split("/")
        val year = dateList[2].toInt()
        val month = dateList[1].toInt() - 1
        val day = dateList[0].toInt()

        time = time.dropLast(1)
        val timeList = time.split(":")
        //Memecah time berdasarkan karakter :
        val hour = timeList[0].toInt()
        val minute = timeList[1].toInt()

        updateDialog.etUpdateDate.setText(date)
        updateDialog.etUpdateTime.setText(time)
        updateDialog.etUpdateDescription.setText(myActivityModel.description)

        updateDialog.etUpdateDate.inputType = InputType.TYPE_NULL
        updateDialog.etUpdateTime.inputType = InputType.TYPE_NULL

        //Menampilkan DatePicker ketika etUpdateDate di tekan
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DATE, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                updateDialog.etUpdateDate.setText(sdf.format(cal.time))

            }
        }
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                updateDialog.etUpdateTime.setText(sdf.format(cal.time))
            }
        updateDialog.etUpdateDate!!.setOnClickListener {
            closeKeyBoard()
            DatePickerDialog(this,
                dateSetListener,
                // Set DatePickerDialog berdasarkan tanggal dari data yang dipilih
                year , month, day).show()
        }

        updateDialog.etUpdateTime!!.setOnClickListener{
                closeKeyBoard()
                TimePickerDialog( this,
                    timeSetListener, hour, minute,true).show()
        }

        updateDialog.tvUpdate.setOnClickListener(View.OnClickListener {
            val date = updateDialog.etUpdateDate.text.toString()
            val time = updateDialog.etUpdateTime.text.toString()
            val dateTime = "${date}(${time})"
            val description = updateDialog.etUpdateDescription.text.toString()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            if(!date.isEmpty() && !time.isEmpty() && !description.isEmpty()){
                val status = databaseHandler.updateActivity(MyActivityModel(myActivityModel.id, dateTime, description))
                if (status > -1){
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()
                    setupListofDataIntoRecyclerView()
                    updateDialog.dismiss()
                    this.closeKeyBoard()
                }
            }else{
                Toast.makeText(applicationContext, "Time and Description cannot be blank", Toast.LENGTH_LONG).show()
            }
        })
        updateDialog.tvCancel.setOnClickListener(View.OnClickListener {
            updateDialog.dismiss()
        })
        updateDialog.show()

    }
    /**
     * method to hide keyboard
     */
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}