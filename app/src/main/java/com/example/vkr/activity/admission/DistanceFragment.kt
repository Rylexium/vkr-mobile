package com.example.vkr.activity.admission

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vkr.R
import com.example.vkr.databinding.FragmentDistanceBinding
import com.example.vkr.personal_cabinet.PersonalCabinetActivity

class DistanceFragment : Fragment() {
    private var _binding: FragmentDistanceBinding? = null

    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistanceBinding.inflate(inflater, container, false)
        initComponents()
        return binding.root
    }

    private fun initComponents(){ if(PersonalCabinetActivity.idEducation.toInt() < 5) forAbit() else forOther() }

    private fun forAbit() {
        addField("1", resources.getString(R.string.title_step1_abit),
            resources.getString(R.string.distance_budjet_step1_abit), resources.getString(R.string.distance_contract_step1_abit))

        addField("2", resources.getString(R.string.title_step2_abit),
            resources.getString(R.string.distance_budjet_step2_abit), resources.getString(R.string.distance_contract_step2_abit))

        addField("3", resources.getString(R.string.title_step3_abit),
            resources.getString(R.string.distance_budjet_step3_abit), resources.getString(R.string.distance_contract_step3_abit))

        addField("4", resources.getString(R.string.title_step4_abit),
            resources.getString(R.string.distance_budjet_step4_abit), resources.getString(R.string.distance_contract_step4_abit))

        addField("5", resources.getString(R.string.title_step5_abit),
            resources.getString(R.string.distance_budjet_step5_abit), resources.getString(R.string.distance_contract_step5_abit))

        addField("6", resources.getString(R.string.title_step6_abit),
            resources.getString(R.string.distance_budjet_step6_abit), resources.getString(R.string.distance_contract_step6_abit))

        addField("7", resources.getString(R.string.title_step7_abit),
            resources.getString(R.string.distance_budjet_step7_abit), resources.getString(R.string.distance_contract_step7_abit))

        addField("8", resources.getString(R.string.title_step8_abit),
            resources.getString(R.string.distance_budjet_step8_abit), resources.getString(R.string.distance_contract_step8_abit))
    }

    private fun forOther() {
        addField("1", resources.getString(R.string.title_step1_magistr),
            resources.getString(R.string.distance_budjet_step1_magistr), resources.getString(R.string.distance_contract_step1_magistr))

        addField("2", resources.getString(R.string.title_step2_magistr),
            resources.getString(R.string.distance_budjet_step2_magistr), resources.getString(R.string.distance_contract_step2_magistr))

        addField("3", resources.getString(R.string.title_step3_magistr),
            resources.getString(R.string.distance_budjet_step3_magistr), resources.getString(R.string.distance_contract_step3_magistr))

        addField("4", resources.getString(R.string.title_step4_magistr),
            resources.getString(R.string.distance_budjet_step4_magistr), resources.getString(R.string.distance_contract_step4_magistr))

        addField("5", resources.getString(R.string.title_step5_magistr),
            resources.getString(R.string.distance_budjet_step5_magistr), resources.getString(R.string.distance_contract_step5_magistr))

        addField("6", resources.getString(R.string.title_step6_magistr),
            resources.getString(R.string.distance_budjet_step6_magistr), resources.getString(R.string.distance_contract_step6_magistr))

        addField("7", resources.getString(R.string.title_step7_magistr),
            resources.getString(R.string.distance_budjet_step7_magistr), resources.getString(R.string.distance_contract_step7_magistr))

        addField("8", resources.getString(R.string.title_step8_magistr),
            resources.getString(R.string.distance_budjet_step8_magistr), resources.getString(R.string.distance_contract_step8_magistr))
    }

    @SuppressLint("SetTextI18n")
    private fun addField(step : String, title : String, budjet : String, contract : String){
        val inflater = activity?.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.field_step_admission, null)
        rowView.findViewById<TextView>(R.id.textview_step_admission).text = step
        rowView.findViewById<TextView>(R.id.textview_title_admission).text = title
        rowView.findViewById<TextView>(R.id.textview_date_admission).text = "????????????:     $budjet\n????????????????:   $contract"
        binding.fragmentDistanceLayout.addView(rowView)
    }
}