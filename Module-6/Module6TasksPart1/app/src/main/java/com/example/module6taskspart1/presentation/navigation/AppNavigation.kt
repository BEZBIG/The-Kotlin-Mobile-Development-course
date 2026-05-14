package com.example.module6taskspart1.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module6taskspart1.domain.model.Photo
import com.example.module6taskspart1.presentation.detail.PhotoDetailScreen
import com.example.module6taskspart1.presentation.list.PhotoListScreen

object Routes {
    const val PHOTO_LIST = "photo_list"
    const val PHOTO_DETAIL = "photo_detail"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var selectedPhoto: Photo? = null

    NavHost(navController = navController, startDestination = Routes.PHOTO_LIST) {

        composable(Routes.PHOTO_LIST) {
            PhotoListScreen(
                onPhotoClick = { photo ->
                    selectedPhoto = photo
                    navController.navigate(Routes.PHOTO_DETAIL)
                }
            )
        }

        composable(Routes.PHOTO_DETAIL) {
            val photo = selectedPhoto ?: return@composable
            PhotoDetailScreen(
                photo = photo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}