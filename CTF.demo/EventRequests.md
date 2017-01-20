# Creating New Event Requests

<sub>Last Updated: 2017/01/20</sub>

EventRequests are excecuted via EventBus, meaning that the sender and receiver are not linked; this makes it a bit harder to see the data process, but removes a lot of the boilerplate code and makes things much cleaner.

## The Basic Cycle

There are three main components for requests
* fragments/ - Contains the UI portion; necessary methods are specified in interfaces/RoboFragmentContract
* eventRequests/ - The handler that gets the requests via SpiceManager; all listeners are handled through BaseEventRequest
* enums/DataType - specifies enums for Single data types and categories
    * Acts as unique IDs for requests
    * Single types are used within the eventRequests
    * Category types are used within fragments, which specifies which requests are used in their respective fragments

## Creating a New Event Request

1. **Create a new DataType**
<br>
In enums/DataType, specify a new Single enum.
You should also add it to at least one category, assuming that you are using it in a fragment.

1. **Create a new xxxEventRequest that extends BaseEventRequest**
<br>
Firstly, make sure you set all the generics to the proper data type.
For example, NicknameEventRequest deals with Strings, so the type would be String.
When a request is sent, getRequest(String token, @Nullable Object extra) is called.
<br>
We must also change getDataType() to return the proper DataType.Single. This is very important, as it is how fragments differentiate which request is which.
<br>
Every request requires a token, which is used to get data for a specific user.
Some requests also require variables (ie setting nicknames must have the nickname String), which can be added through "extra".
We will work on sending the data later, but assuming that extra contains the desired data,
we may use the getExtra method to easily extract it. <br> **Please use this as it will make it easier to debug, since we need to ensure the object is of the right type** <br>
This method should then return a new instance of xxxRequest
<br>
xxxRequest is a private static inner class extending BaseTepidRequest<T>, where T is the returning data type that matches BaseEventRequest<T>.
The constructor should take in all necessary variables and the super(class), where class is the class of type T.
Those variables should then be saved privately, as they will be used in loadDataFromNetwork();.
In there, we must build a request, get/put depending on what we are doing, and then return the new data.
See existing EventRequests for examples.
<br>
Once the data is received, the EventRequest will asynchronously post the event based on data type, which will be received by all subscribed classes.

1. **Add a subscriber to a fragment**
<br>
If you are creating a new fragment, please look at the existing ones as examples.
The following will tell you how to add new requests.
<br><br>
onLoadEvent(LoadEvent event) <br> is called when a new event is received. <br>
<br>
We first check if it valid using isLoadValid(...); add your DataType.Single here.
<br>
Add a new case in the switch, create a new global variable, and then cast and save event.data to that variable.
**This assumes that the proper type is returned.** So long as the request was done correctly and you are listening to the correct type, this shouldn't be an issue.
<br><br>
updateContent(DataType.Single... types) <br> is called when onLoadEvent returns true; <br>
<br>
Add a new case, and execute the appropriate changes. Note that the global variable(s) may be null, so always check that first.
<br><br>
By default, events are triggered without sending data and filling the extras variable.
You really only send data based on user input, so add a listener to a view and send the data through postEvent(SingleDataEvent event), where the type and data are in the event.
See MyAccountFragment for an example of switching nicknames.
1. **Add changes in RequestActivity**
<br>
In getEventRequest(DataType.Single type), add a new case with the type that returns the new EventRequest you created.
<br>
One of the reasons why this is more responsive than the original implementation is that it saves the data in the activity and reuses it when new fragments are created.
SpiceManager also has caching but it's asynchronous.
<br>
Similar to the fragment, create a new global variable and save it in onLoadEvent. In getLocalData, return that variable where appropriate.