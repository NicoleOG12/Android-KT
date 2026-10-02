package com.example.listacompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listacompras.ui.theme.ListaComprasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ListaComprasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    ListaCompras(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ListaCompras(
    modifier: Modifier = Modifier
) {
    var nome by remember {
        mutableStateOf("")
    }

    var preco by remember {
        mutableStateOf("")
    }

    var quantidade by remember {
        mutableStateOf("")
    }

    val produtos = remember {
        mutableStateListOf<Produto>()
    }

    var total by remember {
        mutableStateOf(0.0)
    }

    Column(
        modifier = modifier.padding(top = 60.dp)
    ) {
        Text("Lista de Compras")

        Formulario(
            nome = nome,
            preco = preco,
            quantidade = quantidade,
            onNomeChange = {
                nome = it
            },
            onPrecoChange = {
                preco = it
            },
            onQuantidadeChange = {
                quantidade = it
            },
            onCadastrar = {
                val produto = Produto(
                    nome = nome,
                    preco = preco.toDouble(),
                    quantidade = quantidade.toInt()
                )

                produtos.add(produto)
                total += produto.calcularTotal()
            }
        )

        ListaProdutos(
            produtos = produtos
        )

        Resumo(
            total = total
        )
    }
}

@Composable
fun Formulario(
    nome: String,
    preco: String,
    quantidade: String,
    onNomeChange: (String) -> Unit,
    onPrecoChange: (String) -> Unit,
    onQuantidadeChange: (String) -> Unit,
    onCadastrar: () -> Unit
) {
    TextField(
        value = nome,
        onValueChange = onNomeChange,
        label = {
            Text("Nome do produto")
        }
    )

    TextField(
        value = preco,
        onValueChange = onPrecoChange,
        label = {
            Text("Preço")
        }
    )

    TextField(
        value = quantidade,
        onValueChange = onQuantidadeChange,
        label = {
            Text("Quantidade")
        }
    )

    Button(
        onClick = onCadastrar
    ) {
        Text("Cadastrar")
    }
}

@Composable
fun ListaProdutos(
    produtos: List<Produto>
) {
    for (produto in produtos) {
        Text(
            "${produto.nome} - " +
                    "${produto.quantidade} x R$ ${produto.preco} = " +
                    "R$ ${produto.calcularTotal()}"
        )
    }
}

@Composable
fun Resumo(
    total: Double
) {
    Text(
        "Total da compra: R$ $total"
    )
}

class Produto(
    val nome: String,
    val preco: Double,
    val quantidade: Int
) {
    fun calcularTotal(): Double {
        return preco * quantidade
    }
}

@Preview(showBackground = true)
@Composable
fun ListaComprasPreview() {
    ListaComprasTheme {
        ListaCompras()
    }
}
