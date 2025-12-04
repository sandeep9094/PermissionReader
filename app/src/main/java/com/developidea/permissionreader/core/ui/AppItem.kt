package com.developidea.permissionreader.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.developidea.permissionreader.R
import com.developidea.permissionreader.core.model.AppInfo
import com.developidea.permissionreader.core.ui.theme.PermissionReaderTheme

@Composable
fun AppItem(
    app: AppInfo,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            app.icon?.let { drawable ->
                val bitmap = drawable.toBitmap(64, 64)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier.size(48.dp)
                )
            } ?: Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_android),
                    contentDescription = app.name,
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            }

            // App Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Version: ${app.version}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppItemPreview() {
    PermissionReaderTheme {
        AppItem(
            app = AppInfo(
                icon = null,
                name = "Sample App Name",
                packageName = "com.example.sampleapp",
                version = "1.0.0"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppItemPreviewLongText() {
    PermissionReaderTheme {
        AppItem(
            app = AppInfo(
                icon = null,
                name = "Very Long App Name That Should Be Truncated",
                packageName = "com.example.verylongpackagename.that.should.also.be.truncated",
                version = "2.5.10-beta"
            )
        )
    }
}

