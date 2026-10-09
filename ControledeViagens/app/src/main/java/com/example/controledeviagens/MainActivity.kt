
package com.example.controledeviagens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.controledeviagens.ui.theme.ControleDeViagensTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(
    name = "preferencias_motorista"
)

private val CHAVE_MOTORISTA = stringPreferencesKey("nome_motorista")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ControleDeViagensTheme {
                ControleDeViagens()
            }
        }
    }
}

@Composable
fun ControleDeViagens() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var motorista by remember {
        mutableStateOf("")
    }

    LaunchedEffect(context) {
        context.dataStore.data
            .map { preferencias ->
                preferencias[CHAVE_MOTORISTA] ?: ""
            }
            .collect { nome ->
                motorista = nome
            }
    }

    fun salvarMotorista(nome: String) {
        scope.launch {
            context.dataStore.edit { preferencias ->
                preferencias[CHAVE_MOTORISTA] = nome
            }
        }
    }

    var data by remember { mutableStateOf("") }
    var kmInicial by remember { mutableStateOf("") }
    var kmFinal by remember { mutableStateOf("") }
    var litros by remember { mutableStateOf("") }
    var combustivel by remember { mutableStateOf("") }
    var pedagio by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    val viagens = remember {
        mutableStateListOf<Viagem>()
    }

    var totalGasto by remember { mutableStateOf(0.0) }
    var totalKm by remember { mutableStateOf(0.0) }
    var mediaKmLitro by remember { mutableStateOf(0.0) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "lista",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("lista") {
                TelaListaViagens(
                    viagens = viagens,
                    totalGasto = totalGasto,
                    totalKm = totalKm,
                    mediaKmLitro = mediaKmLitro,
                    navController = navController,
                    motorista = motorista,
                    onSalvarMotorista = ::salvarMotorista
                )
            }

            composable("cadastro") {
                TelaCadastroViagem(
                    data = data,
                    kmInicial = kmInicial,
                    kmFinal = kmFinal,
                    litros = litros,
                    combustivel = combustivel,
                    pedagio = pedagio,
                    valor = valor,
                    onDataChange = { data = it },
                    onKmInicialChange = { kmInicial = it },
                    onKmFinalChange = { kmFinal = it },
                    onLitrosChange = { litros = it },
                    onCombustivelChange = { combustivel = it },
                    onPedagioChange = { pedagio = it },
                    onValorChange = { valor = it },
                    onCadastrar = {
                        val viagem = Viagem(
                            data = data,
                            kmInicial = kmInicial.toDouble(),
                            kmFinal = kmFinal.toDouble(),
                            litros = litros.toDouble(),
                            combustivel = combustivel,
                            pedagio = pedagio.toDouble(),
                            valor = valor.toDouble()
                        )

                        viagens.add(viagem)
                        totalGasto += viagem.calcularCusto()
                        totalKm += viagem.calcularDistancia()

                        val totalLitros = viagens.sumOf { it.litros }
                        mediaKmLitro = if (totalLitros > 0) {
                            totalKm / totalLitros
                        } else {
                            0.0
                        }

                        data = ""
                        kmInicial = ""
                        kmFinal = ""
                        litros = ""
                        combustivel = ""
                        pedagio = ""
                        valor = ""

                        navController.navigateUp()
                    },
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun TelaListaViagens(
    viagens: List<Viagem>,
    totalGasto: Double,
    totalKm: Double,
    mediaKmLitro: Double,
    navController: NavController,
    motorista: String,
    onSalvarMotorista: (String) -> Unit
) {
    var novoMotorista by remember(motorista) {
        mutableStateOf(motorista)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5FB))
            .padding(16.dp)
    ) {
        Text(
            text = "Controle de Viagens",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 12.dp
            )
        )

        TextField(
            value = novoMotorista,
            onValueChange = { novoMotorista = it },
            label = { Text("Nome do motorista") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFFF1493),
                focusedLabelColor = Color(0xFFFF1493),
                unfocusedIndicatorColor = Color(0xFFCCCCCC)
            )
        )

        Button(
            onClick = {
                if (novoMotorista.isNotBlank()) {
                    onSalvarMotorista(novoMotorista.trim())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF1493)
            )
        ) {
            Text("Salvar motorista")
        }

        Text(
            text = if (motorista.isNotBlank()) {
                "Motorista: $motorista"
            } else {
                "Nenhum motorista cadastrado"
            },
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Resumo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 10.dp
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResumoItem(
                        titulo = "Total gasto",
                        valor = "R$ %.2f".format(totalGasto)
                    )
                    ResumoItem(
                        titulo = "Total de km",
                        valor = "%.2f km".format(totalKm)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResumoItem(
                        titulo = "Média km/l",
                        valor = "%.2f".format(mediaKmLitro)
                    )
                    ResumoItem(
                        titulo = "Viagens",
                        valor = viagens.size.toString()
                    )
                }
            }
        }

        Text(
            text = "Viagens cadastradas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 10.dp
            )
        )

        if (viagens.isEmpty()) {
            Text(
                text = "Nenhuma viagem cadastrada.",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(viagens) { viagem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Data",
                                        tint = Color(0xFF9C27B0),
                                        modifier = Modifier
                                            .size(22.dp)
                                            .padding(end = 4.dp)
                                    )
                                    Text(
                                        text = viagem.data,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF9C27B0)
                                    )
                                }

                                Text(
                                    text = "R$ %.2f".format(
                                        viagem.calcularCusto()
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF1493)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = "Combustível",
                                    tint = Color(0xFFFF1493),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = viagem.combustivel,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InformacaoViagem(
                                    titulo = "Distância",
                                    valor = "%.2f km".format(
                                        viagem.calcularDistancia()
                                    )
                                )
                                InformacaoViagem(
                                    titulo = "Combustível",
                                    valor = "%.2f L".format(viagem.litros)
                                )
                                InformacaoViagem(
                                    titulo = "Pedágio",
                                    valor = "R$ %.2f".format(viagem.pedagio)
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("cadastro") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF1493)
            )
        ) {
            Text("Nova viagem", fontSize = 16.sp)
        }
    }
}

@Composable
fun ResumoItem(
    titulo: String,
    valor: String
) {
    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
        Text(
            text = titulo,
            fontSize = 13.sp,
            color = Color.Gray
        )
        Text(
            text = valor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0)
        )
    }
}

@Composable
fun InformacaoViagem(
    titulo: String,
    valor: String
) {
    Column {
        Text(
            text = titulo,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun TelaCadastroViagem(
    data: String,
    kmInicial: String,
    kmFinal: String,
    litros: String,
    combustivel: String,
    pedagio: String,
    valor: String,
    onDataChange: (String) -> Unit,
    onKmInicialChange: (String) -> Unit,
    onKmFinalChange: (String) -> Unit,
    onLitrosChange: (String) -> Unit,
    onCombustivelChange: (String) -> Unit,
    onPedagioChange: (String) -> Unit,
    onValorChange: (String) -> Unit,
    onCadastrar: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5FB))
            .padding(16.dp)
    ) {
        Text(
            text = "Cadastro de Viagem",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 20.dp
            )
        )

        FormularioViagem(
            data = data,
            kmInicial = kmInicial,
            kmFinal = kmFinal,
            litros = litros,
            combustivel = combustivel,
            pedagio = pedagio,
            valor = valor,
            onDataChange = onDataChange,
            onKmInicialChange = onKmInicialChange,
            onKmFinalChange = onKmFinalChange,
            onLitrosChange = onLitrosChange,
            onCombustivelChange = onCombustivelChange,
            onPedagioChange = onPedagioChange,
            onValorChange = onValorChange,
            onCadastrar = onCadastrar
        )

        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Voltar", fontSize = 16.sp)
        }
    }
}

@Composable
fun FormularioViagem(
    data: String,
    kmInicial: String,
    kmFinal: String,
    litros: String,
    combustivel: String,
    pedagio: String,
    valor: String,
    onDataChange: (String) -> Unit,
    onKmInicialChange: (String) -> Unit,
    onKmFinalChange: (String) -> Unit,
    onLitrosChange: (String) -> Unit,
    onCombustivelChange: (String) -> Unit,
    onPedagioChange: (String) -> Unit,
    onValorChange: (String) -> Unit,
    onCadastrar: () -> Unit
) {
    CampoViagem(
        value = data,
        onValueChange = onDataChange,
        label = "Data",
        icon = Icons.Default.CalendarMonth
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        CampoViagem(
            value = kmInicial,
            onValueChange = onKmInicialChange,
            label = "Km inicial",
            icon = Icons.Default.Route,
            modifier = Modifier.weight(1f)
        )
        CampoViagem(
            value = kmFinal,
            onValueChange = onKmFinalChange,
            label = "Km final",
            icon = Icons.Default.Route,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        CampoViagem(
            value = litros,
            onValueChange = onLitrosChange,
            label = "Litros",
            icon = Icons.Default.LocalGasStation,
            modifier = Modifier.weight(1f)
        )
        CampoViagem(
            value = combustivel,
            onValueChange = onCombustivelChange,
            label = "Combustível",
            icon = Icons.Default.LocalGasStation,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        CampoViagem(
            value = pedagio,
            onValueChange = onPedagioChange,
            label = "Pedágio",
            icon = Icons.Default.Toll,
            modifier = Modifier.weight(1f)
        )
        CampoViagem(
            value = valor,
            onValueChange = onValorChange,
            label = "Valor combustível",
            icon = Icons.Default.LocalGasStation,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }

    Button(
        onClick = onCadastrar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF1493)
        )
    ) {
        Text("Cadastrar", fontSize = 16.sp)
    }
}

@Composable
fun CampoViagem(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = Color(0xFF9C27B0)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color(0xFFFF1493),
            focusedLabelColor = Color(0xFFFF1493),
            unfocusedIndicatorColor = Color(0xFFCCCCCC)
        )
    )
}

class Viagem(
    val data: String,
    val kmInicial: Double,
    val kmFinal: Double,
    val litros: Double,
    val combustivel: String,
    val pedagio: Double,
    val valor: Double
) {
    fun calcularDistancia(): Double {
        return kmFinal - kmInicial
    }

    fun calcularCusto(): Double {
        return (litros * valor) + pedagio
    }
}

@Preview(showBackground = true)
@Composable
fun ControleDeViagensPreview() {
    ControleDeViagensTheme {
        ControleDeViagens()
    }
}