# FireExpense

[![MVVM ](https://img.shields.io/badge/Architecture-MVVM-brightgreen)](https://devexperto.com/mvvm-vs-mvp/)  [![Android-Navigation component ](https://img.shields.io/badge/Android-Navigation&nbsp;component-blue)](https://developer.android.com/guide/navigation/navigation-getting-started) [![coroutines](https://img.shields.io/badge/Coroutines-Flow-red)](https://medium.com/kotlin-en-android/manejo-de-eventos-con-coroutines-creaci%C3%B3n-de-flows-con-callbackflow-y-channelflow-1d32d07a9cdd)  [![firebase ](https://img.shields.io/badge/Firebase-Firestore-purple.svg)](https://firebase.google.com/docs/firestore?hl=es) [![mpandroidchart ](https://img.shields.io/badge/Android-MPAndroidChart-cyan.svg)](https://github.com/PhilJay/MPAndroidChart)

Android application that allows registering personal expenses by saving them in Firestore, after user authentication. 


### MVVM / Repository pattern example with LiveData, Flow and using Firestore

------------

This application serves as a template to learn MVVM architecture with Repository pattern, Livedata, Coroutines / Flow and Fragment with Navigation component (DrawerLayout), being useful for beginners in Kotlin / Android who are learning Clean Architecture.

FireExpense is an application to control personal expenses that are stored in the cloud. The application retrieves / saves the necessary data from Firestore, which is a non-relational database. Developers will need to add the app to one of their Firebase projects. The application encrypts the data so that the database administrators cannot read it. It allows the usual operations of a database: read, add, update and delete. Data can be filtered by year and / or month. Finally, you can display the data as a line chart (making use of the MPAndroidChart library). To enter the application, the user must authenticate (with email and password) or register with an account, for which they must also provide a username and an avatar.

------------
***Device screenshot:***

<img src="https://user-images.githubusercontent.com/68773736/96260858-e4feb500-0fbf-11eb-8af2-e8c3758ff9c5.png" width="200"/> <img src="https://user-images.githubusercontent.com/68773736/96261443-c2b96700-0fc0-11eb-97bc-90913e685d68.png" width="200"/> <img src="https://user-images.githubusercontent.com/68773736/96261622-f85e5000-0fc0-11eb-9d14-14c7888a03b4.png" width="200"/>

<img src="https://user-images.githubusercontent.com/68773736/96261821-36f40a80-0fc1-11eb-873b-758dd625737d.png" width="200"/> <img src="https://user-images.githubusercontent.com/68773736/96262086-98b47480-0fc1-11eb-901f-f2c8f512845c.png" width="450"/>
