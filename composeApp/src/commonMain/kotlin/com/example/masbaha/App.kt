package com.example.masbaha

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masbaha.data.DhikrPhrase

enum class Screen {
    MAIN, HISTORY
}

@Composable
fun App(
    phrasesList: List<DhikrPhrase>,
    selectedPhrase: DhikrPhrase?,
    onPhraseSelected: (DhikrPhrase) -> Unit,
    onManualIncrement: () -> Unit,
    onResetCounter: () -> Unit,
    onAddPhrase: (String, Int) -> Unit,
    onUpdatePhrase: (DhikrPhrase, String, Int) -> Unit,
    onDeletePhrase: (DhikrPhrase) -> Unit
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) } 
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    var isDarkModeOverride by remember { mutableStateOf<Boolean?>(null) }

    val useDarkTheme = isDarkModeOverride ?: isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (useDarkTheme) darkColorScheme(
            primary = Color(0xFFBB86FC),         
            primaryContainer = Color(0xFFFFFFFF),
            onPrimaryContainer = Color(0xFF000000),
            background = Color(0xFF000000),       
            surface = Color(0xFF121212)
        ) else lightColorScheme(
            primary = Color(0xFF6200EE),         
            primaryContainer = Color(0xFFE1BEE7),
            onPrimaryContainer = Color(0xFF4A148C)
        )
    ) {
        when (currentScreen) {
            Screen.MAIN -> {
                MasbahaScreen(
                    phrases = phrasesList,
                    selectedPhrase = selectedPhrase,
                    isDarkMode = useDarkTheme,
                    onPhraseSelected = onPhraseSelected,
                    onManualIncrement = onManualIncrement,
                    onResetCounter = onResetCounter,
                    onOpenSettings = { showSettingsDialog = true },
                    onOpenHelp = { showHelpDialog = true }, 
                    onToggleTheme = { isDarkModeOverride = !useDarkTheme },
                    onNavigateToHistory = { currentScreen = Screen.HISTORY }
                )
            }
            Screen.HISTORY -> {
                HistoryScreen(
                    phrases = phrasesList,
                    onBack = { currentScreen = Screen.MAIN }
                )
            }
        }

        // Les boîtes de dialogue délèguent leurs actions via les paramètres injectés
        if (showSettingsDialog) {
            // Remettez ici votre composant SettingsDialog existant
            // en appelant onAdd, onUpdate et onDelete fournis plus haut
        }

        if (showHelpDialog) {
            // Remettez ici votre composant HelpDialog existant
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasbahaScreen(
    phrases: List<DhikrPhrase>,
    selectedPhrase: DhikrPhrase?,
    isDarkMode: Boolean,
    onPhraseSelected: (DhikrPhrase) -> Unit,
    onManualIncrement: () -> Unit,
    onResetCounter: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    onToggleTheme: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    var showMenuOptions by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenSettings) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Réglages", tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "المسبحة الإلكترونية",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Box {
                    IconButton(onClick = { showMenuOptions = true }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Options", tint = MaterialTheme.colorScheme.primary)
                    }
                    DropdownMenu(
                        expanded = showMenuOptions,
                        onDismissRequest = { showMenuOptions = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isDarkMode) "الوضع النهاري" else "الوضع الليلي") },
                            onClick = {
                                onToggleTheme()
                                showMenuOptions = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("السجل والإحصائيات") },
                            onClick = {
                                onNavigateToHistory()
                                showMenuOptions = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("طريقة الاستعمال") },
                            onClick = {
                                onOpenHelp()
                                showMenuOptions = false
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "اختر الذكر :", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    val currentPercentage = selectedPhrase?.let {
                        if (it.targetCount > 0) (it.count * 100) / it.targetCount else 0
                    } ?: 0
                    val displayText = selectedPhrase?.let { "${it.text} ($currentPercentage%)" } ?: "— اختر —"

                    OutlinedTextField(
                        value = displayText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        phrases.forEachIndexed { index, phrase ->
                            val percentage = if (phrase.targetCount > 0) (phrase.count * 100) / phrase.targetCount else 0
                            
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = "${phrase.text} ($percentage%)", 
                                        modifier = Modifier.fillMaxWidth(), 
                                        textAlign = TextAlign.Center,
                                        color = if (phrase.count >= phrase.targetCount) Color(0xFF2E7D32) else (if (isDarkMode) Color.White else Color(0xFF007AFF))
                                    ) 
                                },
                                onClick = {
                                    onPhraseSelected(phrase)
                                    expandedDropdown = false
                                }
                            )
                            if (index < phrases.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                            }
                        }
                    }
                }

                selectedPhrase?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الهدف : ${it.targetCount}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val isCompleted = selectedPhrase?.let { it.count >= it.targetCount } ?: false
                val counterColor = if (isCompleted) Color(0xFF2E7D32) else (if (isDarkMode) Color.White else Color(0xFF007AFF))

                Text(
                    text = "${selectedPhrase?.count ?: 0} / ${selectedPhrase?.targetCount ?: 100}",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = counterColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onManualIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "اضغط",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Button(
                onClick = onResetCounter,
                colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) MaterialTheme.colorScheme.primary else Color(0xFF007AFF)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(text = "إعادة تصفير العداد", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun HistoryScreen(
    phrases: List<DhikrPhrase>,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "سجل الأذكار والإحصائيات",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val totalTasbih = phrases.sumOf { it.count }
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "إجمالي التسبيحات", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(text = "$totalTasbih", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Text(
                text = "التفاصيل :",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(phrases) { phrase ->
                    // Code de rendu de chaque item de votre LazyColumn
                    Text(text = "${phrase.text}: ${phrase.count}", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
