package cat.dam.andy.composeevents
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cat.dam.andy.composeevents.ui.theme.ComposeEventsTheme

class MainActivity : ComponentActivity() {
    @OptIn(
        ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
        ExperimentalFoundationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeEventsTheme {
                var info by remember { mutableStateOf("") }
                var textField by rememberSaveable { mutableStateOf("") }
                var sizeState by remember { mutableStateOf("Canvia de mida") }
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                //per amagar el teclat
                val softwareKeyboardController = LocalSoftwareKeyboardController.current
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Yellow)
                        // detectar clics al contenidor
                        .clickable {
                            // Detectar clics
                            info = "Detectat clic"
                        }
                        // detectar canvis de mida contenidor
                        .onGloballyPositioned {
                            // Detectar canvis de mida (layout)
                            sizeState = "Detectada canvi de mida ${it.size}"
                        }
                        // detectar gestos contenidor
                        .pointerInput(Unit) {
                            // Detectar gestos i moviments mostrant informació a la pantalla
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                info =
                                    "Detectat gest:\nCentroid: $centroid\nPan: $pan\nZoom: $zoom\nRotation: $rotation"
                            }
                        }
                ) {
                    // Contingut del Composable
                    Text(text = info)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = sizeState)
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = textField,
                        // detectar canvis en el valor del TextField
                        onValueChange = { newText ->
                            textField = newText
                        },
                        label = { Text(text = "Escriu aquí. Detecció de tecles") },
                        // detectar accions del teclat
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Aquí es pot gestionar l'acció Done
                                info = "Detectada Acció Done"
                                //amaguem el teclat
                                softwareKeyboardController?.hide()
                            }
                        ),
                        modifier = Modifier
                            // detectar tecles premudes
                            .onKeyEvent { keyEvent ->
                                when (keyEvent.key) {
                                    Key.Enter -> {
                                        info = "Detectada tecla: Enter premut"
                                        true
                                    }
                                    Key.Backspace -> {
                                        info = "Detectada tecla: Backspace premut"
                                        true
                                    }
                                    else -> {
                                        // mostra caràcter associat a la tecla premuda
                                        info = "Detectada tecla: ${keyEvent.nativeKeyEvent.unicodeChar.toChar()}"
                                        false
                                    }
                                }
                            }
                            // detectar canvi de focus
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    info = "TextField1 amb focus"
                                } else {
                                    info = "TextField1 sense focus"
                                }
                            }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = "",
                        onValueChange = {},
                        label = { Text(text = "Clica per canviar focus") },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // detectar botó premut
                    Button(onClick = { info = "Detectat botó premut" }
                    ) {
                        Text(text = "Prem")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // detectar diferents  tipus de clics
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Blue)
                            .combinedClickable(
                                onClick = { info = "Detectat clic damunt blau" },
                                onDoubleClick = { info = "Detectat doble clic damunt blau" },
                                onLongClick = { info = "Detectat clic llarg damunt blau" },
                            )
                    ) {
                        Text(
                            text = "Clic\nClic Llarg\nDoble Clic",
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // detectar gestos d'arrossegament
                    Text(text = "Quadre vermell:\nOffset X: $offsetX \nOffset Y: $offsetY")
                    Box(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .graphicsLayer {
                                this.translationX = offsetX
                                this.translationY = offsetY
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                            .size(50.dp)
                            .background(Color.Red)
                    )
                }
            }
        }
    }
}