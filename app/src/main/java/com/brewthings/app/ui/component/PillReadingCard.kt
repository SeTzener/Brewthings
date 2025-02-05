package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun PillReadingCard(
    dataType: DataType,
    value: Float,
    previousValue: Float?,
) {
    val unit = dataType.toUnit()
    val formatter = DecimalFormat(dataType.toFormatPattern())
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
        ConstraintLayout(modifier = Modifier.padding(vertical = 16.dp)) {
            val horizontalPadding = 16.dp
            val interVertical = 4.dp
            val interHorizontalPadding = 8.dp
            val (headerIconRef, headerRef, valueRef, unitRef, trendIconRef, footerRef) = createRefs()

            Icon(
                modifier = Modifier
                    .constrainAs(headerIconRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(96.dp),
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
                    .padding(horizontal = horizontalPadding)
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
                style = MaterialTheme.typography.headlineLarge,
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
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
            )

            if (trendIconRes != null) {
                Icon(
                    modifier = Modifier
                        .constrainAs(trendIconRef) {
                            centerVerticallyTo(valueRef)
                            start.linkTo(unitRef.end)
                        }
                        .padding(
                            start = interHorizontalPadding,
                            end = horizontalPadding,
                        )
                        .size(48.dp),
                    painter = painterResource(id = trendIconRes),
                    contentDescription = null,
                    tint = textColor,
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
    BrewthingsTheme {
        PillReadingCard(
            dataType = DataType.GRAVITY,
            value = 1.056f,
            previousValue = 1.060f,
        )
    }
}
