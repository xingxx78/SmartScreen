package www.wayne.com.smartscreen

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.wayne.www.waynelib.fdc.FdcClient
import com.wayne.www.waynelib.fdc.OnFdcServiceResponseReceivedListener
import com.wayne.www.waynelib.fdc.message.ServiceResponse
import com.wayne.www.waynelib.webservice.CloudServiceGenerator
import com.wayne.www.waynelib.webservice.ReceiptService
import com.wayne.www.waynelib.webservice.CloudServiceAddressBook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MainActivity : AppCompatActivity(), ElectricKeypadFragment.OnFragmentButtonClickedListener {

    private var editText: EditText? = null
    private var textView: TextView? = null
    internal var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)//设置全屏

        val fragmentContainer = findViewById<View>(R.id.electric_keypad_container)
        if (fragmentContainer != null) run {
            val details = ElectricKeypadFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.electric_keypad_container, details)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(null)
            ft.commit()
        }
        textView = findViewById(R.id.textViewPleaseInput) as TextView
        textView!!.setTextSize(45f)
        editText = findViewById(R.id.editTextAuthCode) as EditText
        editText!!.setTextColor(Color.BLACK)
        editText!!.setTextSize(60f)
        editText!!.setCursorVisible(false)
        editText!!.setFocusable(false)
        editText!!.setFocusableInTouchMode(false)
    }

    private fun ShowInvalidLength() {
        AlertDialog.Builder(this@MainActivity).
                setMessage("短码长度必须是4位").
                setCancelable(false).
                setPositiveButton("继续") { dialogInterface, i -> }.
                show()
    }

    override fun OnFragmentButtonClicked(buttonId: Int) {

        if (buttonId == R.id.centerPosLayout_KeypadArea_Clear_Button) {
            editText!!.setText("")
        } else if (buttonId == R.id.centerPosLayout_KeypadArea_Delete_Button) {
            if (editText!!.getText().length > 0) {
                editText!!.setText(editText!!.getText().subSequence(0, editText!!.getText().length - 1))
            }
        } else if (buttonId == R.id.centerPosLayout_KeypadArea_Enter_Button) {
            val num = editText!!.getText().toString()
            if (num.length != 4) {
                ShowInvalidLength()
            } else {
                //SendGetDspConfiguration();
                }
        } else {
            val num = editText!!.getText().toString()
            if (num.length >= 4) {
                ShowInvalidLength()
            } else {
                when (buttonId) {
                    R.id.centerPosLayout_KeypadArea_0_Button -> editText!!.append("0")
                    R.id.centerPosLayout_KeypadArea_1_Button -> editText!!.append("1")
                    R.id.centerPosLayout_KeypadArea_2_Button -> editText!!.append("2")
                    R.id.centerPosLayout_KeypadArea_3_Button -> editText!!.append("3")
                    R.id.centerPosLayout_KeypadArea_4_Button -> editText!!.append("4")
                    R.id.centerPosLayout_KeypadArea_5_Button -> editText!!.append("5")
                    R.id.centerPosLayout_KeypadArea_6_Button -> editText!!.append("6")
                    R.id.centerPosLayout_KeypadArea_7_Button -> editText!!.append("7")
                    R.id.centerPosLayout_KeypadArea_8_Button -> editText!!.append("8")
                    R.id.centerPosLayout_KeypadArea_9_Button -> editText!!.append("9")
                    R.id.centerPosLayout_KeypadArea_Dot_Button, R.id.centerPosLayout_KeypadArea_Star_Button -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }

//    private fun SendGetDspConfiguration() {
//        FdcClient.getDefault().sendGetDspConfigurationRequestAsync(
//                OnFdcServiceResponseReceivedListener { sender, serviceResponse ->
//                    //region Description
//                    if (serviceResponse == null || serviceResponse.overallResult != "Success") {
//                        Log.i("", "Failed on DspConfigurationRequest, restart FdcClient...")
//                        FdcClient.getDefault().restart()
//                        return@OnFdcServiceResponseReceivedListener
//                    } else {
//                        Log.i("","success")
//                    }
//                }, 60000)
//    }

    private fun sendAuthCodeToCloud(num: String) {
        CloudServiceAddressBook.ReceiptServiceAddress = "http://192.168.1.111:8899";
        CloudServiceGenerator.setApiBaseUrl(CloudServiceAddressBook.ReceiptServiceAddress)
        CloudServiceGenerator.createService(ReceiptService::class.java).getShiftReceiptByShiftId(1, 10).enqueue(
                object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (!response.isSuccessful() || response.code() != 200) {
                            onFailure(null, Throwable("unSuccessful or not 200"))
                            return
                        }
                        println("success")
//                        this@PosMainActivity.cloudServiceProgressDialog.dismiss()
//                        val printer = AidlUtil.getInstance()
//                        printer.printShiftReceipt(receiptPaperMaxCharPerLine, response.body())
                    }

                    override fun onFailure(call: Call<String>?, t: Throwable) {
//                        fileLogger.error("Printing failed for shift receipt with id: " + targetShiftId + ", detail: " + t.message)
//                        this@PosMainActivity.cloudServiceProgressDialog.dismiss()
                        println("failure")
                    }
                })
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}
