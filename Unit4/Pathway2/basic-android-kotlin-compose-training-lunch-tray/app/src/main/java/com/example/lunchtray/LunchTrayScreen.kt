/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.model.OrderUiState
import com.example.lunchtray.ui.*

// TODO: Screen enum
enum class LunchScreen {
    Start,
    Main,
    Side,
    Accompaniment,
    Result

}
// TODO: AppBar
@Composable
fun LunchAppBar(
    canNavigateBack: Boolean,
    currentScreen:String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

    TopAppBar(
        title = { Text(currentScreen) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
@Composable
fun LunchTrayApp(modifier: Modifier = Modifier) {
    // TODO: Create Controller and initialization

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            LunchAppBar(
                currentScreen = backStackEntry?.destination?.route ?: LunchScreen.Start.name,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = LunchScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = LunchScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(LunchScreen.Main.name)
                    }
                )
            }
            composable(route = LunchScreen.Main.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        navController.popBackStack()
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchScreen.Side.name)
                    },
                    onSelectionChanged = {}
                )
            }
            composable(route = LunchScreen.Side.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onNextButtonClicked = {
                        navController.navigate(LunchScreen.Accompaniment.name)
                    },
                    onCancelButtonClicked = {
                        navController.popBackStack()
                    },
                    onSelectionChanged = {}
                )
            }
            composable(route = LunchScreen.Accompaniment.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onNextButtonClicked = {
                        navController.navigate(LunchScreen.Result.name)
                    },
                    onCancelButtonClicked = {
                        navController.popBackStack()
                    },
                    onSelectionChanged = {}
                )
            }
            composable(route = LunchScreen.Result.name) {
                CheckoutScreen(
                    orderUiState = OrderUiState(
                        entree = DataSource.entreeMenuItems[0],
                        sideDish = DataSource.sideDishMenuItems[0],
                        accompaniment = DataSource.accompanimentMenuItems[0],
                        itemTotalPrice = 15.00,
                        orderTax = 1.00,
                        orderTotalPrice = 16.00
                    ),
                    onNextButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }
        }
    }
}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(LunchScreen.Start.name, inclusive = false)
}

