package com.brewthings.app.data.model.graph

import java.time.Instant

sealed interface DateFormat {
    /**
     * A date formatter for absolute dates.
     *
     * @param date the specific date we're formatting.
     */
    data class Absolute(val date: Instant, val type: Type) : DateFormat {
        enum class Type {
            /**
             * The day before today.
             */
            YESTERDAY,

            /**
             * Between 2 and 6 days ago.
             */
            BETWEEN_TWO_AND_SIX_DAYS_AGO,

            /**
             * More than 6 days ago.
             */
            MORE_THAN_SIX_DAYS_AGO,

            /**
             * Between two and six days later.
             */
            MORE_THAN_TWO_HOURS_LATER,
        }
    }

    /**
     * A date time formatter for relative dates.
     *
     * @param date the specific date we're formatting.
     * @param relativeTo the reference date of [date].
     */
    data class Relative(val date: Instant, val relativeTo: Instant, val type: Type) : DateFormat {
        enum class Type {
            /**
             * Between 3 and 59 minutes ago only if date is still in today.
             */
            MINUTES_AGO,

            /**
             * Between 1 and 24 hours ago only if date is still in today.
             */
            HOURS_AGO,

            /**
             * Between 0 and 59 minutes into the future regardless of if the future date is in today or tomorrow.
             */
            MINUTES_LATER,

            /**
             * Between 1 and 2 hours into the future regardless of if the future date is in today or tomorrow.
             */
            HOURS_LATER,
        }
    }

    /**
     * Certain date representations are custom and not provided by system frameworks.
     */
    data class Constant(val type: Type) : DateFormat {
        enum class Type {
            /**
             * 0 - 3 minutes ago.
             */
            JUST_NOW,

            /**
             * When we aren't able to correctly interpret the date.
             */
            ERROR,
        }
    }
}
