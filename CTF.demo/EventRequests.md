# Creating New Event Requests

EventRequests are excecuted via EventBus, meaning that the sender and receiver are not linked; this makes it bit harder to see the data process, but removes a lot of the boilerplate code and makes things much cleaner.

## The Basic Cycle

There are three main components for requests
* fragments/ - Contains the UI portion; necessary methods are specified in interfaces/RoboFragmentContract