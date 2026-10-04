package com.convx.desktop.db

import java.io.File

object StoragePaths {
    val appDataDir: File by lazy {
        val localAppData = System.getenv("LOCALAPPDATA")
            ?: (System.getProperty("user.home") + "/AppData/Local")
        File(localAppData, "Convx").apply { mkdirs() }
    }

    val databaseFile: File by lazy {
        File(appDataDir, "convx.db")
    }

    val preferencesFile: File by lazy {
        File(appDataDir, "settings.preferences_pb")
    }
}
