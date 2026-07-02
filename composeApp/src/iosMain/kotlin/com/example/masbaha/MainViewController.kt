package com.example.masbaha

import androidx.compose.ui.window.ComposeUIViewController
import com.example.masbaha.data.DhikrPhrase
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    // 💡 Pour le build iOS initial, nous passons des données fictives/vides
    // car votre logique de base de données Room actuelle reste confinée sur Android.
    App(
        phrasesList = emptyList(),
        selectedPhrase = null,
        onPhraseSelected = {},
        onManualIncrement = {},
        onResetCounter = {},
        onAddPhrase = { _, _ -> },
        onUpdatePhrase = { _, _, _ -> },
        onDeletePhrase = {}
    )
}
