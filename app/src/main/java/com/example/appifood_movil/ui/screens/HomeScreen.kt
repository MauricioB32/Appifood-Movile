package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.appifood_movil.R
import com.example.appifood_movil.data.buscarRestaurantes
import com.example.appifood_movil.data.restaurantes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import com.example.appifood_movil.data.Restaurante
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.statusBarsPadding

data class Filtro(
    val categoria: String = "Todas",
    val precioMax: Float = 100000f,
    val ratingMin: Float = 1f
)

val listaProductosAppiFood = listOf(
    Producto(1, "Cheeseburger", "$25.000", R.drawable.cheese, "Hamburguesas"),
    Producto(2, "Big Mac", "$32.000", R.drawable.bicmac, "Hamburguesas"),
    Producto(7, "Hamburguesa", "$15.000", R.drawable.clasica, "Hamburguesas"),
    Producto(3, "Philadelphia", "$28.000", R.drawable.philadelphia, "Sushi"),
    Producto(4, "Ojo de Tigre", "$35.000", R.drawable.ojotigre, "Sushi"),
    Producto(5, "Coca Cola 1L", "$6.000", R.drawable.cocacola, "Bebidas"),
    Producto(6, "Ramen Tonkotsu", "$22.000", R.drawable.ramen, "Sopas")
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var categoriaSeleccionada by remember { mutableStateOf("Hamburguesas") }
    var searchText by remember { mutableStateOf("") }
    var resultados by remember { mutableStateOf(restaurantes) }
    var cantidadEnCarrito by remember { mutableStateOf(0) }
    var filtro by remember { mutableStateOf(Filtro()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = {
                    scope.launch { drawerState.close() }
                },
                onApplyFilter = {
                    filtro = it
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            item {
                HeaderSection(
                    searchText = searchText,
                    onSearchChange = {
                        searchText = it
                        resultados = buscarRestaurantes(it)
                    },
                    categoriaSeleccionada = categoriaSeleccionada,
                    onCategoriaSelected = { categoriaSeleccionada = it },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }


            if (searchText.length > 2) {
                items(resultados) { restaurante ->
                    RestaurantSearchResultCard(restaurante) {
                        navController.navigate("restaurantDetail/${restaurante.nombre}")
                    }
                }
            } else {
                item {
                    AnimatedContent(
                        targetState = categoriaSeleccionada,
                        transitionSpec = {
                            fadeIn(tween(400)) + scaleIn(initialScale = 0.92f) togetherWith fadeOut(tween(200))
                        }, label = ""
                    ) { targetCategoria ->
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            val filtrados = listaProductosAppiFood.filter { producto ->

                                val cumpleCategoria =
                                    filtro.categoria == "Todas" || producto.categoria == filtro.categoria

                                val precioNumero = producto.precio
                                    .replace("$", "")
                                    .replace(".", "")
                                    .toFloat()

                                val cumplePrecio = precioNumero <= filtro.precioMax

                                // (simulado por ahora)
                                val ratingProducto = 4f
                                val cumpleRating = ratingProducto >= filtro.ratingMin

                                cumpleCategoria && cumplePrecio && cumpleRating
                            }
                            items(filtrados, key = { it.id }) { prod ->
                                FoodItemCard(
                                    nombre = prod.nombre,
                                    imagenRes = prod.imagen,
                                    precio = prod.precio,
                                    onNavigate = {
                                        navController.navigate("productDetail/${prod.nombre}/${prod.precio}/${prod.imagen}")
                                    },
                                    onAddToCart = { cantidadEnCarrito++ }
                                )
                            }
                        }
                    }
                }
            }
        }


        }
        BottomNavigationBar(cantidad = cantidadEnCarrito, navController = navController)
    }
}

@Composable
fun DrawerContent(
    onClose: () -> Unit,
    onApplyFilter: (Filtro) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 🔥 ESTO ARREGLA EL FONDO
    ) {
        FilterContent(
            filtroActual = Filtro(),
            onApply = {
                onApplyFilter(it)
            },
            onClose = onClose
        )
    }
}


@Composable
fun FilterContent(
    filtroActual: Filtro,
    onApply: (Filtro) -> Unit,
    onClose: () -> Unit
) {

    var price by remember { mutableStateOf(50000f) }
    var selectedCategory by remember { mutableStateOf("Todas") }
    var rating by remember { mutableStateOf(3f) }

    val categorias = listOf("Todas", "Hamburguesas", "Sushi", "Bebidas", "Sopas")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // 🔥 ajusta automáticamente
            .padding(20.dp)
    ){

        // 🔴 Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Filtros", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Text(
                "Cerrar",
                color = Color(0xFFFF4B3A),
                modifier = Modifier.clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 💰 Precio
        Text("Precio máximo", fontWeight = FontWeight.Bold)

        Slider(
            value = price,
            onValueChange = { price = it },
            valueRange = 10000f..100000f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF4B3A),
                activeTrackColor = Color(0xFFFF4B3A)
            )
        )

        Text("$${price.toInt()}", color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        // 🍔 Categorías
        Text("Categoría", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categorias.forEach { cat ->

                val isSelected = cat == selectedCategory

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFFFF4B3A) else Color(0xFFF1F1F1))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                ) {
                    Text(
                        cat,
                        color = if (isSelected) Color.White else Color.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ⭐ Rating
        Text("Calificación mínima", fontWeight = FontWeight.Bold)

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF4B3A),
                activeTrackColor = Color(0xFFFF4B3A)
            )
        )

        Text("${rating.toInt()} estrellas ⭐", color = Color.Gray)

        Spacer(modifier = Modifier.height(30.dp))

        // 🔥 BOTÓN APLICAR
        Button(
            onClick = {
                onApply(
                    Filtro(
                        categoria = selectedCategory,
                        precioMax = price,
                        ratingMin = rating
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A))
        ) {
            Text("APLICAR FILTROS", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedCartIcon(cantidad: Int) {
    BadgedBox(
        badge = {
            if (cantidad > 0) {
                Badge(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                ) {
                    Text("$cantidad", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        modifier = Modifier.padding(end = 16.dp, top = 8.dp)
    ) {

    }
}

@Composable
fun FoodItemCard(
    nombre: String,
    imagenRes: Int,
    precio: String,
    onNavigate: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(top = 60.dp, bottom = 10.dp)
            .clickable { onNavigate() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(140.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            )
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text(nombre, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text("AppiFood Special", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(precio, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B3A))
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(35.dp).background(Color(0xFFFF4B3A), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryText(text: String, active: Boolean, onSelect: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    val textColor by animateColorAsState(
        targetValue = if (active) Color.White else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onSelect() }
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .width(if (active) 40.dp else 0.dp)
                .height(4.dp)
                .background(Color(0xFFFF4B3A), RoundedCornerShape(2.dp))
                .animateContentSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(cantidad: Int, navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFFF4B3A), RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) { Icon(Icons.Default.Home, null, tint = Color.White) }
            IconButton(onClick = { navController.navigate("profile") }) { Icon(Icons.Default.Person, null, tint = Color.White) }
            Spacer(modifier = Modifier.width(40.dp))
            IconButton(onClick = {navController.navigate("orderHistory")}) { Icon(Icons.Default.Message, null, tint = Color.White) }
            IconButton(onClick = {
                navController.navigate("favorites")
            }) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-42).dp)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape)
                .background(Color(0xFFFF4B3A), CircleShape)
                .clickable { navController.navigate("cart") },
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (cantidad > 0) {
                        Badge(
                            containerColor = Color.White,
                            contentColor = Color(0xFFFF4B3A),
                            modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                        ) {
                            Text("$cantidad", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun RestaurantSearchResultCard(restaurante: Restaurante, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = restaurante.imagenRes),
                contentDescription = null,
                modifier = Modifier.size(85.dp).clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(restaurante.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(restaurante.direccion, color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Color(0xFFFF4B3A), modifier = Modifier.size(14.dp))
                    Text(" ${restaurante.horario}", fontSize = 11.sp, color = Color.Gray)

                    if (restaurante.tieneDomicilio) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(Icons.Default.DeliveryDining, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Text(" Domicilio", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(
    searchText: String,
    onSearchChange: (String) -> Unit,
    categoriaSeleccionada: String,
    onCategoriaSelected: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(410.dp)
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.burger_background_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        onMenuClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "¿Qué deseas comer\nhoy?",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 35.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.width(280.dp).height(55.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val categorias = listOf("Hamburguesas", "Sushi", "Bebidas", "Sopas")
                categorias.forEach { nombre ->
                    CategoryText(
                        text = nombre,
                        active = categoriaSeleccionada == nombre
                    ) { onCategoriaSelected(nombre) }
                }
            }
        }
    }
}