package com.example.feedm.petsFeature.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedm.R
import com.example.feedm.core.domain.model.FoodModel
import com.example.feedm.core.domain.model.MealModel
import com.example.feedm.core.ui.theme.TailyCareTheme
import com.example.feedm.core.ui.components.CustomDropDownMenu
import com.example.feedm.petsFeature.ui.viewmodel.FoodViewModel
import com.example.feedm.petsFeature.ui.viewmodel.MealsViewmodel
import com.example.feedm.ui.view.theme.Orange
import com.example.feedm.ui.view.theme.RedSemiTransparent
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddMealActivity : ComponentActivity() {

    private val mealsViewmodel: MealsViewmodel by viewModels()
    private val foodViewModel: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val petId = intent.extras!!.getInt("PetId")
        setContent {
            TailyCareTheme {
                foodViewModel.getFoodsByPetId(petId)
                val foodsList = foodViewModel.foods.observeAsState(initial = emptyList())
                var isNewMeal by remember { mutableStateOf(true) }
                var meal by remember {
                    mutableStateOf(
                        MealModel(
                            petId = petId,
                            mealTime = 0,
                            ration = 0f
                        )
                    )
                }
                var food by remember {
                    mutableStateOf(
                        FoodModel(
                            foodName = "test",
                            foodWeight = 1f,
                            calories = 0f,
                            brand = "test"
                        )
                    )
                }
                val foodsNames = listOf("Nueva comida")
                foodsList.value.forEach{
                    foodsNames.toMutableList().add(it.foodName)
                }



                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                    bottomBar = {
                        BottomAppBar(containerColor = Color.White) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color.White)
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        addMeal(meal)
                                        if (isNewMeal) addFood(food)
                                    },
                                    elevation = FloatingActionButtonDefaults.elevation(1.25.dp),
                                    containerColor = Orange,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(200.dp)
                                ) {
                                    Text(
                                        text = "Agregar",
                                        style = TextStyle(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }) { innerPadding ->
                    Screen(
                        name = food.foodName,
                        isNewFood = isNewMeal,
                        foodsNames = foodsNames,
                        food = food,
                        meal = meal,
                        onCaloriesChange = { food = food.copy(calories = it.toFloat()) },
                        onRationChange = { meal = meal.copy(ration = it.toFloat()) },
                        onNameChange = { food = food.copy(foodName = it) },
                        onHourChange = { meal = meal.copy(mealTime = 0) },
                        onMinChange = { meal = meal.copy(mealTime = 1) },
                        onFoodSelected = {
                            isNewMeal = it == "Nueva comida"
                            if (it != "Nueva comida") {
                                foodsList.value.forEach { foodModel ->
                                    if (it == foodModel.foodName) {
                                        food = foodModel
                                    }
                                }
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

   private fun addMeal(meal: MealModel){
        mealsViewmodel.addMeal(meal)
   }

    private fun addFood(food: FoodModel){
        foodViewModel.addFood(food)
    }

}



@Composable
fun Screen(
    name: String,
    isNewFood: Boolean,
    foodsNames: List<String>,
    food: FoodModel,
    meal: MealModel,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onRationChange: (String) -> Unit,
    onHourChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    onFoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val namePadding by animateDpAsState(
        targetValue = if (isNewFood) 105.dp else 25.dp,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 150f),
        label = "PaddingAnimation"
    )

    val imageScaleAnim by animateDpAsState(
        targetValue = if (isNewFood) 250.dp else 0.dp,
        animationSpec = spring(),
        label = "FoodAnimation"
    )

    val fontSizeAnim by animateFloatAsState(
        targetValue = if (isNewFood) 17f else 0f,
        animationSpec = spring(),
        label = "FoodAnimation"
    )

    val textSize = fontSizeAnim


    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(scrollState)
    ) {

        Box(modifier = Modifier) {
            OutlinedTextField(
                value = name,
                supportingText = {},
                onValueChange = { onNameChange(it) },
                label = { },
                enabled = isNewFood,
                isError = false,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    errorIndicatorColor = Color.Red, errorLabelColor = Color.Red,
                    errorTextColor = Color.Red, errorContainerColor = RedSemiTransparent,
                    focusedContainerColor = Color.White, focusedLabelColor = Orange,
                    focusedIndicatorColor = Orange, disabledContainerColor = Color.White,
                    disabledTextColor = Color.White, disabledLabelColor = Color.White
                ),
                modifier = Modifier
                    .padding(top = namePadding)
                    .width(300.dp)
            )
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .height(60.dp)
                    .width(300.dp)
            ) {
                CustomDropDownMenu(
                    options = foodsNames,
                    title = "Comida",
                    selectedOption = food.foodName,
                    errorCommitting = false,
                    onSelectOption = {
                        onFoodSelected(it)
                    }
                )
            }
        }
        Image(
            painter = painterResource(id = R.mipmap.test_image), contentDescription = "",
            modifier = Modifier.size(imageScaleAnim)
        )
        if (isNewFood) {
            Text(
                text = "You can find that information on a side or on the back of the bag",
                fontSize = textSize.sp,
                modifier = Modifier.padding(horizontal = 70.dp)
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = "Kcal/100gr",
            modifier = Modifier.padding(horizontal = 70.dp)
        )
        OutlinedTextField(
            value = if (isNewFood) food.calories.toString() else food.calories.toString(),
            enabled = isNewFood,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = {},
            onValueChange = { onCaloriesChange(it) },
            label = { },
            isError = false,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                errorIndicatorColor = Color.Red, errorLabelColor = Color.Red,
                errorTextColor = Color.Red, errorContainerColor = RedSemiTransparent,
                focusedContainerColor = Color.White, focusedLabelColor = Orange,
                focusedIndicatorColor = Orange
            ),
            modifier = Modifier
                .width(300.dp)
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = "Ración",
            modifier = Modifier.padding(horizontal = 70.dp)
        )
        OutlinedTextField(
            value = meal.ration.toString(),
            supportingText = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onRationChange(it) },
            label = { },
            isError = false,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                errorIndicatorColor = Color.Red, errorLabelColor = Color.Red,
                errorTextColor = Color.Red, errorContainerColor = RedSemiTransparent,
                focusedContainerColor = Color.White, focusedLabelColor = Orange,
                focusedIndicatorColor = Orange
            ),
            modifier = Modifier
                .width(300.dp)
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .height(60.dp)
                    .width(125.dp)
            ) {
                CustomDropDownMenu(options = listOf("24 h", "23 h"), title = "Hora",
                    selectedOption = "24 h", errorCommitting = false,
                    onSelectOption = { onHourChange(it) })
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .height(60.dp)
                    .width(125.dp)
            ) {
                CustomDropDownMenu(options = listOf("59 min", "58 min"), title = "Min",
                    selectedOption = "59 min", errorCommitting = false,
                    onSelectOption = { onMinChange(it) })
            }
        }
        Spacer(modifier = Modifier.padding(30.dp))

    }
}


@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    TailyCareTheme {
        var isNewMeal by remember { mutableStateOf(true) }
        var meal by remember {
            mutableStateOf(
                MealModel(
                    mealId = -1,
                    petId = -1,
                    mealTime = 0,
                    ration = 0f
                )
            )
        }
        var food by remember {
            mutableStateOf(
                FoodModel(
                    foodId = -1,
                    foodName = "test",
                    foodWeight = 1f,
                    calories = 0f,
                    brand = "test"
                )
            )
        }
        var calories by remember { mutableStateOf("") }
        var ration by remember { mutableStateOf("") }


        Scaffold(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
            bottomBar = {
                BottomAppBar(containerColor = Color.White) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.White)
                    ) {
                        FloatingActionButton(
                            onClick = {
                            },
                            elevation = FloatingActionButtonDefaults.elevation(1.25.dp),
                            containerColor = Orange,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text(
                                text = "Agregar",
                                style = TextStyle(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }) { innerPadding ->
            Screen(
                name = food.foodName,
                isNewFood = isNewMeal,
                foodsNames = emptyList(),
                food = food,
                meal = meal,
                onCaloriesChange = { calories = it },
                onRationChange = { ration = it },
                onNameChange = { food = food.copy(foodName = it) },
                onHourChange = { meal = meal.copy(mealTime = 0) },
                onMinChange = { meal = meal.copy(mealTime = 1) },
                onFoodSelected = {
                    isNewMeal = it == "Nueva comida"
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}