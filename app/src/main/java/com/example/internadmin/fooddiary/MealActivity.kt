package com.example.internadmin.fooddiary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_meal.*
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import java.util.*


class MealActivity : AppCompatActivity() {

    private lateinit var mydate: Date
    private var servingcounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)


        btn_expandnutritionfacts.setOnClickListener{
            expandable_layout.toggle()
        }

        btn_datetimepicker.setOnClickListener {
            SingleDateAndTimePickerDialog.Builder(this)
                    .mainColor(resources.getColor(R.color.colorPrimary))
                    .title("Set Meal Time")
                    .listener(object : SingleDateAndTimePickerDialog.Listener {
                        override fun onDateSelected(date: Date) {
                            mydate = date
                        }
                    }).display()
        }

        btn_pluspizzacount.setOnClickListener{
            servingcounter++
            pizzacounternumber.text = servingcounter.toString()
            if(servingcounter > 0){
                pizzacounter.visibility = View.VISIBLE
            }
        }

        btn_minuspizzacount.setOnClickListener{
            servingcounter--
            if(servingcounter <= 0) {
                servingcounter = 0
                pizzacounter.visibility = View.GONE
            }
            pizzacounternumber.text = servingcounter.toString()
        }


    }
}
