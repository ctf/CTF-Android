package ca.mcgill.science.ctf

import ca.mcgill.science.ctf.enums.DataType

/**
 * Created by Allan Wang on 14/02/2017.
 *
 * All the possible Events that can be sent through EventBus
 */

class Events {

    /**
     * Creates event wrapping [DataType.Category]
     */
    data class CategoryDataEvent(val type: DataType.Category, val forceReload: Boolean = false, val extra: Any?) {
        constructor(type: DataType.Category, extra: Any?) : this(type, false, extra)
        constructor(type: DataType.Category) : this(type, false, null)
    }

    /**
     * Creates event wrapping [DataType.Single]
     */
    data class SingleDataEvent(val type: DataType.Single, val forceReload: Boolean = false, val extra: Any?) {
        constructor(type: DataType.Single, extra: Any?) : this(type, false, extra)
        constructor(type: DataType.Single) : this(type, false, null)
    }

    /**
     * Load event for [DataType.Single]
     */
    data class LoadEvent(val type: DataType.Single, val isSuccessful: Boolean, val data: Any?) {
        var isActivityOnly = false
            private set
        var isFragmentOnly = false
            private set

        fun activityOnly(): LoadEvent {
            isActivityOnly = true
            isFragmentOnly = false
            return this
        }

        fun fragmentOnly(): LoadEvent {
            isActivityOnly = false
            isFragmentOnly = true
            return this
        }
    }

}
