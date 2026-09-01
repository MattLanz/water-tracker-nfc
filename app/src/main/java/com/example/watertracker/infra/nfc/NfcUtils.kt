package com.example.watertracker.infra.nfc

import android.nfc.Tag

object NfcUtils {
    fun uidFromTag(tag: Tag): String = tag.id.joinToString(separator = "") { "%02x".format(it) }
}
