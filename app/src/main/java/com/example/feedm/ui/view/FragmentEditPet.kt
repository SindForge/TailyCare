package com.example.feedm.ui.view

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.example.feedm.R
import com.example.feedm.databinding.FragmentFormEditPetBinding
import com.example.feedm.domain.model.Pet
import com.example.feedm.ui.viewmodel.PetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class FragmentEditPet : Fragment() {

    private lateinit var pet: Pet
    private var id = -1
    private val bundle = Bundle()
    private val petViewModel: PetViewModel by viewModels()
    private var _binding: FragmentFormEditPetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getInt(BUNDLE_ID) ?: -1
        petViewModel.onCreate()
        Log.i("Depuring","Petviewmodel created on fragment")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormEditPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Depuring","El pos es $id")
        petViewModel.pets.observe(viewLifecycleOwner){ petsList ->
            if(petsList.isNotEmpty()){
                petViewModel.getPet(id)
            }
        }

        petViewModel.pet.observe(viewLifecycleOwner){
            if (it == null){
                Toast.makeText(requireContext(),"Null", Toast.LENGTH_SHORT).show()
            }
            else{
                pet = it
                initViews()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val paLytConstraint = requireActivity().findViewById<ConstraintLayout>(R.id.pa_lytRecyclerView)
        val blurEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
        } else {
            TODO("manejar como se ve la activity cuando VERSION.SDK_INT < S")
        }
        Log.i("step3", "onResume()")
        paLytConstraint.setRenderEffect(blurEffect)
    }

    override fun onStop() {
        super.onStop()
        val paLytConstraint = requireActivity().findViewById<ConstraintLayout>(R.id.pa_lytRecyclerView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            paLytConstraint.setRenderEffect(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.i("step5", "onDestroyView()")
        val paLytConstraint = requireActivity().findViewById<ConstraintLayout>(R.id.pa_lytRecyclerView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            paLytConstraint.setRenderEffect(null)
        }

    }

    private fun initViews() {
        with(binding) {
            Log.i("Depuring","Comienza a bindear")
            fragmentEditPetTxtName.text = pet.nombre
            if (pet.animal == "dog") {
                fragmentEditPetImageView.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.img_dog_illustration, null)
                )
            } else {
                fragmentEditPetImageView.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.gato, null)
                )
            }

            fragmentEditPetTextInputAlergias.setText(pet.alergia)
            if (pet.esterilizado == "No") {
                fragmentEditPetRgEsterilizado.check(R.id.fragmentEditPet_rbSterilized2)
            } else {
                fragmentEditPetRgEsterilizado.check(R.id.fragmentEditPet_rbSterilized1)
            }
            fragmentEditPetTxtWeight.text = String.format(null, "%.1f Kg", pet.peso)
            fragmentEditPetSbWeight.progress = pet.peso.times(10).roundToInt()

            fragmentEditPetSbWeight.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val weightAdjusted = (fragmentEditPetSbWeight.progress / 5) * 5
                    val numberForTxtWeight = weightAdjusted / 10.0
                    fragmentEditPetTxtWeight.text = String.format(null, "%.1f Kg", numberForTxtWeight)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            fragmentEditPetBtnCancelar.setOnClickListener {
                bundle.putBoolean("finished", true)
                parentFragmentManager.setFragmentResult("result", bundle)
            }

            fragmentEditPetBtnConfirmar.setOnClickListener {
                commitEditPet()
            }
        }
    }

    private fun commitEditPet() {
        Log.i("step4", "commitEditPet()")
        with(binding) {
            pet.alergia = fragmentEditPetTextInputAlergias.text.toString()
            pet.peso = fragmentEditPetSbWeight.progress.div(10).toDouble()
            when (fragmentEditPetRgEsterilizado.checkedRadioButtonId) {
                R.id.fragmentEditPet_rbSterilized1 -> pet.esterilizado = "Si"
                R.id.fragmentEditPet_rbSterilized2 -> pet.esterilizado = "No"
            }
        }
        petViewModel.editPet(pet)
        bundle.putBoolean("finished", true)
        parentFragmentManager.setFragmentResult("result", bundle)
        Log.i("step3223", "ENVIADO")
    }

    companion object {
        const val BUNDLE_ID = "-1"
    }
}
