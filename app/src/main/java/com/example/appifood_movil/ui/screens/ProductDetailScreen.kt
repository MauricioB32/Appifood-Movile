package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.appifood_movil.R
import androidx.compose.foundation.clickable
import androidx.compose.material3.Slider

@Composable
fun ProductDetailScreen(navController: NavController) {

    var portion by remember { mutableStateOf(1) }
    var spicy by remember { mutableStateOf(0.5f) }

    val pricePerUnit = 10000
    val totalPrice = pricePerUnit * portion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        // Barra superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )

            Icon(
                Icons.Default.Search,
                contentDescription = null
            )
        }

        // Imagen producto
        Image(
            painter = painterResource(id = R.drawable.burger_product),
            contentDescription = null,
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Nombre + rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    "Cheeseburger",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "KFC",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "⭐ 4.8",
                fontSize = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = "La hamburguesa con queso de KFC es una opción de comida rápida sabrosa e icónica que presenta una jugosa hamburguesa de pollo con queso derretido y aderezos personalizables.",
            color = Color.Gray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        // SPICY
        Text(
            "Spicy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = spicy,
            onValueChange = { spicy = it },
            colors = SliderDefaults.colors(
                thumbColor = Color.Red,
                activeTrackColor = Color.Red
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "Mild",
                color = Color.Green,
                fontSize = 15.sp
            )

            Text(
                "Hot",
                color = Color.Red,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PORCIONES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "Portion",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = { if (portion > 1) portion-- },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {

                    Text(
                        "-",
                        fontSize = 17.sp
                    )
                }

                Text(
                    "$portion",
                    modifier = Modifier.padding(horizontal = 15.dp),
                    fontSize = 17.sp
                )

                Button(
                    onClick = { portion++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {

                    Text(
                        "+",
                        fontSize = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))

        // PRECIO + BOTON
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .height(60.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    "$$totalPrice",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { },
                modifier = Modifier
                    .height(60.dp)
                    .width(250.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5232))
            ) {

                Text("ORDER NOW")
            }
        }
    }
}