package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@Composable
fun AuthScreen(
    viewModel: DasaniViewModel,
    onAuthSuccess: () -> Unit
) {
    val incomingReferralCode by viewModel.incomingReferralCode.collectAsStateWithLifecycle()

    var isRegisterTab by remember { mutableStateOf(true) }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var withdrawalPasswordInput by remember { mutableStateOf("") }
    var inviteCodeInput by remember { mutableStateOf(incomingReferralCode ?: "") }

    LaunchedEffect(incomingReferralCode) {
        incomingReferralCode?.let { code ->
            inviteCodeInput = code
            isRegisterTab = true
        }
    }

    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }
    var withdrawPassVisible by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00D2C4),
                        Color(0xFF0B2B26),
                        Color(0xFF0B1713)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Hero Banner + Overlapping Circle Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.galaxy_banner_top),
                    contentDescription = "Galaxy Manual Seeder Banner",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 46.dp)
                        .size(96.dp)
                        .background(Color(0xFF0B1F17), CircleShape)
                        .border(3.dp, DasaniPrimary, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.galaxy_circle_logo),
                        contentDescription = "Galaxy Seeder Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(54.dp))

            // Main Dark Auth Card matching Screenshot
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF13191C)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Tab Row: Login | Register
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterTab = false }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Login",
                                fontSize = 18.sp,
                                fontWeight = if (!isRegisterTab) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isRegisterTab) Color.White else DasaniTextSecondary
                            )
                            if (!isRegisterTab) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(3.dp)
                                        .background(DasaniPrimary, RoundedCornerShape(2.dp))
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterTab = true }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Register",
                                fontSize = 18.sp,
                                fontWeight = if (isRegisterTab) FontWeight.Bold else FontWeight.Normal,
                                color = if (isRegisterTab) Color.White else DasaniTextSecondary
                            )
                            if (isRegisterTab) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(3.dp)
                                        .background(DasaniPrimary, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // +91 | Enter Phone Number Box (Same as Screenshot)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E2427), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF2B353A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+91",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(22.dp)
                                .background(Color(0xFF3A474D))
                        )
                        TextField(
                            value = phoneInput,
                            onValueChange = { raw ->
                                phoneInput = raw.filter { it.isDigit() }.take(10)
                            },
                            placeholder = {
                                Text("Enter Phone Number", color = Color(0xFF7B8B90), fontSize = 14.sp)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("phone_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input (Underline Dark Style)
                    TextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = {
                            Text(
                                if (isRegisterTab) "Please new password" else "Enter Password",
                                color = Color(0xFF7B8B90),
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Icon(
                                    imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF9EAAB0)
                                )
                            }
                        },
                        visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = DasaniPrimary,
                            unfocusedIndicatorColor = Color(0xFF2B353A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (isRegisterTab) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Confirm Password Input
                        TextField(
                            value = confirmPasswordInput,
                            onValueChange = { confirmPasswordInput = it },
                            placeholder = {
                                Text("Please new password again", color = Color(0xFF7B8B90), fontSize = 14.sp)
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                    Icon(
                                        imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle confirm password",
                                        tint = Color(0xFF9EAAB0)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = DasaniPrimary,
                                unfocusedIndicatorColor = Color(0xFF2B353A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Withdrawal Password Input
                        TextField(
                            value = withdrawalPasswordInput,
                            onValueChange = { withdrawalPasswordInput = it },
                            placeholder = {
                                Text("Enter Withdrawal Password", color = Color(0xFF7B8B90), fontSize = 14.sp)
                            },
                            trailingIcon = {
                                IconButton(onClick = { withdrawPassVisible = !withdrawPassVisible }) {
                                    Icon(
                                        imageVector = if (withdrawPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle withdrawal password",
                                        tint = Color(0xFF9EAAB0)
                                    )
                                }
                            },
                            visualTransformation = if (withdrawPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = DasaniPrimary,
                                unfocusedIndicatorColor = Color(0xFF2B353A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdrawal_password_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Invitation Code Field
                        TextField(
                            value = inviteCodeInput,
                            onValueChange = { inviteCodeInput = it },
                            placeholder = {
                                Text("Invitation Code (Optional)", color = Color(0xFF7B8B90), fontSize = 14.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Invitation Code",
                                    tint = Color(0xFF9EAAB0)
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = DasaniPrimary,
                                unfocusedIndicatorColor = Color(0xFF2B353A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("invite_code_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Terms Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = agreeTerms,
                                onCheckedChange = { agreeTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DasaniPrimary,
                                    uncheckedColor = Color(0xFF6B7D82),
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text(
                                text = "I have agreed to the Terms & Conditions",
                                color = Color(0xFF8CA5A1),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (isRegisterTab) {
                                val res = viewModel.register(
                                    phoneInput = phoneInput,
                                    pass1 = passwordInput,
                                    pass2 = confirmPasswordInput,
                                    withdrawPass = withdrawalPasswordInput,
                                    invite = inviteCodeInput
                                )
                                if (res.first) {
                                    onAuthSuccess()
                                } else {
                                    viewModel.showToast(res.second)
                                }
                            } else {
                                val success = viewModel.login(
                                    phoneInput = phoneInput,
                                    passInput = passwordInput
                                )
                                if (success) onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_action_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF031A18)
                        )
                    ) {
                        Text(
                            text = if (isRegisterTab) "Register" else "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
