package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppLinkConfig
import com.example.model.TeamMember
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    viewModel: DasaniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userAccount by viewModel.userAccount.collectAsState()
    val teamMembers by viewModel.teamMembers.collectAsState()
    val currentLevel by viewModel.currentTeamLevel.collectAsState()

    var showInviteDialog by remember { mutableStateOf(false) }

    val levelMembers = remember(teamMembers, currentLevel) {
        teamMembers.filter { it.level == currentLevel }
    }

    val totalTeamRecharge = remember(teamMembers) {
        teamMembers.sumOf { it.recharge }
    }

    val inviteLink = remember(userAccount.inviteCode) {
        AppLinkConfig.buildShareLink(userAccount.inviteCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Team LV.$currentLevel",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF041C1A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF041C1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DasaniPrimary // Vibrant teal header matching Screenshot 3
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Team Stats Card (Matching Screenshot 3)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF14191C) // Deep dark card from screenshot 3
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Glowing Dasani Logo on Left
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, DasaniPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.galaxy_circle_logo),
                                    contentDescription = "Galaxy Seeder Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            // Recharge and Team Size Stats on Right
                            Column(modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Team Recharge",
                                        color = DasaniTextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "₹ ${totalTeamRecharge.toLong()}",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                HorizontalDivider(
                                    color = Color(0xFF263238),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Team size",
                                        color = DasaniTextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${teamMembers.size}",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Level Tabs: LV:1 (20%) | LV:2 (2%) | LV:3 (1%) (Matching Screenshot 3)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF00D2C4), RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LevelTabPill(
                            level = 1,
                            percentage = "20%",
                            isSelected = currentLevel == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTeamLevel(1) }
                        )

                        LevelTabPill(
                            level = 2,
                            percentage = "2%",
                            isSelected = currentLevel == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTeamLevel(2) }
                        )

                        LevelTabPill(
                            level = 3,
                            percentage = "1%",
                            isSelected = currentLevel == 3,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTeamLevel(3) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Content Card (Matching Screenshot 3)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF14191C)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (levelMembers.isEmpty()) {
                                // Empty state icon (Cyan circle silhouette from screenshot 3)
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Color(0xFF00D2C4).copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Group,
                                        contentDescription = null,
                                        tint = Color(0xFF00D2C4),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "No LV.$currentLevel members yet",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Share your invite link to grow this level.",
                                    color = DasaniTextSecondary,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // "Invite friends" Button (Matching Screenshot 3)
                                Button(
                                    onClick = { showInviteDialog = true },
                                    modifier = Modifier
                                        .width(220.dp)
                                        .height(46.dp)
                                        .testTag("invite_friends_button"),
                                    shape = RoundedCornerShape(23.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00D2C4),
                                        contentColor = Color(0xFF041C1A)
                                    )
                                ) {
                                    Text(
                                        text = "Invite friends",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // Members List
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    levelMembers.forEach { member ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF192226), RoundedCornerShape(10.dp))
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = DasaniPrimary)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text("Partner ${member.memberId.ifBlank { "Member" }}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(member.date, color = DasaniTextMuted, fontSize = 10.sp)
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Recharge: ₹${member.recharge.toLong()}", color = Color.White, fontSize = 12.sp)
                                                Text("Bonus: +₹${member.commissionEarned.toLong()}", color = DasaniSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { showInviteDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A))
                                    ) {
                                        Text("Invite More Friends", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Invite Friends Dialog (QR & Copy Link)
        if (showInviteDialog) {
            AlertDialog(
                onDismissRequest = { showInviteDialog = false },
                containerColor = Color(0xFF13191C),
                title = {
                    Text("Invite Friends & Earn Commission", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Level 1: 20% Direct Commission\nLevel 2: 2% Commission\nLevel 3: 1% Commission",
                            color = DasaniPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        HorizontalDivider(color = Color(0xFF263238))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E282C), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Your Invite Code", color = DasaniTextSecondary, fontSize = 11.sp)
                                Text(userAccount.inviteCode, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Invite Code", userAccount.inviteCode))
                                    viewModel.showToast("Invitation code copied!")
                                },
                                modifier = Modifier.testTag("team_copy_code_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", tint = DasaniPrimary)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E282C), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Working App Link", color = DasaniTextSecondary, fontSize = 11.sp)
                                Text(inviteLink, color = DasaniPrimary, fontSize = 12.sp, maxLines = 2)
                            }
                            IconButton(
                                onClick = {
                                    try {
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, "Galaxy Manual Seeder App Link")
                                            putExtra(Intent.EXTRA_TEXT, AppLinkConfig.buildShareMessage(userAccount.inviteCode))
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Working App Link"))
                                    } catch (_: Exception) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Invite Link", inviteLink))
                                        viewModel.showToast("Working app link copied!")
                                    }
                                },
                                modifier = Modifier.testTag("team_share_link_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share working app link", tint = DasaniPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Invite Link", inviteLink))
                            viewModel.showToast("Working app link copied!")
                            showInviteDialog = false
                        },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("team_copy_link_close_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A))
                    ) {
                        Text("Copy Link & Close", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun LevelTabPill(
    level: Int,
    percentage: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .background(
                if (isSelected) Color(0xFF13191C) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LV:$level",
                color = if (isSelected) Color.White else Color(0xFF041C1A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Income $percentage",
                color = if (isSelected) Color(0xFF00D2C4) else Color(0xFF041C1A),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
