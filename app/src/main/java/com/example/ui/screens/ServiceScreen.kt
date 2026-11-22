package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceChatMessage
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(
    viewModel: DasaniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val serviceMessages by viewModel.serviceMessages.collectAsState()
    var inputMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Service", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF00E676).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF00E676), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online 24/7", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F2624)
                )
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Official Quick Support Channel (Telegram)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1E1D))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Telegram Support Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/GalaxySeederCare"))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0088CC).copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0088CC))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF0088CC), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Official Telegram Care", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("@GalaxySeederCare", color = Color(0xFF0088CC), fontSize = 10.sp)
                        }
                    }
                }
            }

            // Quick Question Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickHelpChip("How to Recharge ₹1 Lakh?") {
                    viewModel.sendServiceMessage("How to recharge ₹1 Lakh?")
                }
                QuickHelpChip("How to Withdraw?") {
                    viewModel.sendServiceMessage("How to withdraw and watch video?")
                }
                QuickHelpChip("Daily Profits") {
                    viewModel.sendServiceMessage("How do daily profits work?")
                }
            }

            // Chat Messages Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(serviceMessages) { msg ->
                    ChatBubble(msg)
                }
            }

            // Message Input Bar
            Surface(
                color = Color(0xFF0F1F1D),
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Ask about recharge, buying, withdrawals...", fontSize = 13.sp, color = Color(0xFF6B7D82)) },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF162A28),
                            unfocusedContainerColor = Color(0xFF162A28),
                            focusedBorderColor = DasaniPrimary,
                            unfocusedBorderColor = DasaniCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("service_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.sendServiceMessage(inputMessage)
                                inputMessage = ""
                            }
                        },
                        modifier = Modifier
                            .background(DasaniPrimary, CircleShape)
                            .size(44.dp)
                            .testTag("send_service_message_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = Color(0xFF041C1A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickHelpChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xFF162C2A), RoundedCornerShape(16.dp))
            .border(1.dp, DasaniPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = DasaniPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ChatBubble(msg: ServiceChatMessage) {
    val isUser = msg.isFromUser
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .background(
                    if (isUser) DasaniPrimary else Color(0xFF162625),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) DasaniPrimary else DasaniCardBorder,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = msg.sender,
                    color = if (isUser) Color(0xFF041C1A) else DasaniPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.message,
                    color = if (isUser) Color(0xFF041C1A) else Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = msg.time,
                    color = if (isUser) Color(0xFF041C1A).copy(alpha = 0.7f) else DasaniTextMuted,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
