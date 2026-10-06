package com.example.listycity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listycity.ui.theme.ListyCityTheme

@Composable
fun CityListScreen(
    cities: List<City>,
    onAddCity: (City) -> Unit,
    onUpdateCity: (City, City) -> Unit,
    onDeleteCity: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCityName by remember { mutableStateOf("") }
    var newProvinceName by remember { mutableStateOf("") }
    var showAddCityFields by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var editedCityName by remember { mutableStateOf("") }
    var editedProvinceName by remember { mutableStateOf("") }

    //adding deleteMode and the popup dialog
    var deleteMode by remember { mutableStateOf(false) }
    var popupDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    // when the add button is clicked, cancel delete
                    deleteMode = false
                    showAddCityFields = !showAddCityFields
                    if (showAddCityFields) {
                        selectedCity = null
                        editedCityName = ""
                        editedProvinceName = ""
                    }
                }
            ) {
                Text("+")
            }

        }
        if (showAddCityFields) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = newCityName,
                    onValueChange = { newCityName = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = newProvinceName,
                    onValueChange = { newProvinceName = it },
                    label = { Text("Province") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = {
                        if (newCityName.isNotBlank() && newProvinceName.isNotBlank()) {
                            onAddCity(
                                City(
                                    name = newCityName,
                                    province = newProvinceName
                                )
                            )

                            newCityName = ""
                            newProvinceName = ""
                            showAddCityFields = false
                        }
                    }
                ) {
                    Text("ADD CITY")
                }
            }
        }
        if (selectedCity != null && !deleteMode && cities.isNotEmpty()) {  //added delete functionality
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = editedCityName,
                    onValueChange = { editedCityName = it },
                    label = { Text("Updated City") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = editedProvinceName,
                    onValueChange = { editedProvinceName = it },
                    label = { Text("Updated Province") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = {
                        val cityToUpdate = selectedCity
                        if (
                            cityToUpdate != null &&
                            editedCityName.isNotBlank() &&
                            editedProvinceName.isNotBlank()
                        ) {
                            onUpdateCity(
                                cityToUpdate,
                                City(
                                    name = editedCityName,
                                    province = editedProvinceName
                                )
                            )

                            selectedCity = null
                            editedCityName = ""
                            editedProvinceName = ""
                        }
                    }
                ) {
                    Text("UPDATE CITY")
                }
            }
        }
        LazyColumn(modifier = Modifier.weight(1f)) {  //modified for delete btn at bottom
            itemsIndexed(cities) { index, city ->
                CityRow(
                    city = city,
                    onClick = {
                        if (deleteMode) {
                            selectedCity = city
                            popupDialog = true
                        } else {
                            showAddCityFields = false
                            newCityName = ""
                            newProvinceName = ""
                            selectedCity = city
                            editedCityName = city.name
                            editedProvinceName = city.province
                        }
                    }
                )
                if (index < cities.lastIndex) {
                    HorizontalDivider()
                }
            }
        }

        if (popupDialog) {
            AlertDialog(
                onDismissRequest = { popupDialog = false },
                title = { Text("Delete City?") },
                text = { Text("Delete ${selectedCity?.name}, ${selectedCity?.province}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val cityToDel = selectedCity

                            if (cityToDel != null) {
                                //turns off delete mode when last city is deleted
                                if (cities.size == 1) {
                                    deleteMode = false
                                }
                                onDeleteCity(cityToDel)
                                popupDialog = false
                            }
                        }
                    ) {
                        Text("DELETE")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            popupDialog = false
                        }
                    ) {
                        Text("CANCEL")
                    }
                }
            )
        }

        // adding delete button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    deleteMode = !deleteMode

                    // if in delete mode, don't show the text field
                    if (deleteMode) {
                        showAddCityFields = false
                    }

                    // if no cities, delete mode is off
                    if (cities.isEmpty()) {
                        deleteMode = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    if (deleteMode) {
                        "CANCEL DELETE"
                    } else {
                        "DELETE CITY"
                    }
                )
            }
        }
    }
}


@Composable
fun CityRow(
    city: City,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Text(
            text = city.name,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = city.province,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CityListScreenPreview() {
    ListyCityTheme {
        CityListScreen(
            cities = listOf(
                City("Edmonton", "AB"),
                City("Vancouver", "BC"),
                City("Calgary", "AB")
            ),
            onAddCity = {},
            onUpdateCity = { _, _ -> },
            onDeleteCity = {}  // added
        )
    }
}