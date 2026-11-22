package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppLinkConfig
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    viewModel: DasaniViewModel
) {
    val context = LocalContext.current
    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val inviteLink = remember(userAccount.inviteCode) {
        AppLinkConfig.buildShareLink(userAccount.inviteCode)
    }
    val appDeepLink = remember(userAccount.inviteCode) {
        AppLinkConfig.buildDeepLink(userAccount.inviteCode)
    }
    val scrollState = rememberScrollState()

    fun copyText(label: String, value: String, toastText: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
        viewModel.showToast(toastText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invite & Share", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2624))
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Invite QR / Gift Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132826))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(DasaniPrimary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Referral Reward Gift",
                            tint = DasaniPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Invite Friends & Earn Big Commission",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Share your live working app link and earn up to 23% in cumulative team bonuses!",
                        color = DasaniTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // QR Visual
                    Box(
                        modifier = Modifier
                            .size(132.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Invite QR Code",
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Invite Code Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1B3532), RoundedCornerShape(10.dp))
                            .border(1.dp, DasaniPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Invitation Code", color = DasaniTextSecondary, fontSize = 11.sp)
                            Text(
                                text = AppLinkConfig.sanitizeCode(userAccount.inviteCode),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                copyText(
                                    label = "Invitation Code",
                                    value = AppLinkConfig.sanitizeCode(userAccount.inviteCode),
                                    toastText = "Invitation code copied!"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DasaniPrimary,
                                contentColor = Color(0xFF041C1A)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("copy_invite_code_button")
                        ) {
                            Text("Copy Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Working Web App Link Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1B3532), RoundedCornerShape(10.dp))
                            .border(1.dp, DasaniPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = DasaniPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Working App Link (Live)",
                                color = DasaniTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = inviteLink,
                            color = DasaniPrimary,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    copyText(
                                        label = "Working App Link",
                                        value = inviteLink,
                                        toastText = "Working app link copied!"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DasaniPrimary,
                                    contentColor = Color(0xFF041C1A)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("copy_invite_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val openIntent = Intent(Intent.ACTION_VIEW, Uri.parse(inviteLink))
                                        context.startActivity(openIntent)
                                    } catch (_: Exception) {
                                        viewModel.showToast("Unable to open browser for link")
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, DasaniPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DasaniPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("open_working_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Direct App Deep Link Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF152927), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF284D49), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Direct App Scheme Link", color = DasaniTextSecondary, fontSize = 11.sp)
                            Text(
                                text = appDeepLink,
                                color = Color(0xFF8BECE4),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        TextButton(
                            onClick = {
                                copyText(
                                    label = "Direct App Link",
                                    value = appDeepLink,
                                    toastText = "Direct app link copied!"
                                )
                            },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("copy_deep_link_button")
                        ) {
                            Text("Copy", color = DasaniPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Share Sheet Action
                    Button(
                        onClick = {
                            try {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Galaxy Manual Seeder App Link")
                                    putExtra(Intent.EXTRA_TEXT, AppLinkConfig.buildShareMessage(userAccount.inviteCode))
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Working App Link"))
                            } catch (_: Exception) {
                                copyText(
                                    label = "Working App Link",
                                    value = inviteLink,
                                    toastText = "Working app link copied!"
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5D4),
                            contentColor = Color(0xFF041C1A)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("share_working_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Working App Link",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Commission Structure
            Text(
                text = "Three-Tier Commission Rewards",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            CommissionTierCard("Level 1 Tier", "20% Direct Commission", "When friends you invite recharge, you earn 20% instantly.", DasaniPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            CommissionTierCard("Level 2 Tier", "2% Second Tier Commission", "When your friends invite new users, you earn 2% on their recharges.", Color(0xFF00B4D8))
            Spacer(modifier = Modifier.height(8.dp))
            CommissionTierCard("Level 3 Tier", "1% Third Tier Commission", "Earn 1% from third-generation member purchases automatically.", Color(0xFF009688))
        }
    }
}

@Composable
fun CommissionTierCard(title: String, bonus: String, desc: String, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Percent, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(bonus, color = accentColor, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = DasaniTextSecondary, fontSize = 11.sp)
            }
        }
    }
}
