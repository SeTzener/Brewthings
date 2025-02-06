package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.brewthings.app.R
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Trend
import com.brewthings.app.ui.converter.toColor
import com.brewthings.app.ui.converter.toFormatPattern
import com.brewthings.app.ui.converter.toIconRes
import com.brewthings.app.ui.converter.toLabel
import com.brewthings.app.ui.converter.toUnit
import com.brewthings.app.ui.theme.BrewthingsTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Composable
fun PillReadingCard(
    dataType: DataType,
    value: Float,
    previousValue: Float?,
) {
    val unit = dataType.toUnit()
    val symbols = DecimalFormatSymbols().apply { percent = 0.toChar() } // Disable %
    val formatter = DecimalFormat(dataType.toFormatPattern(), symbols)
    PillReadingCard(
        backgroundColor = dataType.toColor(),
        textColor = Color.White,
        headerIconRes = dataType.toIconRes(value),
        header = dataType.toLabel(),
        formattedValue = formatter.format(value),
        unit = unit,
        trendIconRes = Trend.get(previousValue, value).toIconRes(),
        footer = previousValue?.let {
            stringResource(R.string.pill_reading_footer, formatter.format(it), unit)
        } ?: ""
    )
}

@Composable
fun PillReadingCard(
    backgroundColor: Color,
    textColor: Color,
    @DrawableRes headerIconRes: Int,
    header: String,
    formattedValue: String,
    unit: String,
    @DrawableRes trendIconRes: Int?,
    footer: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
    ) {
        val verticalPadding = 16.dp
        val horizontalPadding = 16.dp
        val interVertical = 4.dp
        val interHorizontalPadding = 8.dp

        ConstraintLayout(modifier = Modifier.padding(vertical = verticalPadding)) {
            val (headerIconRef, headerRef, valueRef, unitRef, trendIconRef, footerRef) = createRefs()

            Icon(
                modifier = Modifier
                    .constrainAs(headerIconRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(start = 4.dp) // TODO(walt): replace icons, then remove this
                    .size(86.dp),
                painter = painterResource(id = headerIconRes),
                contentDescription = null,
                tint = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(headerRef) {
                        top.linkTo(headerIconRef.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = interVertical,
                    )
                    .fillMaxWidth(),
                text = header,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(valueRef) {
                        top.linkTo(headerRef.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(
                        start = horizontalPadding,
                        top = interVertical,
                    ),
                text = formattedValue,
                style = MaterialTheme.typography.headlineMedium,
                color = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(unitRef) {
                        baseline.linkTo(valueRef.baseline)
                        start.linkTo(valueRef.end)
                    }
                    .padding(start = interHorizontalPadding),
                text = unit,
                style = MaterialTheme.typography.titleSmall,
                color = textColor,
            )

            if (trendIconRes != null) {
                ScalableIcon(
                    modifier = Modifier
                        .constrainAs(trendIconRef) {
                            bottom.linkTo(valueRef.bottom)
                            start.linkTo(unitRef.end)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .padding(
                            start = interHorizontalPadding,
                            top = interVertical,
                            end = horizontalPadding,
                        ),
                    painter = painterResource(id = trendIconRes),
                    contentDescription = null,
                    tint = textColor,
                    maxSize = 36.dp
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(footerRef) {
                        top.linkTo(valueRef.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = interVertical,
                    )
                    .fillMaxWidth(),
                text = footer,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
            )
        }
    }
}

@Preview
@Composable
fun PillReadingCardPreview() {
    val card: LazyGridScope.(DataType, Float, Float?) -> Unit = { dataType, value, previousValue ->
        item {
            PillReadingCard(
                dataType = dataType,
                value = value,
                previousValue = previousValue,
            )
        }
    }

    BrewthingsTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            card(DataType.GRAVITY, 1.056f, 1.060f)
            card(DataType.TEMPERATURE, 18.25f, 18.25f)
            card(DataType.VELOCITY_MEASURED, 1.575f, 0.617f)
            card(DataType.BATTERY, 0.58f, 0.5825f)
        }
    }
}
