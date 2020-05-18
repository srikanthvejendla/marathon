package com.malinskiy.marathon.cli.args

import com.fasterxml.jackson.annotation.JsonProperty
import com.malinskiy.marathon.android.AndroidConfiguration
import com.malinskiy.marathon.android.DEFAULT_INSTALL_OPTIONS
import com.malinskiy.marathon.android.ScreenRecordConfiguration
import com.malinskiy.marathon.android.VendorType
import com.malinskiy.marathon.android.adam.di.adamModule
import com.malinskiy.marathon.android.defaultInitTimeoutMillis
import com.malinskiy.marathon.android.serial.SerialStrategy
import com.malinskiy.marathon.exceptions.ConfigurationException
import ddmlibModule
import java.io.File

data class FileAndroidConfiguration(
    @JsonProperty("vendor") val vendor: VendorType = VendorType.DDMLIB,
    @JsonProperty("androidSdk") val androidSdk: File?,
    @JsonProperty("applicationApk") val applicationOutput: File?,
    @JsonProperty("testApplicationApk") val testApplicationOutput: File,
    @JsonProperty("autoGrantPermission") val autoGrantPermission: Boolean?,
    @JsonProperty("instrumentationArgs") val instrumentationArgs: Map<String, String>?,
    @JsonProperty("applicationPmClear") val applicationPmClear: Boolean?,
    @JsonProperty("testApplicationPmClear") val testApplicationPmClear: Boolean?,
    @JsonProperty("adbInitTimeoutMillis") val adbInitTimeoutMillis: Int?,
    @JsonProperty("installOptions") val installOptions: String?,
    @JsonProperty("serialStrategy") val serialStrategy: SerialStrategy = SerialStrategy.AUTOMATIC,
    @JsonProperty("screenRecordConfiguration") val screenRecordConfiguration: ScreenRecordConfiguration = ScreenRecordConfiguration()
) : FileVendorConfiguration {

    fun toAndroidConfiguration(environmentAndroidSdk: File?): AndroidConfiguration {
        val finalAndroidSdk = androidSdk
            ?: environmentAndroidSdk
            ?: throw ConfigurationException("No android SDK path specified")

        val implementationModules = when (vendor) {
            VendorType.ADAM -> listOf(adamModule)
            VendorType.DDMLIB -> listOf(ddmlibModule)
        }

        return AndroidConfiguration(
            androidSdk = finalAndroidSdk,
            applicationOutput = applicationOutput,
            testApplicationOutput = testApplicationOutput,
            autoGrantPermission = autoGrantPermission ?: false,
            instrumentationArgs = instrumentationArgs ?: emptyMap(),
            applicationPmClear = applicationPmClear ?: false,
            testApplicationPmClear = testApplicationPmClear ?: false,
            adbInitTimeoutMillis = adbInitTimeoutMillis ?: defaultInitTimeoutMillis,
            installOptions = installOptions ?: DEFAULT_INSTALL_OPTIONS,
            serialStrategy = serialStrategy,
            screenRecordConfiguration = screenRecordConfiguration,
            implementationModules = implementationModules
        )
    }
}
