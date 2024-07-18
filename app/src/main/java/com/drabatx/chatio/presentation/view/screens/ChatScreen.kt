package com.drabatx.chatio.presentation.view.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.data.model.MessageModel
import com.drabatx.chatio.data.model.SenderModel
import com.drabatx.chatio.di.AppConstants.DEFAULT_IMAGE
import com.drabatx.chatio.presentation.navigation.AppScreens
import com.drabatx.chatio.presentation.view.dialogs.LoadingDialog
import com.drabatx.chatio.presentation.view.theme.margin_small
import com.drabatx.chatio.presentation.view.widgets.MainTopBar
import com.drabatx.chatio.presentation.view.widgets.RoundIconButton
import com.drabatx.chatio.presentation.viewmodels.ChatViewModel
import com.drabatx.chatio.utils.FormatTimestamp
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.launch

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
    var messageText by remember { mutableStateOf("") }
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
                            val mesages =
                                (allMessagesState as Result.Success<ChatModel>).data.messages
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
            text = "xfghjj",
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
            text = "xjcjfu",
            timestamp = 1721281828703,
            isThisUser = true,
            user = SenderModel("qi50QfP3buXF5O4HxDMJy5GTDL52", "email", "Marta Diaz", "")
        )
    )
    MessageList(messages = messages, modifier = Modifier.fillMaxSize())
}

@Composable
fun MessageList(messages: List<MessageModel>, modifier: Modifier) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(state = listState, reverseLayout = true, verticalArrangement = Arrangement.Bottom) {
        items(messages.size) { index ->
            val message = messages[index]
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                if (!message.isThisUser) {
                    Text(
                        text = message.user?.name ?: "Usuario",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                    val (chat, image) = createRefs()
                    //Chat
                    Box(modifier = Modifier.constrainAs(chat) {
                        if (message.isThisUser) {
                            end.linkTo(image.start)
                        } else {
                            start.linkTo(image.end)
                        }
                    }) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp)) // Primero aplicamos el clip
                                .background(if (message.isThisUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary) // Luego el color de fondo
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = if (message.isThisUser) Alignment.End else Alignment.Start) {
                                Text(
                                    text = message.text,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = FormatTimestamp(message.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                    //imagen
                    AsyncImage(
                        model = message.user?.profileImageUrl ?: DEFAULT_IMAGE,
                        contentDescription = "imagen de usuario",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(
                                start = if (message.isThisUser) margin_small else 0.dp,
                                end = if (message.isThisUser) 0.dp else margin_small
                            )
                            .height(36.dp)
                            .width(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .constrainAs(image) {
                                if (message.isThisUser) {
                                    end.linkTo(parent.end)
                                } else {
                                    start.linkTo(parent.start)
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.padding(top = margin_small))
            }
        }
//        coroutineScope.launch {
//            listState.scrollToItem(index = messages.size-1)
//        }
    }
}


@Composable
fun ChatBox(modifier: Modifier, onSendMessage: (String) -> Unit) {
    val rainbowColors = listOf(
        Color.Cyan,
        Color.Green,
        Color.Yellow,
    )

    var text by remember { mutableStateOf("") }
    Row(modifier = modifier) {
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
            icon = { Icon(Icons.Filled.Send, contentDescription = "Enviar") },
            backgroundColor = MaterialTheme.colorScheme.primary
        )
    }
}

