package com.alvee.fetchjson.presentation.screens.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.alvee.fetchjson.R
import com.alvee.fetchjson.presentation.screens.Screens
import com.alvee.fetchjson.ui.theme.RedFirebrick

@Composable
fun AccountScreen(
    accountScreenViewModel: AccountScreenViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val uiState by accountScreenViewModel.uiState.collectAsState()
    BackHandler { }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.email_text),
                )
                Text(
                    text = uiState.email,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.user_id_text),
                )
                Text(
                    text = uiState.userId.toString(),
                )
            }

            Button(
                onClick = {
                    accountScreenViewModel.logOutUser()
                    navHostController.navigate(Screens.LoginScreen.route) {
                        popUpTo(Screens.PostFeedScreen.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(.8f),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = RedFirebrick
                ),
                shape = RoundedCornerShape(corner = CornerSize(8.dp))
            ) {
                Text(
                    text = stringResource(R.string.logout_button_text),
                    color = Color.White
                )
            }
        }
    }
}
