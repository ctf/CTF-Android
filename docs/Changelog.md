# Changelog

## v1.5
* Made Changelog display automatically on version update
* Kept Room fragment active on tab switch
* Added splash screen when loading
* Big code cleanup

## v1.4
* Added Room Info
* Made PrintJob list display errors & refunds
* Made PrintJob dialog display print user, room, and status

## v1.3
* Fixed fragment IllegalStateException
* Improved user search results
* Created refund option
* Reformatted account fragment

## v1.2
* Add prefilled ticket submissions

## v1.1
* Completely refactored and rewrote fragments
* Switched to retrofit and reactive programming
* Added caching
* Added Tepid interface in Kotlin
* Simplified request objects
* Integrated SwipeRecyclerView
* Improved recycler animations
* Add option to enable and disable printers
* Created automated apk builds (see README)
* Added auto refresh after data change
* Added silent refreshing after data change

## v1.0_beta2
* Added adapter animations
* Added support for kitkat
* Added SwipeRefreshLayouts to views that need to load data
* Created RequestActivity to manage all requests asynchronously
* Made data parcelable
* Removed Spice caching in favour of RequestActivity data
* Added Crashlytics logging

## v1.0_beta1
* Cleaned up MainActivity and Fragments
* Integrated Capsule Framework, EventBus, Butterknife, and Material Drawer
* Added themes and materialized
* Changing settings will reload and bring you back to the same window
