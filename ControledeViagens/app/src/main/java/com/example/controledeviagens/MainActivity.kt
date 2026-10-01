package com.example.controledeviagens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controledeviagens.ui.theme.ControleDeViagensTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ControleDeViagensTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    ControleDeViagens(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ControleDeViagens(
    modifier: Modifier = Modifier
) {

    var data by remember {
        mutableStateOf("")
    }

    var kmInicial by remember {
        mutableStateOf("")
    }

    var kmFinal by remember {
        mutableStateOf("")
    }

    var litros by remember {
        mutableStateOf("")
    }

    var combustivel by remember {
        mutableStateOf("")
    }

    var pedagio by remember {
        mutableStateOf("")
    }

    var valor by remember {
        mutableStateOf("")
    }

    val viagens = remember {
        mutableStateListOf<Viagem>()
    }

    var totalGasto by remember {
        mutableStateOf(0.0)
    }

    var totalKm by remember {
        mutableStateOf(0.0)
    }

    var mediaKmLitro by remember {
        mutableStateOf(0.0)
    }

    Column(
        modifier = modifier
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
                bottom = 20.dp
            )
        )

        Text(
            text = "Cadastre uma nova viagem",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF1493),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        TextField(
            value = data,
            onValueChange = {
                data = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Data")
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {

            TextField(
                value = kmInicial,
                onValueChange = {
                    kmInicial = it
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Km inicial")
                }
            )

            TextField(
                value = kmFinal,
                onValueChange = {
                    kmFinal = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                label = {
                    Text("Km final")
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {

            TextField(
                value = litros,
                onValueChange = {
                    litros = it
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Litros")
                }
            )

            TextField(
                value = combustivel,
                onValueChange = {
                    combustivel = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                label = {
                    Text("Combustível")
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {

            TextField(
                value = pedagio,
                onValueChange = {
                    pedagio = it
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Pedágio")
                }
            )

            TextField(
                value = valor,
                onValueChange = {
                    valor = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                label = {
                    Text("Valor combustível")
                }
            )
        }

        Button(
            onClick = {

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

                mediaKmLitro =
                    totalKm / viagens.sumOf {
                        it.litros
                    }

                data = ""
                kmInicial = ""
                kmFinal = ""
                litros = ""
                combustivel = ""
                pedagio = ""
                valor = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF1493)
            )
        ) {
            Text(
                text = "Cadastrar",
                fontSize = 16.sp
            )
        }

        Text(
            text = "Resumo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 8.dp
            )
        )

        Text(
            text = "Total gasto: R$ %.2f".format(totalGasto),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF1493)
        )

        Text(
            text = "Total de km: %.2f km".format(totalKm),
            color = Color(0xFF9C27B0)
        )

        Text(
            text = "Média km/l: %.2f".format(mediaKmLitro),
            color = Color(0xFF9C27B0)
        )

        Text(
            text = "Viagens cadastradas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 8.dp
            )
        )

        for (viagem in viagens) {
            Text(
                text = "${viagem.data} - " +
                        "${viagem.combustivel} - " +
                        "${viagem.calcularDistancia()} km - " +
                        "${viagem.litros} litros - " +
                        "R$ ${viagem.calcularCusto()}",
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.DarkGray
            )
        }
    }
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