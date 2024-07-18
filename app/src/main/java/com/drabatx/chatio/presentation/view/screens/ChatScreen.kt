package com.drabatx.chatio.presentation.view.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.drabatx.chatio.data.model.MessageModel
import com.drabatx.chatio.data.model.SenderModel
import com.drabatx.chatio.di.AppConstants.BASE_GOOGLE_MAPS_URL
import com.drabatx.chatio.di.AppConstants.DEFAULT_IMAGE
import com.drabatx.chatio.di.AppConstants.MAPS_LINK
import com.drabatx.chatio.presentation.navigation.AppScreens
import com.drabatx.chatio.presentation.view.dialogs.LoadingDialog
import com.drabatx.chatio.presentation.view.theme.margin_large
import com.drabatx.chatio.presentation.view.theme.margin_medium
import com.drabatx.chatio.presentation.view.theme.margin_small
import com.drabatx.chatio.presentation.view.widgets.MainTopBar
import com.drabatx.chatio.presentation.view.widgets.RoundIconButton
import com.drabatx.chatio.presentation.viewmodels.ChatViewModel
import com.drabatx.chatio.utils.FormatTimestamp
import com.drabatx.chatio.utils.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Preview
@Composable
fun ChatScreenPreview() {
//    ChatScreen(
//        navController = rememberNavController(), // Provide a mock NavController
//        chatViewModel = ChatViewModel()
//    )
}


@Composable
fun ChatScreen(navController: NavController, chatViewModel: ChatViewModel) {
    var loadingState by remember { mutableStateOf(false) }
    val allMessagesState by chatViewModel.getAllMesagesState.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.getAllMessages()
    }
    Scaffold(
        topBar = {
            MainTopBar(onLogoutClick = {
                chatViewModel.logOut()
                loadingState = true
                navController.navigate(AppScreens.LoginScreen.route) {
                    popUpTo(AppScreens.ChatScreen.route) {
                        inclusive = true
                    }
                }
            })
        },
        contentWindowInsets = WindowInsets.safeContent,
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (allMessagesState) {
                        is Result.Loading -> {
                            loadingState = true
                        }

                        is Result.Success -> {
                            loadingState = false
                            val mesages = chatViewModel.messages
                            MessageList(
                                messages = mesages, modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }

                        else -> {}
                    }
                }
                ChatBox(
                    onSendMessage = { message ->
                        chatViewModel.sendMessage(message)
                    },
                    viewModel = chatViewModel,
                    modifier = Modifier
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
                if (loadingState) {
                    LoadingDialog(true)
                }
            }

        })
}

@Preview
@Composable
fun ChatBoxPreview() {
    val messages = listOf(
        MessageModel(
            imageUrl = "",
            sender = "qi50QfP3buXF5O4HxDMJy5GTDL52",
            text = "xfghjj asd asd asdasd asdasd asdad asdasd asdasd asdas dasd asdad asdasd adasd asasd asdadsd adasd asdasdad asdasdasd",
            timestamp = 1721279824787,
            user = SenderModel("qi50QfP3buXF5O4HxDMJy5GTDL52", "email", "Juan Perez", "")
        ),
        MessageModel(
            imageUrl = "",
            sender = "qi50QfP3buXF5O4HxDMJy5GTDL52",
            text = "bbh",
            timestamp = 1721280463396,
            user = SenderModel("qi50QfP3buXF5O4HxDMJy5GTDL52", "email", "Alan Lopez", "")

        ),
        MessageModel(
            imageUrl = "",
            sender = "qi50QfP3buXF5O4HxDMJy5GTDL52",
            text = "lamda asd asdasd asd asdasdas dsadas dasd asdas dasdas asdad asda sdadasd asdasdasd asdasdad asdasd asdasda dad adasdasd asdadasd ads asd",
            timestamp = 1721281828703,
            isThisUser = true,
            user = SenderModel("qi50QfP3buXF5O4HxDMJy5GTDL52", "email", "Marta Diaz", "")
        ),
        MessageModel(
            imageUrl = "",
            sender = "qi50QfP3buXF5O4HxDMJy5GTDL52",
            text = "https://www.google.com/maps/search/?api=1&query=latitude,longitude",
            timestamp = 1721281828703,
            isThisUser = true,
            user = SenderModel("qi50QfP3buXF5O4HxDMJy5GTDL52", "email", "Marta Diaz", "")
        )


    )
    MessageList(messages = messages, modifier = Modifier.fillMaxSize())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageList(messages: List<MessageModel>, modifier: Modifier) {
    val isFirstime = remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        reverseLayout = true,
        verticalArrangement = Arrangement.Bottom
    ) {
        items(messages.size) { index ->
            ChatItem(
                messages[index],
                modifier = Modifier.animateItemPlacement(
                    animationSpec = if (isFirstime.value) tween(
                        durationMillis = 500
                    ) else tween(durationMillis = 0)
                )
            )
            isFirstime.value = false
        }
    }
}

@Composable
private fun ChatItem(messageModel: MessageModel, modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!messageModel.isThisUser) {
            Text(
//                color = MaterialTheme.colorScheme.,
                text = messageModel.user?.name ?: "Usuario",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (!messageModel.isThisUser) Arrangement.Start else Arrangement.End) {
            //imagen
            if (!messageModel.isThisUser) {
                AsyncImage(
                    model = if (messageModel.user?.profileImageUrl == "") DEFAULT_IMAGE else messageModel.user?.profileImageUrl,
                    contentDescription = "imagen de usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(
                            start = if (messageModel.isThisUser) margin_small else 0.dp,
                            end = if (messageModel.isThisUser) 0.dp else margin_small
                        )
                        .height(36.dp)
                        .width(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
            //Globo de chat
            Box(
                modifier = Modifier
                    .padding(
                        start = if (messageModel.isThisUser) margin_large else 0.dp,
                        end = if (!messageModel.isThisUser) margin_large else margin_medium
                    )
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)) // Primero aplicamos el clip
                        .background(if (messageModel.isThisUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer) // Luego el color de fondo
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = if (messageModel.isThisUser) Alignment.End else Alignment.Start) {
                        //Si hay una imagen mostrar
                        if (messageModel.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = messageModel.imageUrl,
                                contentDescription = "imagen compartida",
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        //Si hay texto mostrar el mensaje
                        if (messageModel.text.isNotEmpty()) {
                            if (messageModel.text.contains(BASE_GOOGLE_MAPS_URL)) {
                                val context = LocalContext.current
                                val message = if (messageModel.isThisUser) {
                                    "Ver mi ubicación"
                                } else {
                                    "Ver ubicación"
                                }
                                Column(modifier = Modifier
                                    .clickable {
                                        val intent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(messageModel.text)
                                            )
                                        context.startActivity(intent)
                                    }
                                    .padding(8.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.MyLocation,
                                        contentDescription = "Ubicacion compartida",
                                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                                    )
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            } else {
                                Text(
                                    text = messageModel.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Text(
                            text = FormatTimestamp(messageModel.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(top = margin_small))
    }
}

@Composable
fun ChatBox(modifier: Modifier, onSendMessage: (String) -> Unit, viewModel: ChatViewModel) {
    var text by remember { mutableStateOf("") }
    var uri by remember {
        mutableStateOf<Uri?>(null)
    }

    var location by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scope.launch {
                location = getCurrentLocation(fusedLocationClient)
            }
        }
    }


    val singlePhotPicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            uri = it
            uri?.let { it1 -> viewModel.sendImageMessage(it1) }
        }
    Row(modifier = modifier) {
        ShareLocationButton {
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scope.launch {
                        location = getCurrentLocation(fusedLocationClient)
                        location?.let {
                            viewModel.sendMessage(it)
                        }
                    }
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
        ButtonPickerImage {
            singlePhotPicker.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )

        }
        TextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { text = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
            placeholder = { Text(text = "Mensaje") },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary.copy(1f)
            )
        )
        RoundIconButton(
            modifier = Modifier.padding(start = margin_small),
            onClick = {
                onSendMessage(text)
                text = ""
            },
            icon = {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Enviar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            backgroundColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ButtonPickerImage(onPickImage: () -> Unit) {

    RoundIconButton(
        modifier = Modifier.padding(start = margin_small),
        onClick = {
            onPickImage()
        },
        icon = {
            Icon(
                Icons.Filled.AddPhotoAlternate,
                contentDescription = "Seleccione una imagen",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        backgroundColor = MaterialTheme.colorScheme.primary
    )

}

@Composable
fun ShareLocationButton(onGetLocation: () -> Unit) {

    Column {
        RoundIconButton(
            modifier = Modifier.padding(start = margin_small),
            onClick = {
                onGetLocation()
            },
            icon = {
                Icon(
                    Icons.Filled.AddLocationAlt,
                    contentDescription = "Seleccione una imagen",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            backgroundColor = MaterialTheme.colorScheme.primary
        )
    }
}


@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient): String {
    return suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                val locationString = if (location != null) {
                    MAPS_LINK.plus(location.latitude).plus(",").plus(location.longitude)
                } else {
                    "Ubicación no disponible"
                }
                continuation.resume(locationString) {}
            }
            .addOnFailureListener { exception ->
                continuation.resume("Error: ${exception.message}") {}
            }
    }
}
