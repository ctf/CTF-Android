# Creating New Event Requests

<sub>Last Updated: 2017/03/21</sub>

TEPID requests are done with retrofit and Kotlin data classes, which makes things very simple and concise.
The api is in [`CTF/src/main/kotlin/ca.mcgill.science.ctf/api`](https://github.com/CTFMcGill/CTF-Android/tree/master/CTF/src/main/kotlin/ca/mcgill/science/ctf/api) and contains the following notable files:

## Data.kt

This file contains all of the possible Objects we may retrieve from TEPID. Notice that they are all one liners, because Kotlin will generate all the getters and setters. The only requirement is that the names of the values match that in the JSON received from TEPID. You also don't have to add all the values; only put what you need. By default, we'll use val to create immutable types, but var may be used if necessary.

## ITEPID

This file is the interface that is used with retrofit. It is annotated with the appropriate retrieval type, as well as the file paths and potential queries. It is pretty straightfoward; consult the [docs](http://square.github.io/retrofit/) for more information. Make sure that returned data types are wrapped with Call<> to be asynchronous.

## TEPIDAPI

This file combines the interface with the actual client. It is currently a Singleton and does not need any further modifications.

## Using the api

All the fragments come loaded with the boilerplate code needed to load a specific call. Look at the existing fragments for examples. Should you require more calls, simply make your own; those ones are usually minute and only need to be loaded once, but if you need it on a per refresh basis, add it in the refresh listener like the main call.
