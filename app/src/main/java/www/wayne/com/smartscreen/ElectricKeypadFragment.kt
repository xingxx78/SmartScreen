package www.wayne.com.smartscreen


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


/**
 * A simple [Fragment] subclass.
 */
class ElectricKeypadFragment : Fragment(), View.OnClickListener{

    private var mListener: OnFragmentButtonClickedListener? = null

    fun ElectricKeypadFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val layout = inflater!!.inflate(R.layout.fragment_electric_keypad, container, false)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idArray = intArrayOf(R.id.centerPosLayout_KeypadArea_0_Button,
                R.id.centerPosLayout_KeypadArea_1_Button,
                R.id.centerPosLayout_KeypadArea_2_Button,
                R.id.centerPosLayout_KeypadArea_3_Button,
                R.id.centerPosLayout_KeypadArea_4_Button,
                R.id.centerPosLayout_KeypadArea_5_Button,
                R.id.centerPosLayout_KeypadArea_6_Button,
                R.id.centerPosLayout_KeypadArea_7_Button,
                R.id.centerPosLayout_KeypadArea_8_Button,
                R.id.centerPosLayout_KeypadArea_9_Button,
                R.id.centerPosLayout_KeypadArea_Clear_Button,
                R.id.centerPosLayout_KeypadArea_Delete_Button,
                R.id.centerPosLayout_KeypadArea_Enter_Button,
                R.id.centerPosLayout_KeypadArea_Dot_Button,
                R.id.centerPosLayout_KeypadArea_Star_Button)
        for (id in idArray) {
            val button = view!!.findViewById<View>(id) as Button
            button.setOnClickListener(this)
        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentButtonClickedListener) {
            mListener = context as OnFragmentButtonClickedListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentButtonClickedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        val idArray = intArrayOf(R.id.centerPosLayout_KeypadArea_0_Button,
                R.id.centerPosLayout_KeypadArea_1_Button,
                R.id.centerPosLayout_KeypadArea_2_Button,
                R.id.centerPosLayout_KeypadArea_3_Button,
                R.id.centerPosLayout_KeypadArea_4_Button,
                R.id.centerPosLayout_KeypadArea_5_Button,
                R.id.centerPosLayout_KeypadArea_6_Button,
                R.id.centerPosLayout_KeypadArea_7_Button,
                R.id.centerPosLayout_KeypadArea_8_Button,
                R.id.centerPosLayout_KeypadArea_9_Button,
                R.id.centerPosLayout_KeypadArea_Clear_Button,
                R.id.centerPosLayout_KeypadArea_Delete_Button,
                R.id.centerPosLayout_KeypadArea_Enter_Button,
                R.id.centerPosLayout_KeypadArea_Dot_Button,
                R.id.centerPosLayout_KeypadArea_Star_Button)
        for (id in idArray) {
            var view = getView()!!.findViewById<View>(id)
            view = null
        }
    }

    override fun onClick(view: View) {
        if (mListener != null) {
            mListener!!.OnFragmentButtonClicked(view.id)
        }
    }

    interface OnFragmentButtonClickedListener {
        fun OnFragmentButtonClicked(buttonId: Int)
    }

}// Required empty public constructor