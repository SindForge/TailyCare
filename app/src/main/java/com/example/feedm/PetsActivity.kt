package com.example.feedm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_ALLERGIES
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_ANIMAL
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_ID
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_NAME
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_POS
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_STERILIZED
import com.example.feedm.FragmentEditPet.Companion.BUNDLE_WEIGHT
import com.example.feedm.data.Pet
import com.example.feedm.managementClasses.PetsManager
import com.example.feedm.managementClasses.petsAdapter.MyPetsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PetsActivity : AppCompatActivity() {

    // Controla si el fragmento de edición ha sido iniciado
    var fragmentWasStarted = false
    // RecyclerView que muestra la lista de mascotas
    lateinit var paRecyclerView: RecyclerView
    // Controla si el RecyclerView ha sido inicializado
    private var recyclerViewIsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pa_lytConstraint)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configureFragment()  // Configura el fragmento de edición
        innitViews() // Inicializa las vistas del Activity
    }

    // Inicializa y configura las vistas del Activity
    private fun innitViews() {
        // Si hay mascotas registradas, crea y actualiza el RecyclerView
        if (PetsManager(this).getPetList().isNotEmpty()) {
            createRecyclerView()
            updateRecyclerView(PetsManager(this).getPetList())
        }

        // Botón flotante para añadir nuevas mascotas
        val btnAddPet: FloatingActionButton = findViewById(R.id.pa_floatbtnAñadirMascota)
        btnAddPet.setOnClickListener { addPet() }  // Al hacer clic, inicia la actividad para añadir una mascota
    }

    // Crea el RecyclerView y asigna un layout manager
    private fun createRecyclerView() {
        paRecyclerView = findViewById(R.id.pa_RecyclerView)
        paRecyclerView.layoutManager = LinearLayoutManager(this)  // Asigna un layout de lista vertical
        recyclerViewIsInitialized = true  // Marca que el RecyclerView está inicializado
    }

    // Inicia la actividad para añadir una nueva mascota
    private fun addPet() {
        val intent = Intent(this, FormActivity::class.java)
        startActivity(intent)
    }

    // Actualiza el RecyclerView con la lista de mascotas
    private fun updateRecyclerView(pets: List<Pet>) {
        val intent = Intent(this, SearchActivity::class.java)
        val adapter = MyPetsAdapter(this, pets,
            // Al hacer clic en un item, inicia la actividad de búsqueda con la información de la mascota
            onItemClick = { pet ->
                val bundle = Bundle()
                bundle.putInt("petId", pet.id)
                intent.putExtras(bundle)
                startActivity(intent)
            },
            // Al hacer clic largo, muestra un menú emergente con opciones
            onItemLongClick = { view, pet, pos ->
                val popupMenu = PopupMenu(this, view)
                popupMenu.menuInflater.inflate(R.menu.item_menu_petsrecyclerview, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.item1MenuPetRV -> {
                            showFragment(pet, pos)  // Muestra el fragmento de edición de mascota
                            Log.i("step8", "lifecycleScope")
                            recreate()  // Recarga la actividad después de editar
                            true
                        }
                        R.id.item2MenuPetRV -> {
                            eliminarMascota(pet)  // Elimina la mascota seleccionada
                            recreate()  // Recarga la actividad
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        )

        paRecyclerView.adapter = adapter  // Asigna el adaptador al RecyclerView
    }

    // Elimina la mascota seleccionada
    private fun eliminarMascota(pet: Pet) {
        PetsManager(this).deletePet(pet)  // Llama al método para eliminar la mascota en PetsManager
    }

    // Configura el listener para recibir resultados del fragmento de edición
    private fun configureFragment() {
        supportFragmentManager.setFragmentResultListener("result", this) { _, result ->
            val finished = result.getBoolean("finished")
            if (finished) {
                Log.i("step7", "setFragmentResultListener()")
                val fragment = supportFragmentManager.findFragmentByTag("fragment")
                supportFragmentManager.beginTransaction().remove(fragment!!).commit()  // Elimina el fragmento
                recreate()  // Recarga la actividad
            }
        }
    }

    // Muestra el fragmento de edición de mascotas
    private fun showFragment(pet: Pet, pos: Int) {
        if (!fragmentWasStarted) {
            Log.i("step2", "showFragment()")

            // Crea un bundle con la información de la mascota seleccionada
            val bundle = bundleOf(
                BUNDLE_ID to pet.id,
                BUNDLE_POS to pos,
                BUNDLE_ANIMAL to pet.animal,
                BUNDLE_NAME to pet.nombre,
                BUNDLE_STERILIZED to pet.esterilizado,
                BUNDLE_ALLERGIES to pet.alergia,
                BUNDLE_WEIGHT to pet.peso
            )

            // Inicia el fragmento de edición de mascotas
            supportFragmentManager.beginTransaction()
                .replace<FragmentEditPet>(R.id.fragmentContainer_EditPet, "fragment", args = bundle)
                .addToBackStack(null)
                .commit()

            fragmentWasStarted = true  // Marca que el fragmento ha sido iniciado
        }
    }
}
