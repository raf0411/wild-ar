package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.ErrorRed
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreenPale
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val borderShape = RoundedCornerShape(16.dp)

    val borderColor = when {
        isError -> ErrorRed
        enabled -> PrimaryGreenPale
        else -> PrimaryGreenPale.copy(alpha = 0.5f)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, fontFamily = PoppinsFont, color = AlmostBlack.copy(.8f), fontSize = 14.sp) },
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = borderColor,
                shape = borderShape
            ),
        enabled = enabled,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        tint = AlmostBlack,
                        imageVector = if (passwordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        }
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreenPale,
            unfocusedBorderColor = PrimaryGreenPale,
            disabledBorderColor = PrimaryGreenPale,
            focusedContainerColor = PastelYellow,
            unfocusedContainerColor = PastelYellow,
            disabledContainerColor = PastelYellow.copy(alpha = 0.5f),
            focusedTextColor = AlmostBlack,
            unfocusedTextColor = AlmostBlack,
            disabledTextColor = AlmostBlack.copy(alpha = 0.5f)
        ),
        shape = borderShape
    )
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Text(
                    text = "Email Field",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Password Field",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldWithContentPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                CustomTextField(
                    value = "user@example.com",
                    onValueChange = {},
                    label = "Email",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = "mySecretPassword123",
                    onValueChange = {},
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldDisabledPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                CustomTextField(
                    value = "user@example.com",
                    onValueChange = {},
                    label = "Email (Disabled)",
                    keyboardType = KeyboardType.Email,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = "mySecretPassword123",
                    onValueChange = {},
                    label = "Password (Disabled)",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    enabled = false
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldErrorPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                CustomTextField(
                    value = "invalid@email",
                    onValueChange = {},
                    label = "Email (Error)",
                    keyboardType = KeyboardType.Email,
                    isError = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = "123",
                    onValueChange = {},
                    label = "Password (Error)",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    isError = true
                )
            }
        }
    }
}

