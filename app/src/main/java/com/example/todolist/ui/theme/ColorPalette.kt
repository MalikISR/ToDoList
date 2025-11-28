package com.example.todolist.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.todolist.R

@Composable
fun richTextColorPalette(): List<Color> = listOf(
    // gray
    colorResource(R.color.palette_black),
    colorResource(R.color.palette_gray_900),
    colorResource(R.color.palette_gray_700),
    colorResource(R.color.palette_gray_600),
    colorResource(R.color.palette_gray_400),
    colorResource(R.color.palette_gray_300),
    colorResource(R.color.palette_gray_200),
    colorResource(R.color.palette_white),

    // red
    colorResource(R.color.palette_red_900),
    colorResource(R.color.palette_red_700),
    colorResource(R.color.palette_red_500),
    colorResource(R.color.palette_red_200),

    // orange
    colorResource(R.color.palette_orange_700),
    colorResource(R.color.palette_orange_500),
    colorResource(R.color.palette_orange_300),
    colorResource(R.color.palette_orange_100),

    // yellow
    colorResource(R.color.palette_yellow_500),
    colorResource(R.color.palette_yellow_300),
    colorResource(R.color.palette_yellow_100),

    // green
    colorResource(R.color.palette_green_800),
    colorResource(R.color.palette_green_600),
    colorResource(R.color.palette_green_300),
    colorResource(R.color.palette_green_100),

    // lime
    colorResource(R.color.palette_lime_500),
    colorResource(R.color.palette_lime_300),
    colorResource(R.color.palette_lime_100),

    // teal
    colorResource(R.color.palette_teal_800),
    colorResource(R.color.palette_teal_600),
    colorResource(R.color.palette_teal_300),
    colorResource(R.color.palette_teal_100),

    // blue
    colorResource(R.color.palette_blue_900),
    colorResource(R.color.palette_blue_500),
    colorResource(R.color.palette_blue_300),
    colorResource(R.color.palette_blue_100),

    // indigo
    colorResource(R.color.palette_indigo_900),
    colorResource(R.color.palette_indigo_600),
    colorResource(R.color.palette_indigo_300),
    colorResource(R.color.palette_indigo_100),

    // purple
    colorResource(R.color.palette_purple_900),
    colorResource(R.color.palette_purple_600),
    colorResource(R.color.palette_purple_300),
    colorResource(R.color.palette_purple_100),

    // pink
    colorResource(R.color.palette_pink_700),
    colorResource(R.color.palette_pink_500),
    colorResource(R.color.palette_pink_300),
    colorResource(R.color.palette_pink_100),

    // cyan
    colorResource(R.color.palette_cyan_600),
    colorResource(R.color.palette_cyan_400),
    colorResource(R.color.palette_cyan_200),
)
