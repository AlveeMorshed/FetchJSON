package com.alvee.fetchjson.presentation.screens.registration

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.alvee.fetchjson.R
import com.alvee.fetchjson.utils.filterEmailInput
import com.alvee.fetchjson.utils.filterPasswordInput

@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {},
    onRegistrationSuccess: () -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by registerViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            Toast.makeText(
                context,
                context.getString(R.string.registration_successful_toast_message),
                Toast.LENGTH_SHORT
            )
                .show()
            onRegistrationSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.register_page_title_text),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stringResource(R.string.register_page_subtitle_text),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp, top = 4.dp)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { registerViewModel.updateEmail(it.filterEmailInput()) },
            label = { Text(stringResource(R.string.email_label_text)) },
            placeholder = { Text(stringResource(R.string.email_placeholder_text)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { registerViewModel.updatePassword(it.filterPasswordInput()) },
            label = { Text(stringResource(R.string.password_label_text)) },
            placeholder = { Text(stringResource(R.string.password_placeholder_text)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))


        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { registerViewModel.updateConfirmPassword(it.filterPasswordInput()) },
            label = { Text(stringResource(R.string.confirm_password_label_text)) },
            placeholder = { Text(stringResource(R.string.confirm_password_placeholder_text)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
        )

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = { registerViewModel.register() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6EF5)),
            enabled = !uiState.isLoading && uiState.email.isNotBlank()
                    && uiState.password.isNotBlank()
                    && (uiState.password == uiState.confirmPassword)
        ) {
            Text(
                text = if (uiState.isLoading) stringResource(R.string.registering_text)
                else stringResource(R.string.register_button_text),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        uiState.errorMessage?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.have_an_account_text))
            TextButton(onClick = { onNavigateToLogin() }) {
                Text(stringResource(R.string.go_to_login_page_text))
            }
        }
    }
}
