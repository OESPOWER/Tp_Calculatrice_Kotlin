package com.example.calculatortp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatortp.ui.theme.CalculatorTPTheme

class MainActivity : ComponentActivity() {

    private val vm: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF101010)
                ) {
                    CalculatorScreen(
                        vm = vm,
                        onCopy = { text ->
                            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("result", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(this, "Résultat copié", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(vm: CalculatorViewModel, onCopy: (String) -> Unit) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isPortrait) 12.dp else 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isPortrait) 0.2f else 0.25f)
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
                .clickable { onCopy(vm.display.value) }
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = vm.display.value,
                    color = Color.White,
                    fontSize = if (isPortrait) 36.sp else 28.sp,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
                if (isPortrait) {
                    Text(
                        text = "Appuyez pour copier",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isPortrait) 12.dp else 8.dp))

        Box(modifier = Modifier.weight(0.8f)) {
            if (isPortrait) {
                PortraitButtons(vm, onCopy)
            } else {
                LandscapeButtons(vm, onCopy)
            }
        }
    }
}

@Composable
fun PortraitButtons(vm: CalculatorViewModel, onCopy: (String) -> Unit) {

    val buttons = listOf(
        listOf("C", "⌫", "+/−", "%"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "−"),
        listOf(".", "0", "=", "+")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in buttons) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (btn in row) {
                    CalculatorButton(
                        label = btn,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { handleButtonClick(btn, vm, onCopy) }
                    )
                }
            }
        }
    }
}

@Composable
fun LandscapeButtons(vm: CalculatorViewModel, onCopy: (String) -> Unit) {

    val buttons = listOf(
        listOf("C", "⌫", "+/−", "%", "÷"),
        listOf("7", "8", "9", "×", "−"),
        listOf("4", "5", "6", "+", "="),
        listOf("1", "2", "3", ".", "0")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (row in buttons) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (btn in row) {
                    CalculatorButton(
                        label = btn,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { handleButtonClick(btn, vm, onCopy) },
                        fontSize = 20
                    )
                }
            }
        }
    }
}

fun handleButtonClick(btn: String, vm: CalculatorViewModel, onCopy: (String) -> Unit) {
    when (btn) {
        "C" -> vm.onReset()
        "⌫" -> vm.onBackspace()
        "%" -> vm.onOperation('%')
        "÷" -> vm.onOperation('/')
        "×" -> vm.onOperation('*')
        "−" -> vm.onOperation('-')
        "+" -> vm.onOperation('+')
        "=" -> vm.onEquals()
        "+/−" -> vm.onNegate()
        "." -> { }
        else -> if (btn.length == 1 && btn[0].isDigit()) vm.onDigit(btn[0])
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    fontSize: Int = 22
) {
    val backgroundColor = when {
        label in listOf("+", "−", "×", "÷", "%") -> Color(0xFF4ECDC4)
        label == "=" -> Color(0xFF51CF66)
        label in listOf("C", "⌫") -> Color(0xFFFF6B6B)
        label == "+/−" -> Color(0xFFFFB84D)
        label == "." -> Color(0xFF666666)
        else -> Color(0xFF303030)
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(
            text = label,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium
        )
    }
}